<%@page import="util.RoleUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String navUser = (String) session.getAttribute("user");
    String navRole = (String) session.getAttribute("role");
    boolean isNavAdmin = RoleUtil.isAdmin(navRole);
%>
<div class="navbar">
    <div class="navbar-left">
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="navbar-brand">📚 Библиотека</a>
        <nav class="navbar-links">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Главная</a>
            <a href="${pageContext.request.contextPath}/BooksServlet">Книги</a>
            <a href="${pageContext.request.contextPath}/ReadersServlet">Читатели</a>
            <a href="${pageContext.request.contextPath}/LoansServlet">Выдача/Возврат</a>
            <a href="${pageContext.request.contextPath}/PurchaseServlet">Закупки</a>
            <a href="${pageContext.request.contextPath}/ReportsServlet">Отчёты</a>
            <% if (isNavAdmin) { %>
            <a href="${pageContext.request.contextPath}/AdminUsersServlet">Сотрудники</a>
            <% }%>
        </nav>
    </div>
    <div class="navbar-right">
        <span class="navbar-user">👤 <%= navUser%></span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="navbar-btn">Выйти</a>
    </div>
</div>