package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import org.bson.Document;

import waclone_db_message_tester.GlobalVariables.RequestType;

class SampleSendingThread extends Thread {

    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;
    DataInputStream inputStream;

    SampleSendingThread(String id) throws InterruptedException {
        System.out.println("Sending thread with id "+id+" created.");
        this.id = id;
    }

    public void run() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            System.out.println("Thread with id "+id+" authenticated.");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if(GlobalVariables.senderThreadsReady){
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
            if(GlobalVariables.getActionString(validation.getAction()).equals("POSITIVE")){
                token = validation.getToken();
                System.out.println("Signed up successfully! Token received: "+token);
            } else if(GlobalVariables.getActionString(validation.getAction()).equals("ERROR")){
                System.out.println("Account already exists! TERMINATING");
                return;
            } else {
                System.out.println("Unknown message received");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for(int i=0;i<10;i++){
            int receiver = ThreadLocalRandom.current().nextInt(10,20);
            request.setSenderId(id);
            request.setReceiverId(Integer.toString(receiver));
            request.setData("Source: "+id+", Receiver: "+Integer.toString(receiver));
            request.setAction(RequestType.Message);
            request.setToken(token);
            
            try {
                // DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(gson.toJson(request));
                System.out.println("Sent: "+request.getData());
                // outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

}