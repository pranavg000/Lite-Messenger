package com.squids;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthListeningThread extends Thread{

    private ServerSocket listeningSocket;
    private Socket socket;

    public void run(){
        System.out.println("Auth Listening Thread activated");
        try {
            //Create Listening Socket listening at port 5001
            listeningSocket = new ServerSocket(5001);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Auth Listening Socket ready and listening at port 5001");

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
                if(message.charAt(0) == 'A'){
                    System.out.printf("Received an auth request: %s\n", message);
                    //Add to requests queue
                    GlobalVariables.authRequests.add(message);

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
