package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class AuthenticationTestThread extends Thread {
    //To test "account does not exist" and "unauthorised access"

    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;
    DataInputStream inputStream;
    
    AuthenticationTestThread(String i){
        this.id=i;
    }

    public void run(){
        this.token = "123";

        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            System.out.println("Authentication test thread with id " + id + " authenticated.");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if (GlobalVariables.authenticationThreadsReady) {
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
            if(validation.getAction() == RequestType.POSITIVE){
                System.out.println("Authenticated Sending Thread with ID "+id+ " Signed In Successfully!");
            } else if(validation.getAction() == RequestType.ERROR){
                System.out.println(validation.getData()+" TERMINATING!!!");
                return;
            } else {
                System.out.println("Unknown message received");
                return;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
    
    }

}
