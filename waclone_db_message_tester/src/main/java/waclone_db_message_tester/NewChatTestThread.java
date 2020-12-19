package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class NewChatTestThread extends Thread {

    public String id;
    public Socket socket;
    public String token;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;
    DataInputStream inputStream;

    NewChatTestThread(String id) throws InterruptedException {
        System.out.println("NEWCHAT thread with id " + id + " created.");
        this.id = id;
    }

    public void run() {
        if (!GlobalVariables.tokens.containsKey(id)) {
            System.out.println("TOKEN NOT AVAILABLE!!! TERMINATING!!!");
            return;
        }
        token = GlobalVariables.tokens.get(id);

        try {
            socket = new Socket("127.0.0.1", 5000);
            System.out.println("New chat thread connected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Document authDoc = new Document().append("senderId", id).append("receiverId", "-1").append("action", "Auth")
        //         .append("token", token).append("data", "Authenticating request");
        Request authReq = new Request(RequestType.Auth, id, "-1", "Authenticating request", token);
        Gson gson = new Gson();

        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(authReq));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            Request validation = gson.fromJson(inputStream.readUTF(), Request.class);
            if (validation.getAction() == RequestType.POSITIVE) {
                System.out.println("NEWCHAT THREAD Signed In Successfully!");
            } else if (validation.getAction() == RequestType.ERROR) {
                System.out.println("NEWCHAT THREAD not signed In FAILURE!!! "+validation.getData());
                return;
            } else {
                System.out.println("UNKNOWN");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 10; i < 20; i++) {
            // Document doc = new Document().append("senderId", id).append("receiverId", Integer.toString(i))
            //         .append("token", token).append("data", "Creating new chat with " + Integer.toString(i))
            //         .append("action", "NewChat");
            Request req = new Request(RequestType.NewChat, id, Integer.toString(i), "Creating new chat with " + Integer.toString(i), token);
            try {
                outputStream.writeUTF(gson.toJson(req));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                Request res = gson.fromJson(inputStream.readUTF(), Request.class);
                System.out.println(res.getAction()+" "+res.getData());
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                return;
            }

        }
        
        for (int i = 20; i < 30; i++) {
            // Document doc = new Document().append("senderId", id).append("receiverId", Integer.toString(i))
            //         .append("token", token).append("data", "Creating new chat with " + Integer.toString(i))
            //         .append("action", "NewChat");
            Request req = new Request(RequestType.NewChat, id, Integer.toString(i), "Creating new chat with " + Integer.toString(i), token);
            try {
                outputStream.writeUTF(gson.toJson(req));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                Request res = gson.fromJson(inputStream.readUTF(), Request.class);
                System.out.println(res.getAction()+" "+res.getData());
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                return;
            }

        }


    }

}
