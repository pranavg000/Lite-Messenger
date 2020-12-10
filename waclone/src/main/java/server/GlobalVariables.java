package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.mongodb.*;

public class GlobalVariables {

    //Database
    public static MongoClientURI uri;
    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection messageCollection;
    public static DBCollection userCollection;

    //Threads etc.
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
