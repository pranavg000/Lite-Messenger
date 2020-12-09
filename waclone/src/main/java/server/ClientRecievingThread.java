package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;

import com.google.gson.Gson;

import server.GlobalVariables.RequestType;

import java.io.IOException;

public class ClientRecievingThread extends Thread {

    private Socket socket;
    private DataInputStream inputStream;
    private String clientId;

    public ClientRecievingThread(Socket inputSocket) {
        socket = inputSocket;
        clientId = "";
    }

    private Boolean isAuth(Request request) {
        if (request.getAction() == RequestType.Auth)
            return true;
        return false;
    }

    private Boolean processRequest(Request request) {
        RequestType reqType = request.getAction();
        String recieverId = request.getRecieverId();
        if (clientId.isEmpty())
            clientId = request.getSenderId();
        if (!GlobalVariables.onlineClients.containsKey(clientId)) {
            if (isAuth(request)) {
                System.out.println("Auth");
                DataOutputStream outputStream;
                try {
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    GlobalVariables.onlineClientsAddKey(clientId, new ClientInfo(clientId, inputStream, outputStream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (reqType == RequestType.NewChat) {
            System.out.println("New Chat");
            // Check if the receiver is present in Map
            // if (!GlobalVariables.clientSendBox.containsKey(recieverId)) {
            // // Return error response
            // }
        } else if (reqType == RequestType.Message) {
            System.out.println("Send message");
            if (GlobalVariables.onlineClients.containsKey(recieverId)) {
                ClientInfo recieverInfo = GlobalVariables.onlineClients.get(recieverId);
                GlobalVariables.sendMessage.execute(new SendMessageTask(recieverInfo.getOutputStream(), request));
            } else {
                System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Offline");
            }
        } else {
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Unknown Command");
            // Unkown command return error response
            return false;
        }
        return true;
    }

    public void run() {
        Gson gson = new Gson();
        try {
            // Get input from socket
            inputStream = new DataInputStream(socket.getInputStream());
            Request request;
            String input;
            while (true) {
                input = inputStream.readUTF();
                // System.out.println(input);
                request = gson.fromJson(input, Request.class);
                System.out.println(request);
                if (!processRequest(request))
                    break;
            }

            inputStream.close();
        } catch (EOFException e) {
            System.out.println("Closing socket from server");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            GlobalVariables.onlineClientsRemoveKey(clientId);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;

    }
}
