<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<html>
    <head>
        <title>View Books</title>
        <link href="<c:url value='/css/estilo.css'/>" rel="stylesheet" type="text/css">
    </head>
    <body>
	<h1>Listado de libros:</h1>
        <table>
            <thead>
                <tr>
                    <th>ISBN</th>
                    <th>Name</th>
                    <th>Author</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${books}" var="book">
                    <tr>
                        <td>${book.isbn}</td>
                        <td>${book.name}</td>
                        <td>${book.author}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
	<br/>
	<button onclick="window.location.href='/enterAddBook';">Añadir Libro</button>
	<button onclick="window.location.href='/servlet';">Mi Servlet</button>
	<button onclick="window.location.href='/index.html';">Inicio</button>
    </body>
</html>
