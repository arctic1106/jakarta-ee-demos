<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
	<head>
		<title>Añadir Libro</title>
		<link href="<c:url value='/css/estilo.css'/>" rel="stylesheet" type="text/css">
	</head>
	<body>
		<h1>Añadir un libro:</h1>
		<br/>
		<form:form action="/addBook" method="post" modelAttribute="book">
			<form:label path="isbn">ISBN: </form:label> <form:input type="text" path="isbn"/>
			<form:label path="name">Nombre: </form:label> <form:input type="text" path="name"/>
			<form:label path="author">Autor: </form:label> <form:input path="author"/>
			<input type="submit" value="submit"/>
		</form:form>
	</body>
</html>
