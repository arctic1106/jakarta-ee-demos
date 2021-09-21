<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>KumuluzEE JSF sample</title>
</head>
<body>
    <h2>Please enter customer data below:</h2>
    <form action="${pageContext.request.contextPath}/customers" method="post">
        <label for="id">ID:
            <input type="text" id="id" name="id"/>
        </label>
        <label for="firstName">First name:
            <input type="text" id="firstName" name="firstName"/>
        </label>
        <label for="lastName">Last name:
            <input type="text" id="lastName" name="lastName"/>
        </label>
        <br/>
        <br/>
        <input type="submit" id="submit" name="submit" value="Add"/>
        <br/>
    </form>
    <br/>
    <br/>
    <a href="welcome.jsp">Home</a>
</body>
</html>