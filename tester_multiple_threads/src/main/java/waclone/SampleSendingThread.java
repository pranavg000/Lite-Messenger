package waclone;

import java.util.concurrent.ThreadLocalRandom;
import java.io.DataOutputStream;
import java.io.IOException;
import com.google.gson.Gson;
import java.net.Socket;

class SampleSendingThread extends Thread {
    public String id;
    public Socket socket;
    public boolean isAuthenticated = false;
    SampleReceivingThread assistant;
    DataOutputStream outputStream;

    SampleSendingThread(String x) {
        this.id = x;
    }

    public void run() {

        try {
            // When request at listening socket, create socket with client.
            socket = new Socket("127.0.0.1", 5000);
            isAuthenticated = true;

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if(GlobalVariables.threadsReady){
                    GlobalVariables.printer.release();
                    break;
                }
                GlobalVariables.printer.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // try {
            //     GlobalVariables.printer.acquire();
            //     System.out.println("Threads not ready");
            //     GlobalVariables.printer.release();
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

            continue;
        }
        // try {
        //     GlobalVariables.printer.acquire();
        //     System.out.println("Threads ready");
        //     GlobalVariables.printer.release();
        // } catch (InterruptedException e1) {
        //     e1.printStackTrace();
        // }


        Gson gson = new Gson();
        Request request = new Request();
        request.setSenderId(id);
        request.setRecieverId("-1");
        request.setData("CONNECTION PACKET");
        request.setAction("Connect");

        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(request));
            // outputStream.close();
            assistant = new SampleReceivingThread(socket);
            assistant.start();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        for(int i=0;i<10;i++){
            int receiver = ThreadLocalRandom.current().nextInt(0,100);
            request.setSenderId(id);
            request.setRecieverId(Integer.toString(receiver));
            request.setData("Source: "+id+", Receiver: "+Integer.toString(receiver));
            request.setAction("Message");
            
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