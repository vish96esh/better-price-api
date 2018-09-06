<%@ page import="java.sql.ResultSet"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <body>
        <% if(session.getAttribute("id")==null || session.isNew()){ %>
        <a href="index.html">Please login</a>
        <% }else if(((Integer)session.getAttribute("isAuth")).equals(1)) { 
        if(((Integer)session.getAttribute("isKey")).equals(1))
        {
            %>
            <div>
            username=<%=(String)(session.getAttribute("username"))%>
            <br>
            key=<%=(String)(session.getAttribute("key"))%>
            </div>
            <%
        }
        %>
        <input type="button" name="generate" value="Generate" onclick="window.open('http://localhost:8080/Minor2/Generate','_self');"/>
        <a href="Logout">Logout</a>
        <%}else{%>
        <a href="index.html">Please login</a>
        <%}%>
    </body>
</html>
