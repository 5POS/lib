<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    List<Map<String, Object>> loans = (List<Map<String, Object>>) request.getAttribute("loans");
    if (loans == null) {
        loans = Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Выдача/Возврат</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>↪️ Выдача книг</h2>
            <button class="btn btn-success" onclick="showIssueForm()">➕ Выдать книгу</button>
            <div class="card">
                <table class="admin-table">
                    <thead><tr><th>ID</th><th>Книга</th><th>Читатель</th><th>Дата выдачи</th><th>Срок</th><th>Дата возврата</th><th>Статус</th><th></th></tr></thead>
                    <tbody>
                        <% for (Map<String, Object> row : loans) {%>
                        <tr>
                            <td><%= row.get("id")%></td><td><%= row.get("book_title")%></td><td><%= row.get("reader_name")%></td>
                            <td><%= row.get("loan_date")%></td><td><%= row.get("due_date")%></td>
                            <td><%= row.get("return_date") != null ? row.get("return_date") : "—"%></td>
                            <td><%= row.get("status")%></td>
                            <td>
                                <% if (!"RETURNED".equals(row.get("status"))) {%>
                                <form action="<%= cp%>/LoansServlet" method="post">
                                    <input type="hidden" name="action" value="return">
                                    <input type="hidden" name="loan_id" value="<%= row.get("id")%>">
                                    <button type="submit" class="btn btn-sm btn-primary">Принять возврат</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
        </div>
        <div id="issueModal" class="modal" style="display:none;">
            <div class="modal-content"><span class="close" onclick="closeIssueModal()">&times;</span>
                <h3>Выдача книги</h3>
                <form action="<%= cp%>/LoansServlet" method="post">
                    <input type="hidden" name="action" value="issue">
                    <label>Книга (ID):</label><input type="number" name="book_id" required><br>
                    <label>Читатель (ID):</label><input type="number" name="reader_id" required><br>
                    <button type="submit" class="btn btn-primary">Выдать</button>
                </form>
                <p class="hint">ID книг и читателей можно посмотреть в соответствующих разделах</p>
            </div>
        </div>
        <script>
            function showIssueForm() {
                document.getElementById('issueModal').style.display = 'flex';
            }
            function closeIssueModal() {
                document.getElementById('issueModal').style.display = 'none';
            }
            window.onclick = function (e) {
                if (e.target.classList.contains('modal'))
                    e.target.style.display = 'none';
            }
        </script>
        <style>.modal{position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);display:flex;align-items:center;justify-content:center;z-index:1000;}
            .modal-content{background:white;padding:25px;border-radius:16px;min-width:300px;}</style>
    </body>
</html>