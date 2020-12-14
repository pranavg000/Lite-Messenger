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
            if (GlobalVariables.onlineClients.containsKey(request.getReceiverId())) {
                outputStream.writeUTF(gson.toJson(request));
            } else {

                System.out.println("FFFFFFFFFFFFFFFFFFFFFF Receiver Offline, saving to DB");

                GlobalVariables.globalLocks.acquire();   
                GlobalVariables.messageCollection.insertOne(request.toDocument());
                GlobalVariables.globalLocks.release();

            }

        } catch (IOException e1) {
            // Insert into database if unable to send message.
            try {
                GlobalVariables.globalLocks.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GlobalVariables.messageCollection.insertOne(request.toDocument());
            GlobalVariables.onlineClientsRemoveKey(request.getReceiverId());

            GlobalVariables.globalLocks.release();

            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
