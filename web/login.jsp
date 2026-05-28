<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Авторизация</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-guest.jsp"%>
        <div class="auth-page">
            <div class="auth-card">
                <h2>Вход для сотрудников</h2>
                <%
                    String error = request.getParameter("error");
                    if ("1".equals(error)) {
                %>
                <div class="alert-error">Неверный логин или пароль</div>
                <%
                } else if ("db".equals(error)) {
                %>
                <div class="alert-error">Ошибка подключения к базе данных</div>
                <%
                    }
                %>
                <form action="<%= cp%>/LoginServlet" method="POST">
                    <div class="form-group">
                        <label for="username">Логин</label>
                        <input type="text" id="username" name="username" required autocomplete="username">
                    </div>
                    <div class="form-group">
                        <label for="password">Пароль</label>
                        <input type="password" id="password" name="password" required autocomplete="current-password">
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Войти</button>
                </form>
            </div>
        </div>
    </body>
</html>