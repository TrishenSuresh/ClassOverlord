<%-- 
    Document   : addLab
    Created on : Feb 23, 2018, 2:31:05 PM
    Author     : trishen
--%>
<%@page import="java.util.List"%>
<%@page import="Classes.Lab"%>
<%@page import="Classes.MySQL"%>
<%
   
    MySQL sql = new MySQL();
    Boolean dropDown = false;
    String dropDownText = "";
    String testText = "testText";
    List<Lab> allLabs = sql.getAllLabs();

    if (request.getParameter("saveChanges") != null) {
        
        Lab oldLab = null;
        
        String selectedLabNo = request.getParameter("saveChanges");
        
        for(Lab l : allLabs)
        {
            if(l.getLabNo().equals(selectedLabNo))
            {
                oldLab = l;
                break;
            }
        }

        
        String labNo = request.getParameter("labNo");
        String subnet = request.getParameter("subnet");
        String masterIp = request.getParameter("masterIp");
        Lab newLab = new Lab(labNo, subnet, masterIp,false);

        dropDownText = sql.editLab(newLab, oldLab);
        dropDown = true;

        allLabs = sql.getAllLabs();

    }

    if (request.getParameter("delete") != null) 
    {
        String selectedLab = request.getParameter("delete");
       
        for(Lab l : allLabs)
        {
            if(l.getLabNo().equals(selectedLab))
            {
               dropDownText = sql.deletelab(l);
               dropDown = true;
            }
        }
   
        allLabs = sql.getAllLabs();
    }

    if (request.getParameter("addLab") != null) {

        String labNo = request.getParameter("labNo");
        String subnet = request.getParameter("subnet");
        String masterIp = request.getParameter("masterIp");

        Lab newLab = new Lab(labNo, subnet, masterIp,false);

        dropDownText = sql.addLab(newLab);
        dropDown = true;
        
        allLabs = sql.getAllLabs();

    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="thirdparty/fontawesome/css/fontawesome-all.css" rel="stylesheet">
        <link rel="stylesheet" href="thirdparty/bootstrap/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="thirdparty/DataTables/datatables.css"/>
    </head>
    <body>
        <jsp:include page="nav.jsp" />
        <div class="container">
            <br>
            <div class="row">
                <p class="h1">List of labs</p>
            </div>
            <br>
            <div class="row">
                <table id="labList" class="table table-striped">
                    <thead>
                        <tr>
                            <th>Lab No</th>
                            <th>Subnet</th>
                            <th>Master Ip</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>Lab No</th>
                            <th>Subnet</th>
                            <th>Master Ip</th>
                            <th>Actions</th>
                        </tr>
                    </tfoot>
                    <tbody>
                        <% for (Lab currentLab : allLabs) {%>
                        <tr>
                            <td> <%= currentLab.getLabNo()%> </td>
                            <td> <%= currentLab.getSubnet()%> </td>
                            <td><%= currentLab.getMasterIp()%></td>
                            <td class="text-center"> 
                                <button name="edit" value="<%= currentLab.getLabNo()%>" type="button" class="btn btn-outline-primary" data-toggle="modal" data-target="#Edit<%= currentLab.getLabNo()%>">
                                    <i class="far fa-edit"></i>
                                </button>
                                <button name="delete" value="<%= currentLab.getLabNo()%>" type="button" class="btn btn-outline-danger" data-toggle="modal" data-target="#Delete<%= currentLab.getLabNo()%>">
                                    <i class="far fa-trash-alt"></i>
                                </button> 
                            </td>

                        </tr>

                        <% } %>
                    </tbody>
                </table>
            </div>
            <br>
            <div class="row float-right">
                <button name="newLab" value="" type="button" class="btn btn-outline-success" data-toggle="modal" data-target="#newLab">
                    <i class="far fa-plus-square"></i> &nbsp; Add New Lab
                </button> 
            </div>
        </div>

        <%-- Edit Labs --%>         
        <% for (Lab currentLab : allLabs) {%>            
        <div class="modal fade" id="Edit<%= currentLab.getLabNo()%>" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Editing Lab <%= currentLab.getLabNo()%></h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form>
                        <div class="modal-body">

                            <div class="form-group">
                                <label for="labNo">Lab No</label>
                                <input name="labNo" type="text" class="form-control" id="labNo" value="<%= currentLab.getLabNo()%>">                               
                            </div>
                            <div class="form-group">
                                <label for="subnet">Subnet</label>
                                <input name="subnet" type="text" class="form-control" id="subnet" value="<%= currentLab.getSubnet()%>" >
                            </div>
                            <div class="form-group">
                                <label for="masterIp">Master Ip</label>
                                <input name="masterIp" type="text" class="form-control" id="masterIp" value="<%= currentLab.getMasterIp()%>" >
                            </div>

                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Close</button>
                            <button name="saveChanges" value="<%= currentLab.getLabNo()%>" type="submit" class="btn btn-outline-primary">Save changes</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <% } %>

        <%-- Add Labs --%>                  
        <div class="modal fade" id="newLab" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Add a new lab</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form>
                        <div class="modal-body">

                            <div class="form-group">
                                <label for="labNo">Lab No</label>
                                <input name="labNo" type="text" class="form-control" id="labNo" required="true">                               
                            </div>
                            <div class="form-group">
                                <label for="subnet">Subnet</label>
                                <input name="subnet" type="text" class="form-control" id="subnet" required="true">
                            </div>
                            <div class="form-group">
                                <label for="masterIp">Master Ip</label>
                                <input name="masterIp" type="text" class="form-control" id="masterIp" required="true">
                            </div>

                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Close</button>
                            <button name="addLab" value="save" type="submit" class="btn btn-outline-primary">Save</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- Delete Labs --%>        
        <% for (Lab currentLab : allLabs) {%>            
        <div class="modal fade" id="Delete<%= currentLab.getLabNo()%>" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Delete <%=currentLab.getLabNo()%>?</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete <%= currentLab.getLabNo()%>?
                    </div>
                    <form>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">No</button>
                            <button type="submit" name="delete" value="<%=currentLab.getLabNo()%>" class="btn btn-outline-danger">Yes</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <% } %>

        <%-- Drop Down --%>     
        <% if (dropDown) {%>
        <div class="modal fade" id="dropDownModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">

                    <div class="modal-body">
                        <%= dropDownText%>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary-secondary" data-dismiss="modal">Ok</button>
                    </div>
                </div>
            </div>
        </div>
        <% }%>
        <script src="thirdparty/jquery/js/jquery-3.3.1.js"></script>
        <script src="thirdparty/bootstrap/js/bootstrap.js"></script>
        <script type="text/javascript" src="thirdparty/DataTables/datatables.js"></script>
        <script>
            $(document).ready(function () {
                $('#labList').DataTable();
                $('#dropDownModal').modal('show');
            });
        </script>
    </body>
</html>

<%
    sql.closeConnection();
%>