package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.sql.ResultSet;

public final class dash_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <body>\n");
      out.write("        ");
 if(request.getSession(false)==null || session.isNew()){ 
      out.write("\n");
      out.write("        <a href=\"index.html\">Please login</a>\n");
      out.write("        ");
 }else if(((Integer)session.getAttribute("isAuth")).equals(1)) { 
        if(((Integer)session.getAttribute("isKey")).equals(1))
        {
            
      out.write("\n");
      out.write("            <div>\n");
      out.write("            username=");
      out.print((String)(session.getAttribute("username")));
      out.write("\n");
      out.write("            <br>\n");
      out.write("            key=");
      out.print((String)(session.getAttribute("key")));
      out.write("\n");
      out.write("            </div>\n");
      out.write("            ");

        }
        
      out.write("\n");
      out.write("        <input type=\"button\" name=\"generate\" value=\"Generate\" onclick=\"window.open('http://localhost:8080/Minor2/Generate','_self');\"/>\n");
      out.write("        <h1>Hello World1! ");
      out.print((Integer)session.getAttribute("id"));
      out.write("</h1>\n");
      out.write("        <a href=\"Logout\">Logout</a>\n");
      out.write("        ");
}else{
      out.write("\n");
      out.write("        <a href=\"index.html\">Please login</a>\n");
      out.write("        ");
}
      out.write("\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
