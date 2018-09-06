/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.minor;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gaurav
 */
public class Register extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Register</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Register at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
     
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
     
    private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try
        {
        //processRequest(request, response);
            response.setContentType("text/html");  
            PrintWriter out = response.getWriter();  
            String n=request.getParameter("uname");  
            String p=request.getParameter("pwd");            
            Class.forName("com.mysql.jdbc.Driver");   
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?zeroDateTimeBehavior=convertToNull","root","vishesh");  
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user where username='"+n+"'");  
            //out.print("select * from user where username='"+n+"'");
            rs.last();
            if(rs.getRow()!=0)
            {
                out.print("<a href='index.html'>Username already present</a>");
            }
            else
            {
                byte[] salt = getSalt();
                String securePassword = getSecurePassword(p, salt);
                String salt1=new String(salt);
                PreparedStatement ps=con.prepareStatement("insert into user(username,password,salt,status) values(?,?,?,?)");  
  
                ps.setString(1,n);  
                ps.setString(2,securePassword);  
                ps.setString(3,salt1);  
                ps.setInt(4,0);  
          
                int i=ps.executeUpdate();  
                if(i>0)  
                {
                    out.print("You are successfully registered...");  
                    out.print("<a href='index.html'>Click here to login</a>");
                }
            }
        }
        catch( NoSuchAlgorithmException e)
        {}
        catch( NoSuchProviderException e1)
        {}
        catch(ClassNotFoundException e2)
        {}
        catch(SQLException se)
        {}
    }

    
    
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
