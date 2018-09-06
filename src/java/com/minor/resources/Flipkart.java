package com.minor.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import com.minor.methods.*;
import java.sql.Timestamp;

/**
 * REST Web Service
 *
 * @author Vishesh
 */
@Path("flipkart")
public class Flipkart {

    public  ResultSet rs=null;
    public Statement stmt;
    public Connection con;
    @Context
    private UriInfo context;

    public Flipkart() {
        try {  
	    Class.forName("com.mysql.jdbc.Driver");   
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?zeroDateTimeBehavior=convertToNull","root","vishesh");  
            stmt=con.createStatement();
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e);
        }  
    }
    
    public static JSONArray convert(ResultSet r) throws SQLException, JSONException
    {
        ResultSetMetaData rsmd = r.getMetaData();
        r.beforeFirst();
        JSONArray json = new JSONArray();
        while(r.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for(int i=1; i<numColumns+1; i++) {
                String column_name = rsmd.getColumnLabel(i); 
                switch( rsmd.getColumnType( i ) ) {
                    case java.sql.Types.ARRAY:
                        obj.put(column_name, r.getArray(column_name));     
                        break;
                    case java.sql.Types.BIGINT:
                        obj.put(column_name, r.getInt(column_name));       
                        break;
                    case java.sql.Types.BOOLEAN:
                        obj.put(column_name, r.getBoolean(column_name));   
                        break;
                    case java.sql.Types.BLOB:
                        obj.put(column_name, r.getBlob(column_name));      
                        break;
                    case java.sql.Types.DOUBLE:
                        obj.put(column_name, r.getDouble(column_name));    
                        break;
                    case java.sql.Types.FLOAT:
                        obj.put(column_name, r.getFloat(column_name));     
                        break;
                    case java.sql.Types.INTEGER:
                        obj.put(column_name, r.getInt(column_name));       
                        break;
                    case java.sql.Types.NVARCHAR:
                        obj.put(column_name, r.getNString(column_name));   
                        break;
                    case java.sql.Types.VARCHAR:
                        obj.put(column_name, r.getString(column_name));    
                        break;
                    case java.sql.Types.TINYINT:
                        obj.put(column_name, r.getInt(column_name));       
                        break;
                    case java.sql.Types.SMALLINT:
                        obj.put(column_name, r.getInt(column_name));       
                        break;
                    default:
                        obj.put(column_name, r.getObject(column_name));    
                        break;
                }
            }
            json.put(obj);
        }
        return json;
    }

    public boolean validUser(String uname, String password) throws SQLException
    {
        int flag=0;
        System.out.println("select * from user1 where username='"+uname+"'");
        rs=stmt.executeQuery("select * from user1 where username='"+uname+"'");
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            flag=1;
        }
        else
        {
            rs.beforeFirst();
            rs.next();
            int id=rs.getInt("id");
            //System.out.println("select * from validation where id='"+id+"'");
            //ResultSet rs1=stmt.executeQuery("select * from validation where id='"+id+"'");
            //rs1.next();
            //System.out.println("1");
            String key=rs.getString("password");
            //System.out.println("2");
            if(!(key.equals(password)))
                flag=1;
        }
        if(flag==1)
            return false;
        else
            return true;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock) {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
               return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="where inStock='1'";
            System.out.println("select * from flipkart "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart "+str+" "+str1);  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByID(@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            rs=stmt.executeQuery("select * from flipkart where id='"+id+"'");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("category")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByCat(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("category") String cat)
    {
        if(cat==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart where category='"+cat+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart where category='"+cat+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("brand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByBrand(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("brand") String brand)
    {
        if(brand==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("title")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByTitle(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("title") String title)
    {
        if(title==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart where Match(title) Against('"+title+"')  "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart where Match(title) Against('"+title+"') "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("mrp/{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByMrp(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("type") String type, @QueryParam("value") String value)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(type!=null && value!=null)
                cond=type+"='"+value+"' and ";
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from flipkart where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @GET
    @Path("amount/{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByAmount(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("type") String type, @QueryParam("value") String value)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(type!=null && value!=null)
                cond=type+"='"+value+"' and ";
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from flipkart where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock) {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
               return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="where inStock='1'";
            System.out.println("select * from flipkart_view "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByIDView(@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            rs=stmt.executeQuery("select * from flipkart_view where id='"+id+"'");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("category")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByCatView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("category") String cat)
    {
        if(cat==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart_view where category='"+cat+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view where category='"+cat+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("brand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByBrandView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("brand") String brand)
    {
        if(brand==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart_view where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("title")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByTitleView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("title") String title)
    {
        if(title==null)
            return Response.status(400).entity("{\"Error\":\"Insufficient parameters\"}").build();
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(inStock!=null && inStock.equals("yes"))
                cond="and inStock='1'";
            System.out.println("select * from flipkart_view where Match(title) Against('"+title+"')  "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view where Match(title) Against('"+title+"') "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("mrp/{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByMrpView(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("type") String type, @QueryParam("value") String value)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(type!=null && value!=null)
                cond=type+"='"+value+"' and ";
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from flipkart_view where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @VIEW
    @Path("amount/{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonByAmountView(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("inStock") String inStock, @QueryParam("type") String type, @QueryParam("value") String value)
    {
        JSONArray jsonn=new JSONArray();
        try {
            if(!validUser(uname,password))
                return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
            String str="";
            String str1="";
            if(sort!=null)
            {
                switch(sort)
                {
                    case "title_asc":str="order by title";
                        break;
                    case "title_desc":str="order by title";
                        str1="desc";
                        break;
                    case "amt_asc":str="order by amount";
                        break;
                    case "amt_desc":str="order by amount";
                        str1="desc";
                        break;
                    case "mrp_asc":str="order by mrp";
                        break;
                    case "mrp_desc":str="order by mrp";
                        str1="desc";
                        break;
                }
            }
            String cond="";
            if(type!=null && value!=null)
                cond=type+"='"+value+"' and ";
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from flipkart_view where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from flipkart_view where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
            jsonn=convert(rs); 
        }
        catch(SQLException se)
        {
            System.out.println("SQL Error");
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        String content= jsonn.toString();
        return Response.status(200).entity(content).build();
    }
    
    @HEAD
    public Response ping(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
        return Response.ok().build();
    }
    
    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOptions(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException{
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();
	return Response.ok()
			.allow("POST", "GET", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH", "LOCK", "UNLOCK", "LINK", "UNLINK", "PROPFIND", "COPY", "VIEW")

                .header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Credentials", "true")
			.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, HEAD, PATCH, LOCK, UNLOCK, LINK, UNLINK, PROPFIND, COPY, VIEW")
			.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
			.build();
    }
    
    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response delEntry (@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException { 
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();        
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where username='"+uname+"' and resource_name='flipkart'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(403).entity("You do not have the permission to lock the resource").build();
        String query = "select * from flipkart where id=\'"+id+"\'";
        rs=stmt.executeQuery(query);
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            return Response.status(400).entity("Invalid ID").build();
        }
        query="delete from flipkart where id=\'"+id+"\'";
        stmt.executeUpdate(query);
        return Response.status(200).entity("Success").build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postJson(String content, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException, JSONException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();        
        JSONArray arr = new JSONArray(content);
        JSONObject obj;
        int i=0;
        String id,title,desc,imgurl,purl,brand,size,su,color,category,imageRes,query,dup="";
        ResultSet rs;
        PreparedStatement preparedStmt;
        double mrp,amt;
        int is,cod1,b1,row,flag=0;
        boolean cod,details;
        while(i<arr.length())
        {
            obj=arr.getJSONObject(i);
            id = obj.getString("id");
            System.out.println(id);
            title = obj.getString("title");
            desc = obj.getString("description");
            if(desc.equals(""))
                desc="No description";
            imgurl=obj.getString("imageUrl");
            mrp=obj.getDouble("mrp");
            amt=obj.getDouble("amount");
            purl=obj.getString("productUrl");
            brand=obj.getString("brand");
            is=obj.optInt("inStock", 0);
            cod=obj.optBoolean("cod",false);
            cod1=0;
            if(cod==true)
                cod1=1;
            size=obj.getString("size");
            if(size.equals(""))
                size="N/A";    
            su=obj.getString("sizeUnit");
            if(su.equals(""))
                su="N/A";
            color=obj.getString("color");
            if(color.equals(""))
                color="N/A";
            details=obj.optBoolean("details",false);
            b1=0;
            if(details==true)
                b1=1;
            category=obj.getString("category");
            imageRes=obj.getString("imageRes");			
            query = "select id from flipkart where id=\'"+id+"\'";
            rs=stmt.executeQuery(query);
            rs.last();
            row=rs.getRow();
            if(row!=0)
            {
                flag=1;
                dup+=id+" "; 
                i++;
                continue;
            }
            rs.beforeFirst();
            query = "insert into flipkart (title, description, imageUrl, mrp, amount, productUrl, inStock, cod, size, sizeUnit, color, brand, details, category, imageRes, id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, title);
            preparedStmt.setString (2, desc);
            preparedStmt.setString (3, imgurl);
            preparedStmt.setDouble (4, mrp);
            preparedStmt.setDouble (5, amt);
            preparedStmt.setString (6, purl);
            preparedStmt.setInt (7, is);
            preparedStmt.setInt (8, cod1);
            preparedStmt.setString (9, size);
            preparedStmt.setString (10, su);
            preparedStmt.setString (11, color);
            preparedStmt.setString (12, brand);
            preparedStmt.setInt (13, b1);
            preparedStmt.setString (14, category);
            preparedStmt.setString (15, imageRes);	    
            preparedStmt.setString (16, id);

            preparedStmt.execute();
            i++;
        }
        if(flag!=0)
            return Response.status(200).entity("Product ID(s) "+dup+" already present").build();
        else
            return Response.status(200).entity("Success").build();

    }  
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putJson (String content, @PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException, JSONException{
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();        
        String query = "select * from flipkart where id=\'"+id+"\'";
        ResultSet rs=stmt.executeQuery(query);
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            return Response.status(400).entity("Invalid ID").build();
        }
        rs.beforeFirst();
        query = "update flipkart set title = ?, description = ?, imageUrl = ?, mrp = ?, amount = ?, productUrl = ?, inStock = ?, cod = ?, size = ?, sizeUnit = ?, color = ?, brand = ?, details = ?, category = ?, imageRes = ? where id=\'"+id+"\'";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        JSONObject obj = new JSONObject(content);
        String title = obj.getString("title");
        String desc = obj.getString("description");
        if(desc.equals(""))
            desc="No description";
        String imgurl=obj.getString("imageUrl");
        double mrp=obj.getDouble("mrp");
        double amt=obj.getDouble("amount");
        String purl=obj.getString("productUrl");
        String brand=obj.getString("brand");
        int is=obj.optInt("inStock", 0);
        boolean cod=obj.optBoolean("cod",false);
        int cod1=0;
        if(cod==true)
            cod1=1;
        String size=obj.getString("size");
        if(size.equals(""))
            size="N/A";    
        String su=obj.getString("sizeUnit");
        if(su.equals(""))
            su="N/A";
        String color=obj.getString("color");
        if(color.equals(""))
            color="N/A";
        boolean details=obj.optBoolean("details",false);
        int b1=0;
        if(details==true)
            b1=1;
        String category=obj.getString("category");
        String imageRes=obj.getString("imageRes");			
        preparedStmt.setString (1, title);
        preparedStmt.setString (2, desc);
        preparedStmt.setString (3, imgurl);
        preparedStmt.setDouble (4, mrp);
        preparedStmt.setDouble (5, amt);
        preparedStmt.setString (6, purl);
        preparedStmt.setInt (7, is);
        preparedStmt.setInt (8, cod1);
        preparedStmt.setString (9, size);
        preparedStmt.setString (10, su);
        preparedStmt.setString (11, color);
	preparedStmt.setString (12, brand);
        preparedStmt.setInt (13, b1);
        preparedStmt.setString (14, category);
        preparedStmt.setString (15, imageRes);	    
	
        
        preparedStmt.execute();
        return Response.status(200).entity("Success").build();
    }

    @Path("{id}")
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response patchJson (String content, @PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException, JSONException{
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();        
        String query = "select * from flipkart where id=\'"+id+"\'";
        ResultSet rs=stmt.executeQuery(query);
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            return Response.status(400).entity("Invalid ID").build();
        }
        rs.beforeFirst();
        
        JSONObject obj=new JSONObject(content);
        Iterator<?> keys=obj.keys();
        String key;
        //String value;
        String str="";
        while(keys.hasNext())
        {
            key=(String)keys.next();
            if((key.equals("category"))||(key.equals("title"))||(key.equals("imageUrl"))||(key.equals("imageRes"))||(key.equals("productUrl"))||(key.equals("brand"))||(key.equals("size"))||(key.equals("color"))||(key.equals("description"))||(key.equals("sizeUnit")))
                str+=key+"='"+obj.getString(key)+"'";
            else if((key.equals("mrp"))||(key.equals("amount")))
                str+=key+"='"+obj.getDouble(key)+"'";
            else if((key.equals("inStock"))||(key.equals("cod"))||(key.equals("details")))
                str+=key+"='"+obj.getInt(key)+"'";
            else
                return Response.status(400).entity("Invalid column name").build();
            if(keys.hasNext())
                str+=",";
            else
                str+=" ";
        }
        System.out.println(str);
        query="update flipkart set "+str+"where id='"+id+"'";
        stmt.executeUpdate(query);
        return Response.status(200).entity("Success").build();
    }
    
    @LOCK
    public Response lockRes(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where resource_name='flipkart'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt!=0)
            return Response.status(403).entity("You do not have the permission to lock the resource").build();
        stmt.executeUpdate("insert into lock_resource(username,resource_name) values ('"+uname+"','flipkart')");
        return Response.status(200).entity("Locked").build();
    }
    
    @UNLOCK
    public Response unlockRes(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where username='"+uname+"' and resource_name='flipkart'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(403).entity("You do not have the permission to unlock the resource").build();
        stmt.executeUpdate("delete from lock_resource where username='"+uname+"' and resource_name='flipkart'");
        return Response.status(200).entity("Unlocked").build();
    }
    
    @Path("{id}")
    @COPY
    public Response copyRes(@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @HeaderParam("dest") String dest) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select * from flipkart where id='"+id+"'");
        rs.next();
        String title=rs.getString("title");
        String url=rs.getString("productUrl");
        String imgUrl=rs.getString("imageUrl");
        String imgRes=rs.getString("imageRes");
        String category=rs.getString("category");
        String brand=rs.getString("brand");
        String color=rs.getString("color");
        double mrp=rs.getDouble("mrp");
        double amount=rs.getDouble("amount");
        int details=rs.getInt("details");
        int inStock=rs.getInt("inStock");
        int cod=rs.getInt("cod");
        String size=rs.getString("size");
        String desc=rs.getString("description");
        String sizeUnit=rs.getString("sizeUnit");
        if(dest.equals("localhost:8080/Minor2/webresources/amazon") || dest.equals("http://localhost:8080/Minor2/webresources/amazon"))
        {
            String query = "insert into amazon (id, title, url, imgUrl, imgRes, category, brand, color, mrp, amount, model, details) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, id);
            preparedStmt.setString (2, title);
            preparedStmt.setString (3, url);
            preparedStmt.setString (4, imgUrl);
            preparedStmt.setString (5, imgRes);
            preparedStmt.setString (6, category);
            preparedStmt.setString (7, brand);
            preparedStmt.setString (8, color);
            preparedStmt.setDouble (9, mrp);
            preparedStmt.setDouble (10, amount);
            preparedStmt.setString (11, "N/A");
            preparedStmt.setInt (12, details);
            preparedStmt.execute();
            return Response.status(200).entity("Success").build();
        }
        else
            return Response.status(400).entity("Not a valid destiation").build();
    }
    
    @Path("{id}/{resource2}/{id2}")
    @LINK
    public Response linkRes(@PathParam("id") String id, @PathParam("resource2") String r2, @PathParam("id2") String id2, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from flipkart where id='"+id+"'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(400).entity("Resource not available").build();
        rs=stmt.executeQuery("select count(*) as cnt from "+r2+" where id='"+id2+"'");
        rs.next();
        cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(400).entity("Resource not available").build();
        String query=("insert into product(amazon,flipkart) values (?,?)");
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setString(1,id2);
        pstmt.setString(2,id);
        pstmt.execute();
        return Response.status(200).entity("Success").build();
    }
    
    @Path("{id}/{resource2}/{id2}")
    @UNLINK
    public Response unlinkRes(@PathParam("id") String id, @PathParam("resource2") String r2, @PathParam("id2") String id2, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from product where amazon='"+id2+"' and flipkart='"+id+"'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(400).entity("Resource not linked").build();
        String query=("delete from product where amazon='"+id2+"' and flipkart='"+id+"'");
        stmt.executeUpdate(query);
        return Response.status(200).entity("Success").build();
    }
    
    @PROPFIND
    @Produces(MediaType.APPLICATION_JSON)
    public Response propFind(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException, JSONException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        JSONObject prop=new JSONObject();
        prop.put("author","vishesh");
        prop.put("editor","vishesh");
        rs=stmt.executeQuery("select create_time from information_schema.tables where table_name='flipkart'");
        rs.next();
        Timestamp timestamp = rs.getTimestamp("create_time");
        prop.put("creationdate",timestamp.toString());
        prop.put("displayname","Flipkart");
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where resource_name='flipkart'");
        //rs.beforeFirst();
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt!=0)
        {
            ResultSet rs1=stmt.executeQuery("select username from lock_resource where resource_name='flipkart'");
            rs1.next();
            prop.put("lock_user",rs1.getString("username"));
        }
        prop.put("resourcetype","singleton");
        return Response.status(200).entity(prop.toString()).build();
    }
    
}
