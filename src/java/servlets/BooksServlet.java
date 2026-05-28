package servlets;

import database.DBConnection;
import util.RoleUtil;
import models.Book;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/BooksServlet")
public class BooksServlet extends HttpServlet {

    private boolean isEmployee(HttpServletRequest req) {
        String role = (String) req.getSession().getAttribute("role");
        return RoleUtil.isEmployee(role);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isEmployee(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        List<Book> books = new ArrayList<>();
        String search = req.getParameter("search");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            if (search != null && !search.trim().isEmpty()) {
                sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                String like = "%" + search + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            } else {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("books", books);
        req.getRequestDispatcher("/books.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isEmployee(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String action = req.getParameter("action");
        try (Connection conn = DBConnection.getConnection()) {
            if ("add".equals(action)) {
                Book b = new Book();
                b.setTitle(req.getParameter("title"));
                b.setAuthor(req.getParameter("author"));
                b.setIsbn(req.getParameter("isbn"));
                b.setYear(Integer.parseInt(req.getParameter("year")));
                b.setPublisher(req.getParameter("publisher"));
                int qty = Integer.parseInt(req.getParameter("quantity"));
                b.setQuantity(qty);
                b.setAvailable(qty);
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO books (title,author,isbn,year,publisher,quantity,available) VALUES (?,?,?,?,?,?,?)"
                );
                ps.setString(1, b.getTitle());
                ps.setString(2, b.getAuthor());
                ps.setString(3, b.getIsbn());
                ps.setInt(4, b.getYear());
                ps.setString(5, b.getPublisher());
                ps.setInt(6, b.getQuantity());
                ps.setInt(7, b.getAvailable());
                ps.executeUpdate();
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String title = req.getParameter("title");
                String author = req.getParameter("author");
                String isbn = req.getParameter("isbn");
                int year = Integer.parseInt(req.getParameter("year"));
                String publisher = req.getParameter("publisher");
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                // при редактировании нужно пересчитать available? упростим
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE books SET title=?, author=?, isbn=?, year=?, publisher=?, quantity=? WHERE id=?"
                );
                ps.setString(1, title);
                ps.setString(2, author);
                ps.setString(3, isbn);
                ps.setInt(4, year);
                ps.setString(5, publisher);
                ps.setInt(6, quantity);
                ps.setInt(7, id);
                ps.executeUpdate();
                // обновим available, если quantity меньше текущего available
                ps = conn.prepareStatement("UPDATE books SET available = ? WHERE id=? AND available > ?");
                ps.setInt(1, quantity);
                ps.setInt(2, id);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/BooksServlet");
    }

    private Book extractBook(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setIsbn(rs.getString("isbn"));
        b.setYear(rs.getInt("year"));
        b.setPublisher(rs.getString("publisher"));
        b.setQuantity(rs.getInt("quantity"));
        b.setAvailable(rs.getInt("available"));
        return b;
    }
}
