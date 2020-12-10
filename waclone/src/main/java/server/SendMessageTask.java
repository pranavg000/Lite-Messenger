package server;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

public class SendMessageTask implements Runnable {

    private DataOutputStream outputStream;
    private Request request;

    public SendMessageTask(DataOutputStream outputStream, Request request) {
        this.outputStream = outputStream;
        this.request = request;
    }

    public void run() {
        try {
            Gson gson = new Gson();
            if(GlobalVariables.onlineClients.containsKey(request.getReceiverId())){
                outputStream.writeUTF(gson.toJson(request));
            } else {
                GlobalVariables.messageCollection.insert(request.toDBObject());
            }

        } catch (IOException e1) {
            //Insert into database if unable to send message.
            GlobalVariables.messageCollection.insert(request.toDBObject());
            e1.printStackTrace();
        }

    }
}
