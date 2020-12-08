package server;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println("Starting Client");
        
        SendRequest sendRequest = new SendRequest("127.0.0.1", 5000, "827211133");
        if(!sendRequest.sendAuth()) return;
        System.out.println("Auth sent!!");
        
        if(!sendRequest.sendNewChat("9876543210")) return;
        System.out.println("New Chat sent!!");

        if(!sendRequest.sendMessage("9876543210", "OS Lab ka Assignment karliya??")) return;
        System.out.println("Message sent!!");

    }
}
