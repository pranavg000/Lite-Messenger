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
        Gson gson = new Gson();
        String output = gson.toJson(request);
        System.out.println(output);
        synchronized (outputStream) {
            try {
                outputStream.writeUTF(output);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
