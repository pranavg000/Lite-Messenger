package server;

import java.net.Socket;

public class SendingThread extends Thread {

    private SendRequest sendRequest;
    private String otherPhoneNo;

    public SendingThread(String ipAddress, int port, String myPhoneNo, String otherPhoneNo) {
        sendRequest = new SendRequest(ipAddress, port, myPhoneNo);
        this.otherPhoneNo = otherPhoneNo;
    }

    public void run() {

        System.out.println("Auth sent!!");
        sendRequest.sendAuth();

        try {
            Thread.sleep(10000);
        } 
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        int a[] = {2,3,3,2,3,3,4};
        int i = 0;
        while(true){
            int choice = a[i];
            i++;
            if(choice == 1){
                System.out.println("Auth sent!!");
                if(!sendRequest.sendAuth()) break;
            }
            else if(choice == 2){
                if(!sendRequest.sendNewChat(otherPhoneNo)) break;
                System.out.println("New Chat sent!!");
            }
            else if(choice == 3){
                if(!sendRequest.sendMessage(otherPhoneNo, "OS Lab ka Assignment karliya??")) break;
                System.out.println("Message sent!!");
            }
            else break;
            try {
                Thread.sleep(1000);
            } 
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            Thread.sleep(100000);
        } 
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        sendRequest.finalize();
        System.out.println("Terminating Sending thread");
        
    }

    public Socket getSocket(){
        return sendRequest.getSocket();
    }
}