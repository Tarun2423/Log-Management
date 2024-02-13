import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class LogReceiver extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");

        try {
            // Read the JSON data from the request body
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            // Parse the JSON data
            JSONObject jsonData = new JSONObject(requestBody.toString());
            String source = jsonData.getString("source");
            String level = jsonData.getString("level");
            String message = jsonData.getString("message");
            String format = jsonData.getString("format");

            // Establish database connection and insert log data
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/Log";
                String dbUsername = "postgres";
                String dbPassword = "root";

            String query = "INSERT INTO logs(timestamp, source, level, message, format) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
                 PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, source);
                pst.setString(2, level);
                pst.setString(3, message);
                pst.setString(4, format);
                pst.executeUpdate();
            }

            response.getWriter().write("success");

        } catch (Exception e) {
            response.getWriter().write("error");
        }
    }
}
