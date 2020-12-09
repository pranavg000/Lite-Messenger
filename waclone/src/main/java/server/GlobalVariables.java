package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class GlobalVariables {
    public final static int Nthreads = 10;
    public static ExecutorService sendMessage; 
    public static Map<String, ClientInfo> onlineClients;
    public static BlockingQueue<Request> outbox;

    public static enum RequestType {
        Auth, NewChat, Message
    }

    public static synchronized void onlineClientsAddKey(String key, ClientInfo clientInfo) {
        onlineClients.put(key, clientInfo);
    }

    public static synchronized void onlineClientsRemoveKey(String Key) {
        onlineClients.remove(Key);
    }

}
