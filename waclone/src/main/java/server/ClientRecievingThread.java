package server;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.gson.Gson;

import java.io.IOException;

public class ClientRecievingThread extends Thread {

    private Socket socket;

    public ClientRecievingThread setSocket(Socket inputSocket) {
        socket = inputSocket;
        return this;
    }

    private Boolean isAuthenticated(Request request) {
        if (request.getAction().equals("Connect"))
            return true;
        return false;
    }

    public void run() {
        Gson gson = new Gson();
        try {
            // Get input from socket
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Request request = gson.fromJson(in.readUTF(), Request.class);
            if (isAuthenticated(request)) {
                BlockingQueue<Request> clientSendBox = new LinkedBlockingDeque<Request>();
                GlobalVariables.clientSendBox.put("1", clientSendBox);
                while (true) {
                    request = gson.fromJson(in.readUTF(), Request.class);
                    System.out.println(request);
                    String recieverId = request.getRecieverId();
                    BlockingQueue<Request> sendBox = GlobalVariables.clientSendBox.get(recieverId);
                    if (sendBox != null) {
                        try {
                            sendBox.put(request);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // GlobalVariables.clientSendBox.remove("1");
            }
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }
}
