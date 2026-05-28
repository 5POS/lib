<%@page import="java.util.*, models.Book"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String cp = request.getContextPath();
    List<Book> books = (List<Book>) request.getAttribute("books");
    if (books == null) {
        books = Collections.emptyList();
    }
    String search = request.getParameter("search");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Книги</title>
        <link rel="stylesheet" href="<%= cp%>/css/app.css">
    </head>
    <body>
        <%@include file="includes/navbar-auth.jsp"%>
        <div class="container">
            <h2>📚 Книжный фонд</h2>
            <form method="get" action="<%= cp%>/BooksServlet" class="search-form">
                <input type="text" name="search" placeholder="Поиск по названию или автору" value="<%= search != null ? search : ""%>">
                <button type="submit" class="btn btn-primary">Найти</button>
                <a href="<%= cp%>/BooksServlet" class="btn btn-add">Сброс</a>
            </form>
            <button class="btn btn-success" onclick="showAddForm()">+ Добавить книгу</button>
            <div class="card">
                <table class="admin-table">
                    <thead><tr><th>ID</th><th>Название</th><th>Автор</th><th>ISBN</th><th>Год</th><th>Издательство</th><th>Всего</th><th>Доступно</th><th>Действия</th></tr></thead>
                    <tbody>
                        <% for (Book b : books) {%>
                        <tr>
                            <td><%= b.getId()%></td><td><%= b.getTitle()%></td><td><%= b.getAuthor()%></td>
                            <td><%= b.getIsbn()%></td><td><%= b.getYear()%></td><td><%= b.getPublisher()%></td>
                            <td><%= b.getQuantity()%></td><td><%= b.getAvailable()%></td>
                            <td>
                                <button class="btn btn-sm" onclick="editBook(<%= b.getId()%>, '<%= b.getTitle()%>', '<%= b.getAuthor()%>', '<%= b.getIsbn()%>',<%= b.getYear()%>, '<%= b.getPublisher()%>',<%= b.getQuantity()%>)">✏️</button>
                                <form action="<%= cp%>/BooksServlet" method="post" style="display:inline;" onsubmit="return confirm('Удалить книгу?')">
                                    <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<%= b.getId()%>">
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
                <h3>Добавить книгу</h3>
                <form action="<%= cp%>/BooksServlet" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="text" name="title" placeholder="Название" required><br>
                    <input type="text" name="author" placeholder="Автор"><br>
                    <input type="text" name="isbn" placeholder="ISBN"><br>
                    <input type="number" name="year" placeholder="Год"><br>
                    <input type="text" name="publisher" placeholder="Издательство"><br>
                    <input type="number" name="quantity" placeholder="Количество" required><br>
                    <button type="submit" class="btn btn-primary">Сохранить</button>
                </form>
            </div>
        </div>
        <div id="editModal" class="modal" style="display:none;">
            <div class="modal-content"><span class="close" onclick="closeEditModal()">&times;</span>
                <h3>Редактировать книгу</h3>
                <form id="editForm" action="<%= cp%>/BooksServlet" method="post">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" id="editId">
                    <input type="text" name="title" id="editTitle" required><br>
                    <input type="text" name="author" id="editAuthor"><br>
                    <input type="text" name="isbn" id="editIsbn"><br>
                    <input type="number" name="year" id="editYear"><br>
                    <input type="text" name="publisher" id="editPublisher"><br>
                    <input type="number" name="quantity" id="editQuantity" required><br>
                    <button type="submit" class="btn btn-primary">Обновить</button>
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
            function editBook(id, title, author, isbn, year, publisher, qty) {
                document.getElementById('editId').value = id;
                document.getElementById('editTitle').value = title;
                document.getElementById('editAuthor').value = author;
                document.getElementById('editIsbn').value = isbn;
                document.getElementById('editYear').value = year;
                document.getElementById('editPublisher').value = publisher;
                document.getElementById('editQuantity').value = qty;
                document.getElementById('editModal').style.display = 'flex';
            }
            function closeEditModal() {
                document.getElementById('editModal').style.display = 'none';
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