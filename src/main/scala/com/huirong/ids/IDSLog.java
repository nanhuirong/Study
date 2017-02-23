package com.huirong.ids;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huirong on 17-2-23.
 */
public class IDSLog {
//    public static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.US);
    private Date time;
    private String dt;
    private String level;
    private String id;
    private String type;
//    private JSONObject source;
    private String srcIP;
    private String srcPORT;
    private String srcMAC;
//    private JSONObject destination;
    private String destIP;
    private String destPORT;
    private String destMAC;
    private String protocol;
    private String subject;
    private String message;

    public IDSLog(Date time, String dt, String level, String id, String type,
                  String srcIP, String srcPORT, String srcMAC, String destIP,
                  String destPORT, String destMAC, String protocol, String subject,
                  String message) {
        this.time = time;
        this.dt = dt;
        this.level = level;
        this.id = id;
        this.type = type;
        this.srcIP = srcIP;
        this.srcPORT = srcPORT;
        this.srcMAC = srcMAC;
        this.destIP = destIP;
        this.destPORT = destPORT;
        this.destMAC = destMAC;
        this.protocol = protocol;
        this.subject = subject;
        this.message = message;
    }

    public static IDSLog parse(String line){
        IDSLog log = null;
        JSONObject obj = JSONParser.parse(line);
        if (obj != null){
            try {
                Date time = new Date(obj.getLong("time"));
                String dt = obj.getString("dt");
                String level = obj.get("level").toString();
                String id = obj.getString("id");
                String type = obj.getString("type");
                String protocol = obj.getString("protocol");
                String subject = obj.getString("subject");
                String message = obj.getString("message");
                JSONObject source = obj.getJSONObject("source");
                String srcIP = source.getString("ip");
                String srcPORT = source.get("port").toString();
                String srcMAC = source.getString("mac");
                JSONObject destination = obj.getJSONObject("destination");
                String destIP = destination.getString("ip");
                String destPORT = destination.get("port").toString();
                String destMAC = destination.getString("mac");
                log = new IDSLog(time, dt, level, id, type, srcIP, srcPORT, srcMAC,
                        destIP, destPORT, destMAC, protocol, subject, message);
            }catch (JSONException e){
                log = null;
            }finally {
                return log;
            }
        }else {
            return null;
        }
    }



    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getSrcPORT() {
        return srcPORT;
    }

    public void setSrcPORT(String srcPORT) {
        this.srcPORT = srcPORT;
    }

    public String getSrcMAC() {
        return srcMAC;
    }

    public void setSrcMAC(String srcMAC) {
        this.srcMAC = srcMAC;
    }

    public String getDestIP() {
        return destIP;
    }

    public void setDestIP(String destIP) {
        this.destIP = destIP;
    }

    public String getDestPORT() {
        return destPORT;
    }

    public void setDestPORT(String destPORT) {
        this.destPORT = destPORT;
    }

    public String getDestMAC() {
        return destMAC;
    }

    public void setDestMAC(String destMAC) {
        this.destMAC = destMAC;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return time + "|" + dt + "|" + level + "|" + id + "|" +
                type + "|" + srcIP + "|" + srcPORT + "|" + srcMAC
                + "|" + destIP + "|" + destPORT + "|" + destMAC +
                "|" + protocol + "|" + subject + "|" + message;
    }
}
