package server;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Client");

        Scanner in = new Scanner(System.in);

        String myPhoneNo = in.nextLine();
        String otherPhoneNo = in.nextLine();

        SendingThread sendingThread = new SendingThread("127.0.0.1", 5000, myPhoneNo, otherPhoneNo);
        ReceivingThread receivingThread = new ReceivingThread(sendingThread.getSocket());
        sendingThread.start();
        receivingThread.start();
        try {
            sendingThread.join();
            receivingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        in.close();
    }
}
