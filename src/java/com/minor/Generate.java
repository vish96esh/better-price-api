package com.minor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Random;
import javax.servlet.RequestDispatcher;

/**
 *
 * @author Vishesh
 */
public class Generate extends HttpServlet {

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz_";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 40) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out=response.getWriter();
        HttpSession session=request.getSession();
        if(session.isNew() || session.getAttribute("id")==null)
        {
            out.print("<a href='index.html'>Please login</a>");
        }
        else
        {
            int id=(Integer)session.getAttribute("id");
            if(!((Integer)session.getAttribute("isAuth")).equals(1))
                out.print("<a href='index.html'>Please login</a>");
            else
            {
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");   
                    Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?zeroDateTimeBehavior=convertToNull","root","vishesh");         
                    Statement stmt=con.createStatement();
                    String salt=getSaltString();
                    //out.print(((Integer)session.getAttribute("isKey")));
                    if(((Integer)session.getAttribute("isKey"))==1)
                    {
                        String query = "update validation set access_key=? where id='"+id+"'";
                        PreparedStatement preparedStmt = con.prepareStatement(query);
                        //preparedStmt.setInt(2,id);
                        preparedStmt.setString(1,salt);
                        preparedStmt.execute();
                        
                    }
                    else
                    {
                        String query = "insert into validation (id,access_key) values (?,?)";
                        PreparedStatement preparedStmt = con.prepareStatement(query);
                        preparedStmt.setInt(1,id);
                        preparedStmt.setString(2,salt);
                        preparedStmt.execute();
                        session.setAttribute("isKey",1);
                    }
                    //ResultSet rs1=stmt.executeQuery("select * from validation where id='"+id+"'");
                    //session.setAttribute("rs1",rs1);
                    session.setAttribute("key",salt);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("dash.jsp");
                    requestDispatcher.forward(request, response);
                    return;
                }
                catch(ClassNotFoundException ce){}
                catch(SQLException se){System.out.println("SQL");}
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}
