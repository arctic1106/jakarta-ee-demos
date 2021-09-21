<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>
	<head>
		<title>Añadir Libro</title>
		<link href="<c:url value='/css/estilo.css'/>" rel="stylesheet" type="text/css">
	</head>
	<body>
		<h1>Añadir un libro:</h1>
		<br/>
		<form action="<c:url value='/addBook'/>" method="post">
			<label for="isbn">ISBN: </label>
			<input type="text" id="isbn" name="isbn" required/>
			<br/>
			<label for="name">Nombre: </label>
			<input type="text" id="name" name="name" required/>
			<br/>
			<label for="author">Autor: </label>
			<input type="text" id="author" name="author" required/>
			<br/>
			<input type="submit" value="submit"/>
		</form>
		<br/>
		<button onclick="window.location.href='<c:url value="/viewBooks"/>';">Volver</button>
	</body>
</html>
