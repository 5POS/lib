package servlets;

import database.DBConnection;
import util.RoleUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/ReportsServlet")
public class ReportsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!RoleUtil.isEmployee((String) req.getSession().getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            // общее количество книг
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total_books FROM books");
            rs.next();
            req.setAttribute("totalBooks", rs.getInt("total_books"));
            rs.close();

            rs = st.executeQuery("SELECT COUNT(*) AS total_readers FROM readers");
            rs.next();
            req.setAttribute("totalReaders", rs.getInt("total_readers"));
            rs.close();

            rs = st.executeQuery("SELECT COUNT(*) AS active_loans FROM loans WHERE return_date IS NULL");
            rs.next();
            req.setAttribute("activeLoans", rs.getInt("active_loans"));
            rs.close();

            rs = st.executeQuery("SELECT COUNT(*) AS overdue FROM loans WHERE due_date < CURDATE() AND return_date IS NULL");
            rs.next();
            req.setAttribute("overdue", rs.getInt("overdue"));
            rs.close();

            // топ-5 популярных книг - собираем в List<Map>
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT b.title, COUNT(l.id) AS loan_count FROM books b "
                    + "JOIN loans l ON b.id=l.book_id GROUP BY b.id ORDER BY loan_count DESC LIMIT 5"
            );
            rs = ps.executeQuery();
            List<Map<String, Object>> topBooks = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("title", rs.getString("title"));
                row.put("loan_count", rs.getInt("loan_count"));
                topBooks.add(row);
            }
            rs.close();
            ps.close();
            req.setAttribute("topBooks", topBooks);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("dbError", e.getMessage());
        }
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }
}
