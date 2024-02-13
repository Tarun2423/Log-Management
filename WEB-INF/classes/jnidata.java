public class jnidata {
    int id;
    String message;
    public jnidata(int id, String message, String time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }
    String time;
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
   
}