<%@page import="java.util.*, util.RoleUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    List<Map<String, Object>> users = (List<Map<String, Object>>) request.getAttribute("users");
    if (users == null) {
        users = Collections.emptyList();
    }
    String role = RoleUtil.normalize((String) session.getAttribute("role"));
    if (!RoleUtil.isAdmin(role)) {
        response.sendRedirect(cp + "/dashboard.jsp");
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Сотрудники</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>👥 Учётные записи сотрудников</h2>
            <button class="btn btn-success" onclick="showAddForm()">➕ Добавить сотрудника</button>
            <div class="card">
                <table class="admin-table">
                    <thead><tr><th>ID</th><th>Логин</th><th>Роль</th><th>Действия</th></tr></thead>
                    <tbody>
                        <% for (Map<String, Object> u : users) {%>
                        <tr>
                            <td><%= u.get("id")%></td><td><%= u.get("username")%></td><td><%= u.get("role")%></td>
                            <td>
                                <form action="<%= cp%>/AdminUsersServlet" method="post" onsubmit="return confirm('Удалить сотрудника?')">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<%= u.get("id")%>">
                                    <button type="submit" class="btn btn-sm btn-danger">🗑️ Удалить</button>
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
                <h3>Новый сотрудник</h3>
                <form action="<%= cp%>/AdminUsersServlet" method="post">
                    <input type="hidden" name="action" value="create">
                    <input type="text" name="username" placeholder="Логин" required><br>
                    <input type="password" name="password" placeholder="Пароль" required><br>
                    <select name="role">
                        <option value="LIBRARIAN">Библиотекарь</option>
                        <option value="ADMIN">Администратор</option>
                    </select><br>
                    <button type="submit" class="btn btn-primary">Создать</button>
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