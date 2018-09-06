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
@Path("amazon")
public class Amazon {

    public  ResultSet rs=null;
    public Statement stmt;
    public Connection con;
    @Context
    private UriInfo context;

    public Amazon() {
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
    
    public JSONArray convert(ResultSet r) throws SQLException, JSONException
    {
        ResultSetMetaData rsmd = r.getMetaData();
        r.beforeFirst();
        JSONArray json = new JSONArray();
        while(r.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            String id="";
            int details=0;
            for(int i=1; i<numColumns+1; i++) {
                String column_name = rsmd.getColumnLabel(i); 
                if(column_name.equals("id"))
                    id=r.getString("id");
                if(column_name.equals("details"))
                    details=r.getInt("details");
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
            if(details==1)
            {
                /*System.out.println("select feature from feature_amazon where id='"+id+"'");
                rs=stmt.executeQuery("select feature from feature_amazon where id='"+id+"'");
                
                JSONArray features=convert(rs);
                System.out.println(features.getString(0));
                obj.put("features",features);*/
            }
            json.put(obj);
        }
        return json;
    }

    public boolean validUser(String uname, String password) throws SQLException
    {
        int flag=0;
        System.out.println("select * from user where username='"+uname+"'");
        rs=stmt.executeQuery("select * from user where username='"+uname+"'");
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            flag=1;
        }
        else
        {
            int id=rs.getInt("id");
            System.out.println("select * from validation where id='"+id+"'");
            ResultSet rs1=stmt.executeQuery("select * from validation where id='"+id+"'");
            rs1.next();
            //System.out.println("1");
            String key=rs1.getString(2);
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
    public Response getJson(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort) {
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
            //String cond="";
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="where inStock='1'";
            System.out.println("select * from amazon "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon "+str+" "+str1+" "+"limit 50");  
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
            rs=stmt.executeQuery("select * from amazon where id='"+id+"'");  
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
    public Response getJsonByCat(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("category") String cat)
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
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from amazon where category='"+cat+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon where category='"+cat+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByBrand(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("brand") String brand)
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
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from amazon where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByTitle(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("title") String title)
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
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="and inStock='1'";
            System.out.println("select * from amazon where Match(title) Against('"+title+"')  "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon where Match(title) Against('"+title+"') "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByMrp(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("type") String type, @QueryParam("value") String value)
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
            System.out.println("select * from amazon where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByAmount(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("type") String type, @QueryParam("value") String value)
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
            System.out.println("select * from amazon where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
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
			.allow("POST", "GET", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH", "LINK", "UNLINK", "LOCK", "UNLOCK", "PROPFIND", "COPY", "VIEW")

                .header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Credentials", "true")
			.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, HEAD, PATCH, LINK, UNLINK, LOCK, UNLOCK, PROPFIND, COPY, VIEW")
			.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
			.build();
    }
    
    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response delEntry (@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException { 
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();        
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where username='"+uname+"' and resource_name='amazon'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(403).entity("You do not have the permission to lock the resource").build();
        String query = "select * from flipkart where id=\'"+id+"\'";
        ResultSet rs=stmt.executeQuery(query);
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            return Response.status(400).entity("Invalid ID").build();
        }
        query="delete from amazon where id=\'"+id+"\'";
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
        String id,title,imgurl,purl,brand,color,category,imageRes,query,model,dup="";
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
            //desc = obj.getString("description");
            //if(desc.equals(""))
              //  desc="No description";
            imgurl=obj.getString("imgUrl");
            mrp=obj.getDouble("mrp");
            amt=obj.getDouble("amount");
            purl=obj.getString("url");
            brand=obj.getString("brand");
            color=obj.getString("color");
            if(color.equals(""))
                color="N/A";
            details=obj.optBoolean("details",false);
            b1=0;
            if(details==true)
                b1=1;
            category=obj.getString("category");
            imageRes=obj.getString("imgRes");
            model=obj.getString("model");
            if(model.equals(""))
                model="N/A";
            query = "select id from amazon where id=\'"+id+"\'";
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
            query = "insert into amazon (id, title, url, imgUrl, imgRes, category, brand, color, mrp, amount, model, details) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, id);
            preparedStmt.setString (2, title);
            preparedStmt.setString (3, purl);
            preparedStmt.setString (4, imgurl);
            preparedStmt.setString (5, imageRes);
            preparedStmt.setString (6, category);
            preparedStmt.setString (7, brand);
            preparedStmt.setString (8, color);
            preparedStmt.setDouble (9, mrp);
            preparedStmt.setDouble (10, amt);
            preparedStmt.setString (11, model);
            preparedStmt.setInt (12, b1);
            
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
        String query = "select * from amazon where id=\'"+id+"\'";
        ResultSet rs=stmt.executeQuery(query);
        rs.last();
        int row=rs.getRow();
        if(row==0)
        {
            return Response.status(400).entity("Invalid ID").build();
        }
        rs.beforeFirst();
        
        JSONObject obj = new JSONObject(content);
        String title = obj.getString("title");
        String imgurl=obj.getString("imgUrl");
        double mrp=obj.getDouble("mrp");
        double amt=obj.getDouble("amount");
        String purl=obj.getString("url");
        String brand=obj.getString("brand");
        String color=obj.getString("color");
        if(color.equals(""))
            color="N/A";
        boolean details=obj.optBoolean("details",false);
        int b1=0;
        if(details==true)
            b1=1;
        String category=obj.getString("category");
        String imageRes=obj.getString("imgRes");			
        String model=obj.getString("model");
        if(model.equals(""))
            model="N/A";
        query = "update amazon set title = ?, url = ?, imgUrl = ?, imgRes = ?, category = ?, brand = ?, color = ?, mrp = ?, amount = ?, model = ?, details = ? where id=\'"+id+"\'";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setString (1, title);
        preparedStmt.setString (2, purl);
        preparedStmt.setString (3, imgurl);
        preparedStmt.setString (4, imageRes);
        preparedStmt.setString (5, category);
        preparedStmt.setString (6, brand);
        preparedStmt.setString (7, color);
        preparedStmt.setDouble (8, mrp);
        preparedStmt.setDouble (9, amt);
        preparedStmt.setString (10, model);
        preparedStmt.setInt (11, b1);
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
            if((key.equals("category"))||(key.equals("title"))||(key.equals("imgUrl"))||(key.equals("imgRes"))||(key.equals("url"))||(key.equals("brand"))||(key.equals("model"))||(key.equals("color")))
                str+=key+"='"+obj.getString(key)+"'";
            else if((key.equals("mrp"))||(key.equals("amount")))
                str+=key+"='"+obj.getDouble(key)+"'";
            else if(key.equals("details"))
                str+=key+"='"+obj.getInt(key)+"'";
            else
                return Response.status(400).entity("Invalid column name").build();
            if(keys.hasNext())
                str+=",";
            else
                str+=" ";
        }
        System.out.println(str);
        query="update amazon set "+str+"where id='"+id+"'";
        stmt.executeUpdate(query);
        return Response.status(200).entity(query).build();
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
        rs=stmt.executeQuery("select create_time from information_schema.tables where table_name='amazon'");
        rs.next();
        Timestamp timestamp = rs.getTimestamp("create_time");
        prop.put("creationdate",timestamp.toString());
        prop.put("displayname","Amazon");
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where resource_name='amazon'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt!=0)
        {
            rs=stmt.executeQuery("select username from lock_resource where reource_name='amazon'");
            prop.put("lock_user",rs.getString("username"));
        }
        prop.put("resourcetype","collection");
        return Response.status(200).entity(prop.toString()).build();
    }
    
    @Path("{id}/{resource2}/{id2}")
    @UNLINK
    public Response unlinkRes(@PathParam("id") String id, @PathParam("resource2") String r2, @PathParam("id2") String id2, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from product where amazon='"+id+"' and flipkart='"+id2+"'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(400).entity("Resource not linked").build();
        String query=("delete from product where amazon='"+id+"' and flipkart='"+id2+"'");
        stmt.executeUpdate(query);
        return Response.status(200).entity("Success").build();
    }
    
    @Path("{id}/{resource2}/{id2}")
    @LINK
    public Response linkRes(@PathParam("id") String id, @PathParam("resource2") String r2, @PathParam("id2") String id2, @HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from amazon where id='"+id+"'");
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
        pstmt.setString(1,id);
        pstmt.setString(2,id2);
        pstmt.execute();
        return Response.status(200).entity("Success").build();
    }
    
    @Path("{id}")
    @COPY
    public Response copyRes(@PathParam("id") String id, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @HeaderParam("dest") String dest) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select * from amazon where id='"+id+"'");
        rs.next();
        String title=rs.getString("title");
        String url=rs.getString("url");
        String imgUrl=rs.getString("imgUrl");
        String imgRes=rs.getString("imgRes");
        String category=rs.getString("category");
        String brand=rs.getString("brand");
        String color=rs.getString("color");
        double mrp=rs.getDouble("mrp");
        double amount=rs.getDouble("amount");
        String model=rs.getString("model");
        int details=rs.getInt("details");
        if(dest.equals("localhost:8080/Minor2/webresources/flipkart") || dest.equals("http://localhost:8080/Minor2/webresources/flipkart"))
        {
            String query = "insert into flipkart (id, category, title, imageUrl, imageRes, mrp, amount, productUrl, brand, inStock, cod, details, size, color, description, sizeUnit) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, id);
            preparedStmt.setString (2, category);
            preparedStmt.setString (3, title);
            preparedStmt.setString (4, imgUrl);
            preparedStmt.setString (5, imgRes);
            preparedStmt.setDouble (6, mrp);
            preparedStmt.setDouble (7, amount);
            preparedStmt.setString (8, url);
            preparedStmt.setString (9, brand);
            preparedStmt.setInt (10, 0);
            preparedStmt.setInt (11, 0);
            preparedStmt.setInt (12, details);
            preparedStmt.setString (13, "N/A");
            preparedStmt.setString (14, color);
            preparedStmt.setString (15, "N/A");
            preparedStmt.setString (16, "N/A");
            preparedStmt.execute();
            return Response.status(200).entity("Success").build();
        }
        else
            return Response.status(400).entity("Not a valid destiation").build();
    }
    
    @UNLOCK
    public Response unlockRes(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where username='"+uname+"' and resource_name='amazon'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt==0)
            return Response.status(403).entity("You do not have the permission to unlock the resource").build();
        stmt.executeUpdate("delete from lock_resource where username='"+uname+"' and resource_name='amazon'");
        return Response.status(200).entity("Unlocked").build();
    }
    
    @LOCK
    public Response lockRes(@HeaderParam("user-name") String uname, @HeaderParam("password") String password) throws SQLException
    {
        if(!validUser(uname,password))
            return Response.status(400).entity("{\"Error\":\"Invalid username or password\"}").build();     
        rs=stmt.executeQuery("select count(*) as cnt from lock_resource where resource_name='amazon'");
        rs.next();
        int cnt=rs.getInt("cnt");
        if(cnt!=0)
            return Response.status(403).entity("You do not have the permission to lock the resource").build();
        stmt.executeUpdate("insert into lock_resource(username,resource_name) values ('"+uname+"','amazon')");
        return Response.status(200).entity("Locked").build();
    }
    
    @VIEW
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort) {
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
            //if(inStock!=null && inStock.equals("yes"))
              //  cond="where inStock='1'";
            System.out.println("select * from amazon_view "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view "+str+" "+str1+" "+"limit 50");  
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
            rs=stmt.executeQuery("select * from amazon_view where id='"+id+"'");  
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
    public Response getJsonByCatView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("category") String cat)
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
            
            System.out.println("select * from amazon_view where category='"+cat+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view where category='"+cat+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByBrandView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("brand") String brand)
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
            
            System.out.println("select * from amazon_view where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view where brand='"+brand+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByTitleView(@HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("title") String title)
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
            System.out.println("select * from amazon_view where Match(title) Against('"+title+"')  "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view where Match(title) Against('"+title+"') "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByMrpView(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("type") String type, @QueryParam("value") String value)
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
            System.out.println("select * from amazon_view where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view where "+cond+"mrp between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
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
    public Response getJsonByAmountView(@PathParam("from") String from, @PathParam("to") String to, @HeaderParam("user-name") String uname, @HeaderParam("password") String password, @QueryParam("sort") String sort, @QueryParam("type") String type, @QueryParam("value") String value)
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
            System.out.println("select * from amazon_view where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");
            rs=stmt.executeQuery("select * from amazon_view where "+cond+"amount between '"+from+"' and '"+to+"' "+str+" "+str1+" "+"limit 50");  
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
}
