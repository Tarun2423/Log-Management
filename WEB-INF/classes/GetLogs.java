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


public class GetLogs extends HttpServlet{
	
	protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException{
			res.setHeader("Access-Control-Allow-Origin","*");
        		res.setContentType("text/html");
		try {
			    Class.forName("org.postgresql.Driver");
			    String url = "jdbc:postgresql://localhost:5432/Log";
			    String dbUsername = "postgres";
			    String dbPassword = "root";
			    String query="SELECT * FROM logs";
			    Connection con=DriverManager.getConnection(url, dbUsername, dbPassword);
			    PreparedStatement pst=con.prepareStatement(query);
			    ResultSet rs=pst.executeQuery();
			   ArrayList<LogData> logData=new ArrayList<LogData>();
			    while(rs.next()) {
				    	int rid=rs.getInt("id");
				    	String source=rs.getString("source");
				    	String message=rs.getString("message");
				    	logData.add(new LogData(source,message));
				    	
			    }
                    Gson gson = new Gson();
	                    String json = gson.toJson(logData);
	                    res.setContentType("application/json");
	                    res.getWriter().write(json);
			
		}
		catch(Exception e) {
			res.getWriter().write("error");
		}
		
		
	}

}
