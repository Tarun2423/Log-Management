import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.http.HttpServlet.*;

import com.nimbusds.jose.shaded.gson.Gson;


public class Login extends HttpServlet{
	
	protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException{
			res.setHeader("Access-Control-Allow-Origin","*");
        		res.setContentType("text/html");
		try {
			    Class.forName("org.postgresql.Driver");
			    String url = "jdbc:postgresql://localhost:5432/Log";
			    String dbUsername = "postgres";
			    String dbPassword = "root";
			    String query="SELECT * FROM users WHERE name=? AND password=?";
			    String name=req.getParameter("name");
			    String password=req.getParameter("password");
			    System.out.println("name="+name);
			    System.out.println(password);
			    Connection con=DriverManager.getConnection(url, dbUsername, dbPassword);
			    PreparedStatement pst=con.prepareStatement(query);
			    pst.setString(1, name);
			    pst.setString(2,password);
			    ResultSet rs=pst.executeQuery();
			   
			    if(rs.next()) {
				    	int rid=rs.getInt("id");
				    	String rname=rs.getString("name");
				    	String role=rs.getString("role");
				    	User u=new User(rid,rname,role);
				    	Gson gson = new Gson();
	                    String json = gson.toJson(u);
	                    res.setContentType("application/json");
	                    res.getWriter().write(json);
			    }
				else{
				
				res.getWriter().write("Incorrect");
				
				}
		}
		catch(Exception e) {
			res.getWriter().write("error");
		}
		
		
	}

}
