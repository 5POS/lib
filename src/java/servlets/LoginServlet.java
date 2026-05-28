package servlets;

import database.DBConnection;
import util.RoleUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String cp = req.getContextPath();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                resp.sendRedirect(cp + "/login.jsp?error=db");
                return;
            }
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HttpSession session = req.getSession();
                session.setAttribute("user", rs.getString("username"));
                session.setAttribute("role", RoleUtil.normalize(rs.getString("role")));
                resp.sendRedirect(cp + "/dashboard.jsp");
            } else {
                resp.sendRedirect(cp + "/login.jsp?error=1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(cp + "/login.jsp?error=db");
        }
    }
}
