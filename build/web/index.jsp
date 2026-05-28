<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    String user = (String) session.getAttribute("user");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Библиотечная система</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-guest.jsp"%>
        <section class="hero">
            <h1>Автоматизация библиотеки</h1>
            <p>Управление книгами, читателями, выдачей и закупками</p>
            <div class="hero-actions">
                <a href="<%= cp%>/login.jsp" class="btn btn-primary">Вход для сотрудников</a>
            </div>
        </section>
        <div class="container features">
            <div class="feature-card"><div class="feature-icon">📖</div><h3>Учёт книг</h3><p>Полный каталог с поиском</p></div>
            <div class="feature-card"><div class="feature-icon">🧑</div><h3>Читатели</h3><p>Регистрация и карточки</p></div>
            <div class="feature-card"><div class="feature-icon">↪️</div><h3>Выдача/возврат</h3><p>Контроль сроков</p></div>
        </div>
    </body>
</html>