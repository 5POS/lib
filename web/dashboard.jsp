<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="util.RoleUtil"%>
<%
    String user = (String) session.getAttribute("user");
    String role = (String) session.getAttribute("role");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
    boolean isAdmin = RoleUtil.isAdmin(role);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Панель управления</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <div class="welcome"><h2>Добро пожаловать, <%= user%>!</h2><p>Роль: <%= role%></p></div>
            <div class="action-grid">
                <a href="BooksServlet" class="action-card"><div class="icon">📖</div><h3>Книги</h3><p>Каталог, поиск, редактирование</p></a>
                <a href="ReadersServlet" class="action-card"><div class="icon">🧑</div><h3>Читатели</h3><p>Регистрация, карточки</p></a>
                <a href="LoansServlet" class="action-card"><div class="icon">↪️</div><h3>Выдача/Возврат</h3><p>Оформление и контроль</p></a>
                <a href="PurchaseServlet" class="action-card"><div class="icon">📦</div><h3>Закупки</h3><p>Заявки и приём книг</p></a>
                <a href="ReportsServlet" class="action-card"><div class="icon">📊</div><h3>Отчёты</h3><p>Статистика и аналитика</p></a>
                <% if (isAdmin) { %>
                <a href="AdminUsersServlet" class="action-card"><div class="icon">👥</div><h3>Сотрудники</h3><p>Управление учётными записями</p></a>
                <% }%>
            </div>
        </div>
    </body>
</html>