<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    List<Map<String, Object>> requests = (List<Map<String, Object>>) request.getAttribute("requests");
    if (requests == null) {
        requests = Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Закупки</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>📦 Планирование закупок</h2>
            <button class="btn btn-success" onclick="showCreateForm()">➕ Новая заявка</button>
            <div class="card">
                <table class="admin-table">
                    <thead><tr><th>ID</th><th>Дата</th><th>Статус</th><th>Кол-во экз.</th><th>Действия</th></tr></thead>
                    <tbody>
                        <% for (Map<String, Object> req : requests) {%>
                        <tr>
                            <td><%= req.get("id")%></td><td><%= req.get("request_date")%></td><td><%= req.get("status")%></td>
                            <td><%= req.get("total_items")%></td>
                            <td>
                                <% if ("NEW".equals(req.get("status"))) {%>
                                <form action="<%= cp%>/PurchaseServlet" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="receive">
                                    <input type="hidden" name="id" value="<%= req.get("id")%>">
                                    <button type="submit" class="btn btn-sm btn-primary" onclick="return confirm('Подтвердить поступление и добавить книги в фонд?')">📥 Принять</button>
                                </form>
                                <% } else { %>
                                <span>✓ Получено</span>
                                <% } %>
                            </td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
        </div>
        <div id="createModal" class="modal" style="display:none;">
            <div class="modal-content"><span class="close" onclick="closeCreateModal()">&times;</span>
                <h3>Создать заявку на закупку</h3>
                <form action="<%= cp%>/PurchaseServlet" method="post">
                    <input type="hidden" name="action" value="create">
                    <input type="text" name="book_title" placeholder="Название книги" required><br>
                    <input type="text" name="author" placeholder="Автор"><br>
                    <input type="text" name="isbn" placeholder="ISBN"><br>
                    <input type="number" name="year" placeholder="Год"><br>
                    <input type="text" name="publisher" placeholder="Издательство"><br>
                    <input type="number" name="quantity" placeholder="Количество" required><br>
                    <button type="submit" class="btn btn-primary">Отправить заявку</button>
                </form>
            </div>
        </div>
        <script>
            function showCreateForm() {
                document.getElementById('createModal').style.display = 'flex';
            }
            function closeCreateModal() {
                document.getElementById('createModal').style.display = 'none';
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