package waclone_db_message_tester;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import waclone_db_message_tester.GlobalVariables.RequestType;

class SampleSendingThread extends Thread {

    public String id;
    public Socket socket;
    public boolean isAuthenticated = false;
    DataOutputStream outputStream;

    SampleSendingThread(String id) throws InterruptedException {
        GlobalVariables.printer.acquire();
        System.out.println("Sending thread with id "+id+" created.");
        GlobalVariables.printer.release();
        this.id = id;
    }

    public void run() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;
            GlobalVariables.printer.acquire();
            System.out.println("Thread with id "+id+" authenticated.");
            GlobalVariables.printer.release();
            System.out.println("ID "+id);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        Request request = new Request(RequestType.Auth, id, "-1", "CONNECTION PACKET");

        try{
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(request));

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for(int i=0;i<10;i++){
            int receiver = ThreadLocalRandom.current().nextInt(10,20);
            request.setSenderId(id);
            request.setReceiverId(Integer.toString(receiver));
            request.setData("Source: "+id+", Receiver: "+Integer.toString(receiver));
            request.setAction(RequestType.Message);
            
            try {
                // DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(gson.toJson(request));
                GlobalVariables.printer.acquire();
                System.out.println("Sent: "+request.getData());
                GlobalVariables.printer.release();
                // outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }

}