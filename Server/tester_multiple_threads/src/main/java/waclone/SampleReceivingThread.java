package waclone;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class SampleReceivingThread extends Thread {
    Socket socket;

    SampleReceivingThread(Socket s) {
        socket = s;
    }

    public void run() {
        try {
            GlobalVariables.printer.acquire();
            System.out.println("Assistant initiated");
            GlobalVariables.printer.release();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Gson gson = new Gson();
        DataInputStream in;
        try {
            in = new DataInputStream(socket.getInputStream());
            GlobalVariables.printer.acquire();
            System.out.println("Input stream created");
            GlobalVariables.printer.release();
        } catch (IOException e) {
            // System.out.println("LOBLOLLY LOBLOLLY 3");
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                String input = in.readUTF();
                System.out.println(input);
                Request request = gson.fromJson(input, Request.class);
                GlobalVariables.printer.acquire();
                System.out.println("Received: "+request.getData());
                GlobalVariables.printer.release();
            } catch (JsonSyntaxException e) {
                // System.out.println("LOBLOLLY LOBLOLLY");
                e.printStackTrace();
            } catch (IOException e) {
                // System.out.println("LOBLOLLY LOBLOLLY 2");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}