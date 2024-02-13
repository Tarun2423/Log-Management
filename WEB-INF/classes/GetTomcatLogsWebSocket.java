import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.nimbusds.jose.shaded.gson.Gson;
@ServerEndpoint(value = "/getTomcatLogsWebSocket")
public class GetTomcatLogsWebSocket {

    private static ArrayList<Session> sessions = new ArrayList<>();
    private static long lastKnownLine;
    private static Timer timer;

    @OnOpen
    public void onOpen(Session session) {
        synchronized(sessions) {
            sessions.add(session);
        }
        sendlast3(session);
        sendLogsToClient(session);
        startTimer();
    }

    @OnClose
    public void onClose(Session session) {
        synchronized(sessions) {
            sessions.remove(session);
            if (sessions.isEmpty() && timer != null) {
                timer.cancel(); 
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
       sendDataBasedOnDate(session,message);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentLines = getCurrentLines();
                if (currentLines > lastKnownLine) {
                    lastKnownLine = currentLines;
                    sendUpdatedLogsToClient();
                    
                }
            }
        }, 0, 60000); 
    }
    private void sendlast3(Session session) {
        LocalDate currentDate = LocalDate.now();
        LocalDate yesterdayDate = currentDate.minusDays(1);
        LocalDate dayBeforeYesterdayDate = currentDate.minusDays(2);
        LinkedList<LocalDate> dates = new LinkedList<>();
        dates.add(currentDate);
        dates.add(yesterdayDate);
        dates.add(dayBeforeYesterdayDate);
        StringBuilder result = new StringBuilder();
        for (LocalDate date : dates) {
            String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina." + date + ".log";
            File file = new File(logFilePath);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                    String line;
                    long linesCount = 0;
                    while ((line = reader.readLine()) != null) {
                        linesCount++;
                    }
                    result.append(linesCount).append("|");
                } catch (IOException e) {
                    e.printStackTrace();
                    result.append("not found").append("|");
                }
            } else {
                System.out.println("file not found");
            }
        }
        try {
            session.getBasicRemote().sendText(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendLogsToClient(Session session) {
        LocalDate today = LocalDate.now();
        String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina." + today + ".log";
        System.out.println(logFilePath);
        File f = new File(logFilePath);
        if (f.exists()){
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            String type;
            Long liness = Long.valueOf(0);

            ArrayList<String> lines = new ArrayList<String>();
            String types;
            int info = 0, war = 0, others = 0;
            while ((line = reader.readLine()) != null) {
                String logData;
                liness++;
                String[] parts = line.split(" ");

                if (parts.length > 1) {
                    if (line.contains("WARNING")) {
                        type = "WARNING";
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        war++;
                    } else if (line.contains("INFO")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "INFO";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        info++;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "OTHERS";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        others++;
                    }
                    lines.add(logData);
                }
            }

            lastKnownLine = liness;
            System.out.println(info);
            System.out.println(war);
            types = "info=" + Integer.toString(info) + "|warning=" + Integer.toString(war) + "|others=" + Integer.toString(others);
            lines.add(types);
            Gson gson = new Gson();
            String json = gson.toJson(lines);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    else{
        System.out.println("file not found");
    }
    }
    private void sendDataBasedOnDate(Session session,String message) {
        
        String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina." + message+ ".log";
        System.out.println(logFilePath);
        File f = new File(logFilePath);
        if (f.exists()){
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            String type;
            Long liness = Long.valueOf(0);

            ArrayList<String> lines = new ArrayList<String>();
            String types;
            int info = 0, war = 0, others = 0;
            while ((line = reader.readLine()) != null) {
                String logData;
                liness++;
                String[] parts = line.split(" ");

                if (parts.length > 1) {
                    if (line.contains("WARNING")) {
                        type = "WARNING";
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        war++;
                    } else if (line.contains("INFO")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "INFO";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        info++;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "OTHERS";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        others++;
                    }
                    lines.add(logData);
                }
            }

            lastKnownLine = liness;
            System.out.println(info);
            System.out.println(war);
            types = "info=" + Integer.toString(info) + "|warning=" + Integer.toString(war) + "|others=" + Integer.toString(others);
            lines.add(types);
            Gson gson = new Gson();
            String json = gson.toJson(lines);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
       
        }
    }else{
        System.out.println("file not found");
    }
    }

    private long getCurrentLines() {
        LocalDate today = LocalDate.now();
        String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina." + today + ".log";

        long noOfLines = 0;
        File f = new File(logFilePath);
        if (f.exists()){
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                noOfLines++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return noOfLines;
    }
    else{
        System.out.println("file not found");
        return noOfLines;
    }
       
    }

    private void sendUpdatedLogsToClient() {
        LocalDate today = LocalDate.now();
        String logFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\logs\\catalina." + today + ".log";
        System.out.println(logFilePath);
        File f = new File(logFilePath);
        if (f.exists()){
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            String type;
            Long liness = Long.valueOf(0);
            ArrayList<String> lines = new ArrayList<String>();
            String types;
            int info = 0, war = 0, others = 0;
            while ((line = reader.readLine()) != null) {
                String logData;
                liness++;
                String[] parts = line.split(" ");

                if (parts.length > 1) {
                    if (line.contains("WARNING")) {
                        type = "WARNING";
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        war++;
                    } else if (line.contains("INFO")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "INFO";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        info++;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            sb.append(parts[i]);
                            if (i < parts.length - 1) {
                                sb.append(" ");
                            }
                        }
                        type = "OTHERS";
                        logData = parts[0] + "|" + parts[1]+"|" + "Tomcat" + "|" + type + "|" + sb + "|" + "text";
                        others++;
                    }
                    lines.add(logData);
                }
            
            }
            System.out.println(info);
            System.out.println(war);
            types = "info=" + Integer.toString(info) + "|warning=" + Integer.toString(war) + "|others=" + Integer.toString(others);
            lines.add(types);
            Gson gson = new Gson();
            String json = gson.toJson(lines);
            for (Session session : sessions) {
                session.getBasicRemote().sendText(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else{
            System.out.println("file not found");
        }
    }
}
