package servlets;

import database.DBConnection;
import util.RoleUtil;
import models.Reader;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/ReadersServlet")
public class ReadersServlet extends HttpServlet {
    private boolean isEmployee(HttpServletRequest req) {
        return RoleUtil.isEmployee((String)req.getSession().getAttribute("role"));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isEmployee(req)) { resp.sendRedirect(req.getContextPath()+"/login.jsp"); return; }
        List<Reader> readers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM readers ORDER BY id DESC");
            while (rs.next()) readers.add(extractReader(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("readers", readers);
        req.getRequestDispatcher("/readers.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isEmployee(req)) { resp.sendRedirect(req.getContextPath()+"/login.jsp"); return; }
        String action = req.getParameter("action");
        try (Connection conn = DBConnection.getConnection()) {
            if ("add".equals(action)) {
                Reader r = new Reader();
                r.setCardNumber(generateCardNumber(conn));
                r.setFullName(req.getParameter("full_name"));
                r.setBirthDate(java.sql.Date.valueOf(req.getParameter("birth_date")));
                r.setPhone(req.getParameter("phone"));
                r.setAddress(req.getParameter("address"));
                r.setRegistrationDate(new java.sql.Date(System.currentTimeMillis()));
                r.setActive(true);
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO readers (card_number,full_name,birth_date,phone,address,registration_date,active) VALUES (?,?,?,?,?,?,?)"
                );
                ps.setString(1, r.getCardNumber());
                ps.setString(2, r.getFullName());
                ps.setDate(3, r.getBirthDate());
                ps.setString(4, r.getPhone());
                ps.setString(5, r.getAddress());
                ps.setDate(6, r.getRegistrationDate());
                ps.setBoolean(7, r.isActive());
                ps.executeUpdate();
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                PreparedStatement ps = conn.prepareStatement("DELETE FROM readers WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
        resp.sendRedirect(req.getContextPath() + "/ReadersServlet");
    }

    private Reader extractReader(ResultSet rs) throws SQLException {
        Reader r = new Reader();
        r.setId(rs.getInt("id"));
        r.setCardNumber(rs.getString("card_number"));
        r.setFullName(rs.getString("full_name"));
        r.setBirthDate(rs.getDate("birth_date"));
        r.setPhone(rs.getString("phone"));
        r.setAddress(rs.getString("address"));
        r.setRegistrationDate(rs.getDate("registration_date"));
        r.setActive(rs.getBoolean("active"));
        return r;
    }

    private String generateCardNumber(Connection conn) throws SQLException {
        String last = "";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT card_number FROM readers ORDER BY id DESC LIMIT 1");
        if (rs.next()) last = rs.getString("card_number");
        int num = 1;
        if (last.matches("\\d+")) num = Integer.parseInt(last) + 1;
        else if (last.startsWith("C")) num = Integer.parseInt(last.substring(1)) + 1;
        return String.format("%06d", num);
    }
}