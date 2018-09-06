
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        if((session.getAttribute("id")==null)||(session.isNew())){
     %>
    <form action="Login" method="POST">
        <input type="text" name="uname" placeholder="username">
        <input type="password" name="pwd">
        <input type="submit" name="submit">
    </form>
    <a href="registration.html">Register</a>
    <% }
        else
        {
            response.sendRedirect("dash.jsp");

        } %>
</html>
