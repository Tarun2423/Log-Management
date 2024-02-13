import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class EventLogReader {
    static ArrayList<String> logs = new ArrayList<String>();
    static long logsProcessed = 0;
    static int i = 0;
    static Connection con = null; 

    String url = "jdbc:postgresql://localhost:5432/Log";
    String dbUsername = "postgres";
    String dbPassword = "root";
    String query = "INSERT INTO eventlogs(eventid, eventvalue, time) VALUES (?, ?, ?)";
    public native long getNumLogsProcessed();
    public native void retrieveSecurityLogs();
    
    public void openConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertLogIntoDB(int name,String Category,String timeGenerated ) {
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, name);
            pst.setString(2, Category);
            pst.setString(3, timeGenerated);
         
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processLogMessage(int eventID, String Category, String timeGenerated) {
        if (i >= logsProcessed) {
            logs.add("Received log message for event ID " + eventID + ": " +"Category: "+ Category + "time: " + timeGenerated);
            insertLogIntoDB(eventID, Category, timeGenerated);
            i++;
        } else {
            i++;
        }
    }

    static {
        System.loadLibrary("EventLogReader");
    }

    public static void main(String[] args) {
        EventLogReader reader = new EventLogReader();
        reader.openConnection(); 
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logsProcessed = reader.getNumLogsProcessed();
                System.out.println("Number of logs processed: " + logsProcessed);
                reader.retrieveSecurityLogs();
                System.out.println(logs.size());
                System.out.println("i = " + i);
                i = 0;
            }
        }, 0, 120000);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                reader.closeConnection();
            }
        });
    }
}
