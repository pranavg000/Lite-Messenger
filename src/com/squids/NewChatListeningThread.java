package com.squids;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewChatListeningThread extends Thread{

    private ServerSocket listeningSocket;
    private Socket socket;

    public void run(){
        System.out.println("New Chat Listening Thread activated");
        try {
            //Create Listening Socket listening at port 5002
            listeningSocket = new ServerSocket(5002);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("New Chat Listening Socket ready and listening at port 5002");

        while(true){
            try {
                //When request at listening socket, create socket with client.
                socket = listeningSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                //Get input from socket
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String message = in.readUTF();
                //Check if message input (just in case)
                if(message.charAt(0) == 'N'){
                    System.out.printf("Received a new chat request: %s\n", message);
                    //Add to requests queue
                    GlobalVariables.newChatRequests.add(message);
                    //Close this socket
                    socket.close();
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

}
