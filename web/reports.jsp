<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
    String cp = request.getContextPath();
    Integer totalBooks = (Integer) request.getAttribute("totalBooks");
    Integer totalReaders = (Integer) request.getAttribute("totalReaders");
    Integer activeLoans = (Integer) request.getAttribute("activeLoans");
    Integer overdue = (Integer) request.getAttribute("overdue");
    List<Map<String, Object>> topBooks = (List<Map<String, Object>>) request.getAttribute("topBooks");
    if (topBooks == null) {
        topBooks = Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Отчёты</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>📊 Статистика</h2>
            <div class="stats-grid">
                <div class="stat-card"><span class="stat-value"><%= totalBooks != null ? totalBooks : 0%></span><span class="stat-label">Всего книг</span></div>
                <div class="stat-card"><span class="stat-value"><%= totalReaders != null ? totalReaders : 0%></span><span class="stat-label">Читателей</span></div>
                <div class="stat-card"><span class="stat-value"><%= activeLoans != null ? activeLoans : 0%></span><span class="stat-label">Активных выдач</span></div>
                <div class="stat-card"><span class="stat-value"><%= overdue != null ? overdue : 0%></span><span class="stat-label">Просрочено</span></div>
            </div>
            <div class="card">
                <h3>Топ-5 популярных книг</h3>
                <table class="admin-table">
                    <thead><tr><th>Книга</th><th>Количество выдач</th></tr></thead>
                    <tbody>
                        <% if (topBooks.isEmpty()) { %>
                        <tr><td colspan="2">Нет данных</td></tr>
                        <% } else {
                for (Map<String, Object> row : topBooks) {%>
                        <tr><td><%= row.get("title")%></td><td><%= row.get("loan_count")%></td></tr>
                        <%   }
                }%>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>