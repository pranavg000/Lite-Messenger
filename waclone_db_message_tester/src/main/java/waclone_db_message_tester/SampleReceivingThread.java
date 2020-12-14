package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bson.Document;

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
            System.out.println("Receiver thread with id "+id+" authenticated.");
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
        Document connectionDoc = new Document().append("senderId",id).append("receiverId","-1").append("action","SignUp").append("data","NULL").append("token","NULL");
        Request request = new Request(connectionDoc);

        try{
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
            if(GlobalVariables.getActionString(validation.getAction()).equals("POSITIVE")){
                token = validation.getToken();
                System.out.println("Signed up successfully! Token received: "+token);
            } else if(GlobalVariables.getActionString(validation.getAction()).equals("ERROR")){
                System.out.println("Account already exists! TERMINATING");
                return;
            } else {
                System.out.println("Unknown message received");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                request = gson.fromJson(inputStream.readUTF(), Request.class);
                System.out.println("Received: "+request.getData());
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
        }

    }


}
