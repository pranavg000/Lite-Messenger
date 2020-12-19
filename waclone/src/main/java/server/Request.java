package server;

import server.GlobalVariables.RequestType;

import java.util.UUID;

import org.bson.Document;

public class Request {
    private String requestId;
    private RequestType action;
    private String senderId;
    private String receiverId;
    private String data;
    private String token;
    private long timeStamp;

    Request(Document obj){
        this.requestId = (String)obj.get("requestId");
        this.action = RequestType.valueOf((String)obj.get("action"));
        this.senderId = (String)obj.get("senderId");
        this.receiverId = (String)obj.get("receiverId");
        this.data = (String)obj.get("data");
        this.token = (String)obj.get("token");
        this.timeStamp = (long)obj.get("timeStamp");
    }

    Request(RequestType action, String senderId, String receiverId, String data, String token){
        this.requestId = UUID.randomUUID().toString();
        this.action = action;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.data = data;
        this.token = token;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getToken(){
        return token;
    }

    public void setToken(String t){
        token=t;
    }
    public RequestType getAction() {
        return action;
    }

    public void setAction(RequestType action) {
        this.action = action;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public Document toDocument(){
        Document obj = new Document().append("senderId", senderId).append("receiverId", receiverId)
            .append("data", data).append("action", action.name()).append("token", token).append("timeStamp", timeStamp);
        return obj;
    }

    @Override
    public String toString() {
        return "Request [requestId=" +requestId+ ", action=" + action.name() + ", data=" + data + ", receiverId=" + receiverId + ", senderId=" + senderId
                + ", token="+token+ ", timeStamp=" + String.valueOf(timeStamp) + "]";
    }

    
   
}
