package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class SampleAuthenticatedReceivingThreads extends Thread {
    
    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;
    DataInputStream inputStream;
    
    SampleAuthenticatedReceivingThreads(String i){
        this.id=i;
    }

    public void run(){
        if(!GlobalVariables.tokens.containsKey(id)){
            System.out.println("TOKEN NOT AVAILABLE FOR THIS THREAD!!! TERMINATING!!!");
            return;
        }
        this.token = GlobalVariables.tokens.get(id);

        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            System.out.println("Authenticated receiver thread with id " + id + " authenticated.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if (GlobalVariables.authenticatedReceivingThreadsReady) {
                    GlobalVariables.printer.release();
                    break;
                }
                GlobalVariables.printer.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        Request request = new Request(RequestType.Auth, id, GlobalVariables.serverId, "NULL", token);

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
            if (GlobalVariables.getActionString(validation.getAction()).equals("POSITIVE")) {                
                System.out.println("Authenticated Receiving Thread with ID "+id+ " Signed In Successfully!");
            } else if (GlobalVariables.getActionString(validation.getAction()).equals("ERROR")) {
                System.out.println(validation.getData()+" TERMINATING!!!");
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
