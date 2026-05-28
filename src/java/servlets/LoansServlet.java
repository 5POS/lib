package servlets;

import database.DBConnection;
import util.RoleUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/LoansServlet")
public class LoansServlet extends HttpServlet {

    private boolean isEmployee(HttpServletRequest req) {
        return RoleUtil.isEmployee((String) req.getSession().getAttribute("role"));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isEmployee(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        List<Map<String, Object>> loans = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT l.*, b.title AS book_title, r.full_name AS reader_name "
                    + "FROM loans l JOIN books b ON l.book_id=b.id "
                    + "JOIN readers r ON l.reader_id=r.id ORDER BY l.id DESC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("book_title", rs.getString("book_title"));
                row.put("reader_name", rs.getString("reader_name"));
                row.put("loan_date", rs.getDate("loan_date"));
                row.put("due_date", rs.getDate("due_date"));
                row.put("return_date", rs.getDate("return_date"));
                row.put("status", rs.getString("status"));
                loans.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("loans", loans);
        req.getRequestDispatcher("/loans.jsp").forward(req, resp);
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
            if ("issue".equals(action)) {
                int bookId = Integer.parseInt(req.getParameter("book_id"));
                int readerId = Integer.parseInt(req.getParameter("reader_id"));
                // проверим доступность
                PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
                check.setInt(1, bookId);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt("available") > 0) {
                    // выдача
                    java.sql.Date loanDate = new java.sql.Date(System.currentTimeMillis());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(loanDate);
                    cal.add(Calendar.DAY_OF_MONTH, 14);
                    java.sql.Date dueDate = new java.sql.Date(cal.getTimeInMillis());
                    PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO loans (book_id, reader_id, loan_date, due_date, status) VALUES (?,?,?,?,?)"
                    );
                    ins.setInt(1, bookId);
                    ins.setInt(2, readerId);
                    ins.setDate(3, loanDate);
                    ins.setDate(4, dueDate);
                    ins.setString(5, "ACTIVE");
                    ins.executeUpdate();
                    // уменьшить available
                    PreparedStatement upd = conn.prepareStatement("UPDATE books SET available = available-1 WHERE id=?");
                    upd.setInt(1, bookId);
                    upd.executeUpdate();
                }
            } else if ("return".equals(action)) {
                int loanId = Integer.parseInt(req.getParameter("loan_id"));
                // получить book_id
                PreparedStatement sel = conn.prepareStatement("SELECT book_id FROM loans WHERE id=?");
                sel.setInt(1, loanId);
                ResultSet rs = sel.executeQuery();
                if (rs.next()) {
                    int bookId = rs.getInt("book_id");
                    PreparedStatement upd = conn.prepareStatement(
                            "UPDATE loans SET return_date=?, status='RETURNED' WHERE id=?"
                    );
                    upd.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                    upd.setInt(2, loanId);
                    upd.executeUpdate();
                    PreparedStatement inc = conn.prepareStatement("UPDATE books SET available = available+1 WHERE id=?");
                    inc.setInt(1, bookId);
                    inc.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(cp + "/LoansServlet");
    }
}
