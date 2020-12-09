package server;

import server.GlobalVariables.RequestType;

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

   @Override
   public String toString() {
       return "Request [action=" + action + ", data=" + data + ", recieverId=" + recieverId + ", senderId=" + senderId
               + "]";
   }
   
}
