package waclone_db_message_tester;

import waclone_db_message_tester.GlobalVariables.RequestType;

import org.bson.Document;

public class Request {
   private RequestType action;
   private String senderId;
   private String receiverId;
   private String data;

   Request(Document obj){
        if(((String)obj.get("action")).equals("Auth")){
            this.action = RequestType.Auth;
        } else if(((String)obj.get("action")).equals("NewChat")){
            this.action = RequestType.NewChat;
        } else if(((String)obj.get("action")).equals("Message")){
            this.action = RequestType.Message;
        }
        this.senderId = (String)obj.get("senderId");
        this.receiverId = (String)obj.get("receiverId");
        this.data = (String)obj.get("data");
   }

    public Request(RequestType a, String sendId, String recId, String d) {
        action=a;
        senderId=sendId;
        receiverId=recId;
        data=d;
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

        Document obj = new Document().append("senderId", senderId).append("receiverId", receiverId).append("data", data).append("action",actionString);
        return obj;

   }

   @Override
   public String toString() {
       return "Request [action=" + GlobalVariables.getActionString(action) + ", data=" + data + ", receiverId=" + receiverId + ", senderId=" + senderId
               + "]";
   }
   
}
