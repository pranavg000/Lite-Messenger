package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSendingThread extends Thread {

    private Socket socket;
    private String clientId;

    public ClientSendingThread setClient(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
        return this;
    }

    public void run() {
        while (true) {
            Request request = GlobalVariables.clientSendBox.get(clientId).poll();
            if (request != null) {
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(request.getData());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
