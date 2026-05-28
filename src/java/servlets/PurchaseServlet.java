package servlets;

import database.DBConnection;
import util.RoleUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

    private boolean isEmployee(HttpServletRequest req) {
        return RoleUtil.isEmployee((String) req.getSession().getAttribute("role"));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isEmployee(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        List<Map<String, Object>> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM purchase_requests ORDER BY id DESC");
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("request_date", rs.getDate("request_date"));
                row.put("status", rs.getString("status"));
                row.put("total_items", rs.getInt("total_items"));
                requests.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("requests", requests);
        req.getRequestDispatcher("/purchase_requests.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isEmployee(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String action = req.getParameter("action");
        String cp = req.getContextPath();
        try (Connection conn = DBConnection.getConnection()) {
            if ("create".equals(action)) {
                // упрощённо: только одна книга в заявке для демонстрации
                String title = req.getParameter("book_title");
                String author = req.getParameter("author");
                String isbn = req.getParameter("isbn");
                int year = Integer.parseInt(req.getParameter("year"));
                String publisher = req.getParameter("publisher");
                int qty = Integer.parseInt(req.getParameter("quantity"));
                conn.setAutoCommit(false);
                PreparedStatement insReq = conn.prepareStatement(
                        "INSERT INTO purchase_requests (request_date, status, total_items) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                insReq.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                insReq.setString(2, "NEW");
                insReq.setInt(3, qty);
                insReq.executeUpdate();
                ResultSet keys = insReq.getGeneratedKeys();
                keys.next();
                int reqId = keys.getInt(1);
                PreparedStatement insItem = conn.prepareStatement(
                        "INSERT INTO purchase_items (request_id, book_title, author, isbn, year, publisher, quantity) VALUES (?,?,?,?,?,?,?)"
                );
                insItem.setInt(1, reqId);
                insItem.setString(2, title);
                insItem.setString(3, author);
                insItem.setString(4, isbn);
                insItem.setInt(5, year);
                insItem.setString(6, publisher);
                insItem.setInt(7, qty);
                insItem.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            } else if ("receive".equals(action)) {
                int reqId = Integer.parseInt(req.getParameter("id"));
                // получить позиции и добавить книги в фонд
                PreparedStatement sel = conn.prepareStatement(
                        "SELECT * FROM purchase_items WHERE request_id=?"
                );
                sel.setInt(1, reqId);
                ResultSet rs = sel.executeQuery();
                conn.setAutoCommit(false);
                while (rs.next()) {
                    String title = rs.getString("book_title");
                    String author = rs.getString("author");
                    String isbn = rs.getString("isbn");
                    int year = rs.getInt("year");
                    String publisher = rs.getString("publisher");
                    int qty = rs.getInt("quantity");
                    // проверить, есть ли уже такая книга
                    PreparedStatement exist = conn.prepareStatement(
                            "SELECT id, quantity, available FROM books WHERE title=? AND author=? AND isbn=?"
                    );
                    exist.setString(1, title);
                    exist.setString(2, author);
                    exist.setString(3, isbn);
                    ResultSet ex = exist.executeQuery();
                    if (ex.next()) {
                        int id = ex.getInt("id");
                        int oldQty = ex.getInt("quantity");
                        int oldAvail = ex.getInt("available");
                        PreparedStatement upd = conn.prepareStatement(
                                "UPDATE books SET quantity=?, available=? WHERE id=?"
                        );
                        upd.setInt(1, oldQty + qty);
                        upd.setInt(2, oldAvail + qty);
                        upd.setInt(3, id);
                        upd.executeUpdate();
                    } else {
                        PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO books (title, author, isbn, year, publisher, quantity, available) VALUES (?,?,?,?,?,?,?)"
                        );
                        ins.setString(1, title);
                        ins.setString(2, author);
                        ins.setString(3, isbn);
                        ins.setInt(4, year);
                        ins.setString(5, publisher);
                        ins.setInt(6, qty);
                        ins.setInt(7, qty);
                        ins.executeUpdate();
                    }
                }
                PreparedStatement updReq = conn.prepareStatement(
                        "UPDATE purchase_requests SET status='RECEIVED' WHERE id=?"
                );
                updReq.setInt(1, reqId);
                updReq.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(cp + "/PurchaseServlet");
    }
}
