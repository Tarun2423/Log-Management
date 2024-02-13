public class notify {
    String eid,evalue,etime;

    public notify(String eid, String evalue, String etime) {
        this.eid = eid;
        this.evalue = evalue;
        this.etime = etime;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEvalue() {
        return evalue;
    }

    public void setEvalue(String evalue) {
        this.evalue = evalue;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }
}
