package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bson.Document;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class SampleReceivingThread extends Thread {
    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;

    DataInputStream inputStream;
    DataOutputStream outputStream;

    SampleReceivingThread(String i) {
        id = i;
    }

    public void run() {

        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            System.out.println("Receiver thread with id " + id + " authenticated.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if (GlobalVariables.receiverThreadsReady) {
                    GlobalVariables.printer.release();
                    break;
                }
                GlobalVariables.printer.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        Document connectionDoc = new Document().append("senderId", id).append("receiverId", "-1")
                .append("action", "SignUp").append("data", "NULL").append("token", "NULL");
        Request request = new Request(connectionDoc);

        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(request));

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            Request validation = gson.fromJson(inputStream.readUTF(), Request.class);
            // System.out.println(id+" "+validation.getAction());
            if (GlobalVariables.getActionString(validation.getAction()).equals("POSITIVE")) {
                token = validation.getToken();
                GlobalVariables.printer.acquire();
                GlobalVariables.tokens.put(id, token);
                GlobalVariables.printer.release();
                System.out.println("Signed up successfully! Token received: " + token);
            } else if (GlobalVariables.getActionString(validation.getAction()).equals("ERROR")) {
                System.out.println("Account already exists! TERMINATING");
                return;
            } else {
                System.out.println("Unknown message received");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        Document disconnectDocument = new Document().append("senderId", id).append("receiverId", "-1")
                .append("action", "Disconnect").append("token", token).append("data", "Trying to disconnect!");
        Request disconnectRequest = new Request(disconnectDocument);
        try {
            outputStream.writeUTF(gson.toJson(disconnectRequest));
            System.out.println("Receiving thread with id " + id + " disconnecting.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Request validation = gson.fromJson(inputStream.readUTF(), Request.class);
            if(validation.getAction() == RequestType.POSITIVE){
                System.out.println("Receiving thread with id "+id+" disconnected successfully");
            } else {
                System.out.println("Error disconnecting for id "+id+": "+validation.getData());
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }


    }


}
