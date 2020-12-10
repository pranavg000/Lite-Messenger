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
            outputStream.writeUTF(gson.toJson(request));

        } catch (IOException e1) {
            //Insert into database if unable to send message.
            GlobalVariables.messageCollection.insert(request.toDBObject());
            e1.printStackTrace();
        }

    }
}
