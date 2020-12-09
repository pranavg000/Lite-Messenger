package server;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.gson.Gson;

import java.io.IOException;

public class ClientRecievingThread extends Thread {

    private Socket socket;
    private String clientId;

    public ClientRecievingThread(Socket inputSocket) {
        socket = inputSocket;
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
                clientId = request.getSenderId();
                BlockingQueue<Request> clientSendBox = new LinkedBlockingDeque<Request>();
                System.out.println("Created SendBox of " + request.getSenderId());
                GlobalVariables.clientSendBox.put(request.getSenderId(), clientSendBox);
                ClientSendingThread cst = new ClientSendingThread(socket, clientId);
                cst.start();
                while (true) {
                    String input = "";
                    while (in.available() == 0)
                        ;
                    // {
                    // //
                    // System.out.println(String.valueOf(socket.isConnected())+String.valueOf(socket.isClosed())+String.valueOf(socket.isInputShutdown())+String.valueOf(socket.isOutputShutdown()));
                    // // if(socket.isInputShutdown()){
                    // // System.out.println("Closing");
                    // // GlobalVariables.clientSendBox.remove(clientId);
                    // // socket.close();
                    // // in.close();
                    // // return;
                    // }
                    // }
                    input = in.readUTF();
                    request = gson.fromJson(input, Request.class);
                    System.out.println(request);
                    String recieverId = request.getRecieverId();
                    BlockingQueue<Request> sendBox = GlobalVariables.clientSendBox.get(recieverId);
                    if (sendBox != null) {
                        try {
                            sendBox.put(request);
                            GlobalVariables.clientSendBox.put(recieverId, sendBox);
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
