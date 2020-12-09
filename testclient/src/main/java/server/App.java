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
        
        SendingThread sendingThread = new SendingThread("127.0.0.1", 5000, myPhoneNo, otherPhoneNo);
        ReceivingThread receivingThread = new ReceivingThread(sendingThread.getSocket());
        sendingThread.start();
        receivingThread.start();
        in.close();
    }
}
