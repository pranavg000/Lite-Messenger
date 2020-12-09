package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSendingThread extends Thread {

    private Socket socket;
    private String clientId;

    public ClientSendingThread(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    public void run() {
        DataOutputStream outputStream;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            while (true) {
                if(!GlobalVariables.clientSendBox.containsKey(clientId)){
                    outputStream.close();
                    System.out.println("Closing");
                    return;
                }
                Request request = GlobalVariables.clientSendBox.get(clientId).poll();
                if (request != null) {
                    try {
                        outputStream.writeUTF(request.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
    }
}
