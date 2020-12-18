package server;

import server.GlobalVariables.RequestType;

import org.bson.Document;

public class Request {
    private RequestType action;
    private String senderId;
    private String receiverId;
    private String data;
    private String token;

    Request(Document obj){
        if(((String)obj.get("action")).equals("Auth")){
            this.action = RequestType.Auth;
        } else if(((String)obj.get("action")).equals("NewChat")){
            this.action = RequestType.NewChat;
        } else if(((String)obj.get("action")).equals("Message")){
            this.action = RequestType.Message;
        } else if(((String)obj.get("action")).equals("SignUp")){
            this.action = RequestType.SignUp;
        }else if(((String)obj.get("action")).equals("POSITIVE")){
            this.action = RequestType.POSITIVE;
        } else if(((String)obj.get("action")).equals("ERROR")){
            this.action = RequestType.ERROR;
        } else if(((String)obj.get("action")).equals("Disconnect")){
            this.action = RequestType.Disconnect;
        }
        this.senderId = (String)obj.get("senderId");
        this.receiverId = (String)obj.get("receiverId");
        this.data = (String)obj.get("data");
        this.token = (String)obj.get("token");
    }

    Request(RequestType action, String senderId, String receiverId, String data, String token){
        this.action = action;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.data = data;
        this.token = token;
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

    public Document toDocument(){

        String actionString=GlobalVariables.getActionString(action);

        Document obj = new Document().append("senderId", senderId).append("receiverId", receiverId)
            .append("data", data).append("action",actionString).append("token", token);
        return obj;

    }

    @Override
    public String toString() {
        return "Request [action=" + GlobalVariables.getActionString(action) + ", data=" + data + ", receiverId=" + receiverId + ", senderId=" + senderId
                + ", token="+token+"]";
    }
   
}
