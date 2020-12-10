package server;

import server.GlobalVariables.RequestType;
import com.mongodb.*;

public class Request {
   private RequestType action;
   private String senderId;
   private String recieverId;
   private String data;

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

   public String getRecieverId() {
       return recieverId;
   }

   public void setRecieverId(String recieverId) {
       this.recieverId = recieverId;
   }

   public String getData() {
       return data;
   }

   public void setData(String data) {
       this.data = data;
   }

   public DBObject toDBObject(){

        DBObject obj = new BasicDBObject("_id", recieverId).append("senderId", senderId).append("data", data).append("action",action);
        return obj;

   }

   @Override
   public String toString() {
       return "Request [action=" + action + ", data=" + data + ", recieverId=" + recieverId + ", senderId=" + senderId
               + "]";
   }
   
}
