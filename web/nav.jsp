<%--
  Created by IntelliJ IDEA.
  User: trishen
  Date: 23/2/18
  Time: 10:44 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarTogglerDemo01">
        <a class="navbar-brand" href="#">Class Overlord</a>
        <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item">
                <a class="nav-link" href="Home.jsp">Home</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="Labs.jsp">Labs</a>
            </li>
        </ul>
        <form class="form-inline my-2 my-lg-0">
            <% if(session.getAttribute("User") == null ) { %>
            <a class="btn btn-outline-success my-2 my-sm-0" href="login.jsp" role="button"><i class="fas fa-sign-in-alt"></i> &nbsp;Login</a>
            <% } else { %>
            <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item active">
                <a class="nav-link" href="logout.jsp">Log Out</a>
            </li>
        </ul>
            <% } %>
        </form>
    </div>
</nav>


