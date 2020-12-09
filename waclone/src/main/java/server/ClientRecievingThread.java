package server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.gson.Gson;

import server.GlobalVariables.RequestType;

import java.io.IOException;

public class ClientRecievingThread extends Thread {

    private Socket socket;
    private String clientId;

    public ClientRecievingThread(Socket inputSocket) {
        socket = inputSocket;
    }

    private Boolean isAuth(Request request) {
        if (request.getAction() == RequestType.Auth)
            return true;
        return false;
    }

    public void run() {
        Gson gson = new Gson();
        try {
            // Get input from socket
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Request request = gson.fromJson(in.readUTF(), Request.class);
            if (isAuth(request)) {
                clientId = request.getSenderId();
                BlockingQueue<Request> clientSendBox = new LinkedBlockingDeque<Request>();
                System.out.println("Created SendBox of " + request.getSenderId());
                GlobalVariables.clientSendBox.put(request.getSenderId(), clientSendBox);
                ClientSendingThread cst = new ClientSendingThread(socket, clientId);
                cst.start();
            }   
            String input;
            while (true) {
                // String input = "";
                // while (in.available() == 0)
                //     ;

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
                // System.out.println(input);

                request = gson.fromJson(input, Request.class);
                System.out.println(request);
                RequestType reqType = request.getAction();
                String recieverId = request.getRecieverId();
                if(reqType == RequestType.NewChat){
                    System.out.println("New Chat");
                    // Check if the receiver is present in Map
                    if(!GlobalVariables.clientSendBox.containsKey(recieverId)) {
                        // Return error response
                    }
                }
                else if(reqType == RequestType.Message){
                    System.out.println("Send message");
                    BlockingQueue<Request> sendBox = GlobalVariables.clientSendBox.get(recieverId);
                    if (sendBox != null) {
                        try {
                            sendBox.put(request);
                            // GlobalVariables.clientSendBox.put(recieverId, sendBox);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println("FFFFFFFFFFFFFFFFFFFFFF Mailbox not Found");

                    }
                }   
                else{
                    // Unkown command return error response
                    break;
                }
                
            }
                // GlobalVariables.clientSendBox.remove("1");
            
            in.close();
        } 
        catch (EOFException e){
            System.out.println("Closing socket from server");
        }
        catch (IOException e) {
            e.printStackTrace();
            // return;
        }
        
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }
}
