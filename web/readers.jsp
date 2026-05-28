<%@page import="java.util.*, models.Reader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    List<Reader> readers = (List<Reader>) request.getAttribute("readers");
    if (readers == null) {
        readers = Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Читатели</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>🧑 Читатели</h2>
            <button class="btn btn-success" onclick="showAddForm()">+ Добавить читателя</button>
            <div class="card">
                <table class="admin-table">
                    <thead><tr><th>ID</th><th>Номер карты</th><th>ФИО</th><th>Дата рожд.</th><th>Телефон</th><th>Адрес</th><th>Дата рег.</th><th>Активен</th><th></th></tr></thead>
                    <tbody>
                        <% for (Reader r : readers) {%>
                        <tr>
                            <td><%= r.getId()%></td><td><%= r.getCardNumber()%></td><td><%= r.getFullName()%></td>
                            <td><%= r.getBirthDate()%></td><td><%= r.getPhone()%></td><td><%= r.getAddress()%></td>
                            <td><%= r.getRegistrationDate()%></td><td><%= r.isActive() ? "Да" : "Нет"%></td>
                            <td>
                                <form action="<%= cp%>/ReadersServlet" method="post" onsubmit="return confirm('Удалить читателя?')">
                                    <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<%= r.getId()%>">
                                    <button type="submit" class="btn btn-sm btn-danger">🗑️</button>
                                </form>
                            </td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
        </div>
        <div id="addModal" class="modal" style="display:none;">
            <div class="modal-content"><span class="close" onclick="closeModal()">&times;</span>
                <h3>Новый читатель</h3>
                <form action="<%= cp%>/ReadersServlet" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="text" name="full_name" placeholder="ФИО" required><br>
                    <input type="date" name="birth_date" required><br>
                    <input type="text" name="phone" placeholder="Телефон"><br>
                    <input type="text" name="address" placeholder="Адрес"><br>
                    <button type="submit" class="btn btn-primary">Создать карту</button>
                </form>
            </div>
        </div>
        <script>
            function showAddForm() {
                document.getElementById('addModal').style.display = 'flex';
            }
            function closeModal() {
                document.getElementById('addModal').style.display = 'none';
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