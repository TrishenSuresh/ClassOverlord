<%-- 
    Document   : notMaster
    Created on : Feb 23, 2018, 3:00:48 PM
    Author     : trishen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Not Master</title>
        <link href="thirdparty/fontawesome/css/fontawesome-all.css" rel="stylesheet">
        <link rel="stylesheet" href="thirdparty/bootstrap/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="thirdparty/DataTables/datatables.css"/>
    </head>
    <body>
        <jsp:include page="nav.jsp" />
        <br>
        <div class="container mx-auto">
            <div class="row ">
                <div class="col-2"></div>
                <div class="col-2 mx-auto">
                    <i class="far fa-frown fa-10x"></i>
                </div>
                <div class="col-2"></div>
            </div>
            <br>
            <div class="row">
                <div class="col-8 mx-auto">
                <p class="h1 text-center">Sorry! You're not using a master IP</p>
                <p class="h3 text-center">Please ensure you're plugged into the main Ethernet slot</p>
                <p class="h5 text-center">Contact the network administrator if the problem persist</p>
                </div>
            </div>
        </div>
        <script src="thirdparty/jquery/js/jquery-3.3.1.js"></script>
        <script src="thirdparty/bootstrap/js/bootstrap.js"></script>
    </body>
</html>
