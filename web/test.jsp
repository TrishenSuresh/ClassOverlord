<%-- 
    Document   : test
    Created on : Feb 23, 2018, 12:36:45 PM
    Author     : trishen
--%>

<%@page import="Classes.MySQL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%
            MySQL sql = new MySQL();
            String name = sql.test();
        %>
        
        <%= name %>
    </body>
</html>
