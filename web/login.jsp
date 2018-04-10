<%-- 
    Document   : login
    Created on : Feb 23, 2018, 12:44:05 PM
    Author     : trishen
--%>

<%@page import="Classes.User"%>
<%@page import="Classes.MySQL"%>
<%
    MySQL sql = new MySQL();
    String stickyEmail = "";
    Boolean loginAlert = false;

    if (request.getParameter("loginBtn") != null && request.getParameter("loginBtn").equalsIgnoreCase("login")) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = sql.login(email, password);
        if (user != null) 
        {
            session.setAttribute("User", user);
            response.sendRedirect("Home.jsp");
            return;
        } 
        else 
        {
            loginAlert = true;
            stickyEmail = email;
        }

    }

%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Login</title>
        <link href="thirdparty/fontawesome/css/fontawesome-all.css" rel="stylesheet">
        <link rel="stylesheet" href="thirdparty/bootstrap/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="thirdparty/DataTables/datatables.css"/>
    </head>
    <body>
        <jsp:include page="nav.jsp" />
        <br><br>
        <div class="container">
            <div class="row">
                <div class="col-sm"></div>
                <div class="col-sm">
                    <div class="card">
                        <div class="card-header">
                            Please login to continue
                        </div>
                        <div class="card-body">

                            <form method="post">
                                <div class="form-group">
                                    <label for="exampleInputEmail1">Email address</label>
                                    <input name="email" type="email" value="<%=stickyEmail%>" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email">
                                    <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
                                </div>
                                <div class="form-group">
                                    <label for="exampleInputPassword1">Password</label>
                                    <input name="password" type="password" class="form-control" id="exampleInputPassword1" placeholder="Password">
                                </div>
                                <button name="loginBtn" value="login" type="submit" class="btn btn-primary float-right">Login</button>
                            </form>

                        </div>
                    </div>
                </div>
                <div class="col-sm"></div>
            </div>
            <br>
            <div class="row">
                <div class="col-sm"></div>
                <div class="col-sm">
                    <% if (loginAlert) { %>
                    <div class="alert alert-danger" role="alert">
                        Error! Invalid Email or Password.
                    </div>
                    <% }%>
                </div>
                <div class="col-sm"></div>
            </div>
        </div>
        <script src="thirdparty/jquery/js/jquery-3.3.1.js"></script>
        <script src="thirdparty/bootstrap/js/bootstrap.js"></script>
    </body>
</html>

<% sql.closeConnection(); %>

