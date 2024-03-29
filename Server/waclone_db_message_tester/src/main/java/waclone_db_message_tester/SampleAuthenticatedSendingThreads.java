package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class SampleAuthenticatedSendingThreads extends Thread {
    
    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;
    DataInputStream inputStream;
    
    SampleAuthenticatedSendingThreads(String i){
        this.id=i;
    }

    public void run() {

        if(!GlobalVariables.tokens.containsKey(id)){
            System.out.println("TOKEN NOT AVAILABLE FOR THIS THREAD!!! TERMINATING!!!");
            return;
        }
        this.token = GlobalVariables.tokens.get(id);

        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            System.out.println("Authenticated thread with id "+id+" authenticated.");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if(GlobalVariables.authenticatedSendingThreadsReady){
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
            if(validation.getAction() == RequestType.POSITIVE){
                System.out.println("Authenticated Sending Thread with ID "+id+ " Signed In Successfully!");
            } else if(validation.getAction() == RequestType.ERROR){
                System.out.println(validation.getData()+" TERMINATING!!!");
                return;
            } else {
                System.out.println("Unknown message received");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        for(int i=0;i<10;i++){
            int receiver = ThreadLocalRandom.current().nextInt(10,20);
            request = new Request(RequestType.Message, id, Integer.toString(receiver), "Source: "+id+", Receiver: "+Integer.toString(receiver), token);
            
            try {
                outputStream.writeUTF(gson.toJson(request));
                System.out.println("Sent: "+request.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Request disconnectRequest = new Request(RequestType.Disconnect, id, GlobalVariables.serverId, "Trying to disconnect!", token);
        try {
            outputStream.writeUTF(gson.toJson(disconnectRequest));
            System.out.println("Sending thread with id " + id + " disconnecting.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Request validation = gson.fromJson(inputStream.readUTF(), Request.class);
            if(validation.getAction() == RequestType.POSITIVE){
                System.out.println("Sending thread with id "+id+" disconnected successfully");
            } else {
                System.out.println(validation.getAction().name() + " Error disconnecting for id "+id+": "+validation.getData());
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        
    }

}
