<%-- 
    Document   : Home
    Created on : Feb 23, 2018, 12:39:58 PM
    Author     : trishen
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="Classes.AccessList"%>
<%@page import="Classes.LinuxProcess"%>
<%@page import="org.apache.commons.net.util.SubnetUtils"%>
<%@page import="Classes.Lab"%>
<%@page import="Classes.MySQL"%>
<%@page import="Classes.User"%>
<%

    Boolean isMaster = false;
    List<String> test = new ArrayList<String>();
    String test2 = null;

//    if (session.getAttribute("User") == null) {
//        response.sendRedirect("login.jsp");
//        return;
//    }
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
    }

    MySQL sql = new MySQL();
    Lab currentLab = sql.getLab(ip);

    if (currentLab != null) {
        isMaster = true;
    }

    if (!isMaster) {
        response.sendRedirect("notMaster.jsp");
        return;
    }
    
    LinuxProcess proc = new LinuxProcess();
    currentLab = proc.getLabAccessList(currentLab);
    
    if(request.getParameter("saveChanges") != null)
    {
//        List<String> whiteListSplit = new ArrayList<String>();
//        List<String> blackListSplit = new ArrayList<String>();
//        String pcIp = request.getParameter("saveChanges");
//        String whiteList = request.getParameter("whitelist");
//        String blackList = request.getParameter("blacklist");
//
//        if(whiteList.length() > 0 )
//        {
//           String[] whiteListArray = whiteList.split("\\s+");
//           whiteListSplit = Arrays.asList(whiteListArray);
//        }
//       
//        if(blackList.length() > 0)
//        {
//            blackListSplit = Arrays.asList(blackList.split("\\s+"));
//        }
//        
//        List<AccessList> acl = new ArrayList<AccessList>();
//        acl.add(new AccessList(pcIp, whiteListSplit, blackListSplit));
//        
//        Lab newlab = currentLab;
//        newlab.initializeAccessList(acl);
//        
//        test2 = proc.applyAccessList(newlab, pcIp);
//
//        currentLab = proc.getLabAccessList(currentLab);
        
       
    }

%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.ResultSet" %>
<html>
    <head>
        <title>Home</title>
        <link href="thirdparty/fontawesome/css/fontawesome-all.css" rel="stylesheet">
    </head>
    <body>
        <link rel="stylesheet" href="thirdparty/bootstrap/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="thirdparty/DataTables/datatables.css"/>
        <jsp:include page="nav.jsp" />
        <div class="container">
            <div class="row">
                <p class="h2">Welcome! You're currently in Lab <%= currentLab.getLabNo()%></p>
            </div>
            <% for(String s : test) { %>
            <%= s %> <br>
            <% } %>
            <%= test2 %>
            <br>
            <div class="row">
                <table id="computerList" class="table table-striped">
                    <thead>
                        <tr>
                            <th>Ip Address</th>
                            <th>Actions</th>

                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>Ip Address</th>
                            <th>Actions</th>
                        </tr>
                    </tfoot>
                    <tbody>
                        <%
                            SubnetUtils utils = new SubnetUtils(currentLab.getSubnet());
                            String[] allIps = utils.getInfo().getAllAddresses();

                            for (String ips : allIps) {
                        %>
                        <tr>
                            <td><%= ips%></td>
                            <td class="text-center">
                                <button name="edit" value="<%= ips%>" type="button" class="btn btn-outline-primary" data-toggle="modal" data-target="#edit<%=ips%>">
                                    <i class="far fa-edit"></i>
                                </button>
                            </td>
                        </tr>  
                        <% }%>
                    </tbody>
                </table>
            </div>
            <% for(AccessList acl : currentLab.getAccessList()) { %>        
            <div class="modal fade" id="edit<%=acl.getIp()%>" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLongTitle">Editing <%= acl.getIp() %></h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <form>
                            <div class="modal-body">

                                <div class="form-group">
                                    <label for="whitelist">Whitelist</label>
                                    <textarea name="whitelist" class="form-control" id="whitelist" rows="3"><%for(String wl : acl.getWhiteList()){%><%= wl %><%}%></textarea>
                                </div>
                                
                                <div class="form-group">
                                    <label for="blacklist">Blacklist</label>
                                    <textarea name="blacklist" class="form-control" id="blacklist" rows="3"><%for(String bl : acl.getBlackList()){%><%= bl %><%}%></textarea>
                                </div>

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Close</button>
                                <button name="saveChanges" value="<%= acl.getIp() %>" type="submit" class="btn btn-outline-primary">Save changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <script src="thirdparty/jquery/js/jquery-3.3.1.js"></script>
        <script src="thirdparty/bootstrap/js/bootstrap.js"></script>
        <script type="text/javascript" src="thirdparty/DataTables/datatables.js"></script>
        <script>
            $(document).ready(function () {
                $('#computerList').DataTable();
            });
        </script>
    </body>
</html>

<% sql.closeConnection();%>




