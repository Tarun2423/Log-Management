
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Addnotification extends HttpServlet {

	protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,ServletException{
		
		
	 	String url = "jdbc:postgresql://localhost:5432/Log";
	    String dbUsername = "postgres";
	    String dbPassword = "root";
	    String query="INSERT INTO notification(eid,evalue,etime) VALUES(?,?,?)";
	    String restaurant=req.getParameter("eid");
	    String customer=req.getParameter("evalue");
	    String items=req.getParameter("etime");
	    // String amount=req.getParameter("amount");
	    // String status="Placed";
	    
		try {
			Class.forName("org.postgresql.Driver");
			Connection con=DriverManager.getConnection(url, dbUsername, dbPassword);
			PreparedStatement pst=con.prepareStatement(query);
			pst.setString(1, restaurant);
			pst.setString(2, customer);
			pst.setString(3, items);
			pst.executeUpdate();
			System.out.println("Successfully added");
			res.getWriter().println("success");
			con.close();
		}catch (Exception e) {
			// TODO: handle exception
			System.out.print("some error");
		}
		
	    
	}
}
