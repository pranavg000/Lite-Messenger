package waclone;

import java.util.concurrent.ThreadLocalRandom;
import java.io.DataOutputStream;
import java.io.IOException;
import com.google.gson.Gson;

import waclone.GlobalVariables.RequestType;

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

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // try {
        // GlobalVariables.printer.acquire();
        // System.out.println("Threads ready");
        // GlobalVariables.printer.release();
        // } catch (InterruptedException e1) {
        // e1.printStackTrace();
        // }

        Gson gson = new Gson();
        Request request = new Request();
        request.setSenderId(id);
        request.setReceiverId("-1");
        request.setData("CONNECTION PACKET");
        request.setAction(RequestType.Auth);

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
        isAuthenticated = true;
        try {
            GlobalVariables.printer.acquire();
            GlobalVariables.threadsReady++;
            GlobalVariables.printer.release();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        while (true) {
            try {
                GlobalVariables.printer.acquire();
                if (GlobalVariables.threadsReady == GlobalVariables.Nclients) {
                    GlobalVariables.printer.release();
                    break;
                }
                GlobalVariables.printer.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // try {
            // GlobalVariables.printer.acquire();
            // System.out.println("Threads not ready");
            // GlobalVariables.printer.release();
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            continue;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            int receiver = ThreadLocalRandom.current().nextInt(0, GlobalVariables.Nclients);
            request.setSenderId(id);
            request.setReceiverId(Integer.toString(receiver));
            request.setData("Source: " + id + ", Receiver: " + Integer.toString(receiver));
            request.setAction(RequestType.Message);

            try {
                // DataOutputStream outputStream = new
                // DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(gson.toJson(request));
                GlobalVariables.printer.acquire();
                System.out.println("Sent: " + request.getData());
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