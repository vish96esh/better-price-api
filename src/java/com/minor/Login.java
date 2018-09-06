package com.minor;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vishesh
 */
public class Login extends HttpServlet {

    private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");  
        PrintWriter out = response.getWriter();  
        out.print("method not allowed");
        return;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");  
        PrintWriter out = response.getWriter();  
        String n=request.getParameter("uname");  
        String p=request.getParameter("pwd");            
        try{  
            Class.forName("com.mysql.jdbc.Driver");   
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?zeroDateTimeBehavior=convertToNull","root","vishesh");  
            Statement stmt=con.createStatement();
            Statement stmt1=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user where username='"+n+"'");  
            System.out.println("select * from user where username='"+n+"'");
            //out.print("select * from user where username='"+n+"'");
            rs.last();
            if(rs.getRow()==0)
            {
                out.print("<a href='index.html'>Invalid username1 or password</a>");
            }
            else
            {
                String salt=rs.getString("salt");
                byte[] salt1 = salt.getBytes();
                String securePassword = getSecurePassword(p, salt1);
                System.out.println(securePassword+" "+salt1);
                if(!(rs.getString("password")).equals(securePassword))
                    out.print("<a href='index.html'>Invalid username or password1</a>");
                else
                {
                    if(rs.getInt("status")==1)
                    {
                        HttpSession session;
                        session=request.getSession();
                        //System.out.println("koi garbar nai!");
                        
                        int id=rs.getInt("id");
                        session.setAttribute("id",id);
                        session.setAttribute("isAuth",1);
                        System.out.println("select * from validation where id='"+rs.getInt("id")+"'");
                        ResultSet rs1=stmt1.executeQuery("select * from validation where id='"+rs.getInt("id")+"'");
                        System.out.println("select * from validation where id='"+rs.getInt("id")+"'");
                        rs1.last();
                        session.setAttribute("username",rs.getString("username"));
                        if(rs1.getRow()==0)
                            session.setAttribute("isKey",0);
                        else
                        {
                            session.setAttribute("isKey",1);
                            //session.setAttribute("rs1",rs1);
                            //session.setAttribute("rs",rs);
                            
                            session.setAttribute("key",rs1.getString("access_key"));
                        }
                        System.out.println("redirecting..");
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher("dash.jsp");
                        requestDispatcher.forward(request, response);
                        System.out.println("redirected..");
                        return;
                        
                    }
                    else
                        out.print("<a href='index.html'>User not yet verified</a>");
                }
            }
        }
        catch(SQLException se)
        {}
      
        catch (Exception e2) {System.out.println(e2);}  
          
        out.close();  
    }  

}
