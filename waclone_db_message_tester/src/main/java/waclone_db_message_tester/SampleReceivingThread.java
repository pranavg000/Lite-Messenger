package waclone_db_message_tester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import waclone_db_message_tester.GlobalVariables.RequestType;

public class SampleReceivingThread extends Thread {
    public String id;
    public Socket socket;
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
            GlobalVariables.printer.acquire();
            System.out.println("Receiver thread with id "+id+" authenticated.");
            GlobalVariables.printer.release();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        Request request = new Request(RequestType.Auth, id, "-1", "CONNECTION PACKET");

        try{
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(request));

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            inputStream = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                request = gson.fromJson(inputStream.readUTF(), Request.class);
                GlobalVariables.printer.acquire();
                System.out.println("Received: "+request.getData());
                GlobalVariables.printer.release();
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


}
