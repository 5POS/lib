package servlets;

import database.DBConnection;
import util.RoleUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/AdminUsersServlet")
public class AdminUsersServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        String role = s != null ? RoleUtil.normalize((String) s.getAttribute("role")) : null;
        return RoleUtil.isAdmin(role);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard.jsp");
            return;
        }
        List<Map<String, Object>> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, username, role FROM users");
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("username", rs.getString("username"));
                row.put("role", rs.getString("role"));
                users.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("users", users);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard.jsp");
            return;
        }
        String action = req.getParameter("action");
        String cp = req.getContextPath();
        try (Connection conn = DBConnection.getConnection()) {
            if ("create".equals(action)) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                String role = req.getParameter("role");
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?,?,?)"
                );
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.executeUpdate();
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM users WHERE id=?"
                );
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(cp + "/AdminUsersServlet");
    }
}
