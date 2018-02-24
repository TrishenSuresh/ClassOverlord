<%-- 
    Document   : logout.jsp
    Created on : Feb 23, 2018, 1:36:32 PM
    Author     : trishen
--%>

<% 
session.removeAttribute("User");
response.sendRedirect("login.jsp");
%>
