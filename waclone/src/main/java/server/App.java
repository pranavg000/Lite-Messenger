package server;
import java.util.concurrent.BlockingQueue;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");
        
        GlobalVariables.clientSendBox = new HashMap<String,BlockingQueue<Request>>();

        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();
    }
}
