
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jose.shaded.gson.Gson;






public class GetNotification extends HttpServlet{
	
	protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,ServletException{
		
		ArrayList<notify> resList=new ArrayList<notify>();
	 	String url = "jdbc:postgresql://localhost:5432/Log";
	    String dbUsername = "postgres";
	    String dbPassword = "root";
	    String query="SELECT * FROM notification";
	   
	   
	try {
		Class.forName("org.postgresql.Driver");
		Connection con=DriverManager.getConnection(url, dbUsername, dbPassword);
		PreparedStatement pst=con.prepareStatement(query);
		ResultSet rs=pst.executeQuery();
		while(rs.next()) {
		
			String resname=rs.getString("eid");
			String customer=rs.getString("evalue");
			String items=rs.getString("etime");
			
			resList.add((new notify(resname,customer,items)));
		}
		   Gson gson = new Gson();
           String json = gson.toJson(resList);
           res.setContentType("application/json");
            res.getWriter().write(json);
       
	}catch (Exception e) {
		// TODO: handle exception
		System.out.print("some error");
	}
	
		
		
	}

}
