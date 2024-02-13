import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import org.json.JSONObject;
import com.nimbusds.jose.shaded.gson.Gson;
public class GetTomcatlogs extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                LocalDate today = LocalDate.now();
        
      String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina."+today+".log";
      System.out.println(logFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            String type;
     ArrayList<String> lines = new ArrayList<String>();
     String types;
     int info=0,war=0,others=0;
            while ((line = reader.readLine()) != null) {
                String logData;
                
                if (line.contains("WARNING")) {
                   type="WARNING";
                    logData = "Tomcat"+"|"+type+"|"+line.split("]")[1]+"|"+"text";
                    war++;
                }
                else if(line.contains("INFO")) {type="INFO";logData = 
                "Tomcat"+"|"+type+"|"+line.split("]")[1]+"|"+"text";
                info++;
            }
            else{
                type="OTHERS";logData = 
         "Tomcat"+"|"+type+"|"+line.split("]")[1]+"|"+"text";others++;
            }
            lines.add(logData);
            }
            System.out.println(info);
            System.out.println(war);
            types="info="+Integer.toString(info)+"|warning="+Integer.toString(war)+"|others="+Integer.toString(others);
            lines.add(types);
            Gson gson = new Gson();
            String json = gson.toJson(lines);
            response.setContentType("application/json");
            response.getWriter().write(json);
            
            }
       catch (IOException e) {
            e.printStackTrace();
        }

        //     // Parse the JSON data
        //     JSONObject jsonData = new JSONObject(requestBody.toString());
        //     String source = jsonData.getString("source");
        //     String level = jsonData.getString("level");
        //     String message = jsonData.getString("message");
        //     String format = jsonData.getString("format");

        //     // Establish database connection and insert log data
        //     Class.forName("org.postgresql.Driver");
        //     String url = "jdbc:postgresql://localhost:5432/Log";
        //     String dbUsername = "postgres";
        //     String dbPassword = "root";

        //     String query = "INSERT INTO logs(timestamp, source, level, message, format) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
        //     try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
        //          PreparedStatement pst = con.prepareStatement(query)) {
        //         pst.setString(1, source);
        //         pst.setString(2, level);
        //         pst.setString(3, message);
        //         pst.setString(4, format);
        //         pst.executeUpdate();
        //     }

        //     response.getWriter().write("success");

        // } catch (Exception e) {
        //     response.getWriter().write("error");
        // }
    }
}
