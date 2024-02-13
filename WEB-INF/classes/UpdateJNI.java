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
import java.util.*;
import com.nimbusds.jose.shaded.gson.Gson;


public class UpdateJNI extends HttpServlet{
	
	protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException{
			res.setHeader("Access-Control-Allow-Origin","*");
        		res.setContentType("text/html");
              String sk=req.getParameter("skip");
              System.out.println(sk);
		try {
			    Class.forName("org.postgresql.Driver");
			    String url = "jdbc:postgresql://localhost:5432/Log";
			    String dbUsername = "postgres";
			    String dbPassword = "root";
			    String query="SELECT * FROM eventlogs OFFSET "+sk;
			    Connection con=DriverManager.getConnection(url, dbUsername, dbPassword);
			    PreparedStatement pst=con.prepareStatement(query);
			    ResultSet rs=pst.executeQuery();
			   ArrayList<jnidata> logData=new ArrayList<jnidata>();
			    while(rs.next()) {
				    	int rid=rs.getInt("eventid");
				    	String message=rs.getString("eventvalue");
				    	String time=rs.getString("time");
				    	logData.add(new jnidata(rid,message,time));
				    	
			    }
                    Gson gson = new Gson();
					
	                    String json = gson.toJson(logData);
	                    res.setContentType("application/json");
	                    res.getWriter().write(json);
			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			res.getWriter().write("error");
		}
		
		
	}

}
