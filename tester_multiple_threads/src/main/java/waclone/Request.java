package waclone;

import waclone.GlobalVariables.RequestType;

public class Request {
   private RequestType action;
   private String senderId;
   private String receiverId;
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

   @Override
   public String toString() {
       return "Request [action=" + action + ", data=" + data + ", receiverId=" + receiverId + ", senderId=" + senderId
               + "]";
   }
   
}
