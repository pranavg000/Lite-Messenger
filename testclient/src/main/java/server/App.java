package server;
import java.net.Socket;
import java.util.Scanner;
/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println("Starting Client");
        
        Scanner in = new Scanner(System.in);
        String myPhoneNo = in.nextLine();
        String otherPhoneNo = in.nextLine();
        in.close();
        SendRequest sendRequest = new SendRequest("127.0.0.1", 5000, myPhoneNo);
        if(!sendRequest.sendAuth()) return;
        System.out.println("Auth sent!!");
        
        try {
            Thread.sleep(10000);
        } 
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        System.out.println("hi");
        if(!sendRequest.sendNewChat(otherPhoneNo)) return;
        System.out.println("New Chat sent!!");

        try {
            Thread.sleep(1000);
        } 
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        if(!sendRequest.sendMessage(otherPhoneNo, "OS Lab ka Assignment karliya??")) return;
        System.out.println("Message sent!!");
        // try {
        //     Thread.sleep(10000);
        // } 
        // catch(InterruptedException ex) {
        //     Thread.currentThread().interrupt();
        // }
        sendRequest.finalize();

    }
}
