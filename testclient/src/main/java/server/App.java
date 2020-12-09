package server;
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
        
        SendRequest sendRequest = new SendRequest("127.0.0.1", 5000, myPhoneNo);

        while(true){
            int choice = Integer.parseInt(in.nextLine());
            if(choice == 1){
                sendRequest.sendAuth();
                System.out.println("Auth sent!!");
            }
            else if(choice == 2){
                sendRequest.sendNewChat(otherPhoneNo);
                System.out.println("New Chat sent!!");
            }
            else if(choice == 3){
                sendRequest.sendMessage(otherPhoneNo, "OS Lab ka Assignment karliya??");
                System.out.println("Message sent!!");
            }
            else if(choice == 4){
                sendRequest = new SendRequest("127.0.0.1", 5000, myPhoneNo);
            }
            else break;
        }

        in.close();
    }
}
