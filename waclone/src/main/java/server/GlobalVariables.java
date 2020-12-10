package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GlobalVariables {

    //Database
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> messageCollection;
    public static MongoCollection<Document> userCollection;

    //Threads etc.
    public final static int Nthreads = 10;
    public static ExecutorService sendMessage; 
    public static Map<String, ClientInfo> onlineClients;
    public static BlockingQueue<Request> outbox;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp
    }

    public static synchronized void onlineClientsAddKey(String key, ClientInfo clientInfo) {
        onlineClients.put(key, clientInfo);
    }

    public static synchronized void onlineClientsRemoveKey(String Key) {
        onlineClients.remove(Key);
    }

}
