package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListeningThread extends Thread {

    private ServerSocket listeningSocket;
    private Socket socket;

    public void run() {
        System.out.println("Connection Listening Thread activated");
        try {
            // Create Listening Socket listening at port 5000
            listeningSocket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Connection Listening Socket ready and listening at port 5000");

        while (true) {
            try {
                // When request at listening socket, create socket with client.
                socket = listeningSocket.accept();
                ClientRecievingThread crt = new ClientRecievingThread(socket);
                crt.start();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
};
