package server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

import server.GlobalVariables.RequestType;

public class ReceivingThread extends Thread {

    private Socket socket;
    

    public ReceivingThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        DataInputStream inputStream;
        try {
            String input;
            Request request;
            Gson gson = new Gson();
            inputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                try {
                    input = inputStream.readUTF();
                    request = gson.fromJson(input, Request.class);
                    System.out.println(request);
                    // RequestType reqType = request.getAction();

                    // if(reqType == RequestType.Message){

                    // }
                    // else{
                    //     System.out.println("NOT a message");
                    // }
                }
                catch (EOFException e) {
                    System.out.println("Closing socket");
                    break;
                }
                catch (SocketException e){
                    break;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {
                inputStream.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Terminating Receive thread");
    }
}
