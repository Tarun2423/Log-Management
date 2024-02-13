public class EventLogReader {
    static {
        System.load("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\webapps\\LogManagement\\WEB-INF\\lib\\libintl-9.dll");
        System.load("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\webapps\\LogManagement\\WEB-INF\\src\\libpq.dll");
        System.load("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0_Tomcat9new\\webapps\\LogManagement\\WEB-INF\\classes\\EventLogReader.dll");
    }

    public native void startEventLogReader();

    public static void main(String[] args) {
        System.out.println("Starting EventLogReader");
        EventLogReader reader = new EventLogReader();
        reader.startEventLogReader();
    }
}
