package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GlobalVariables {

    //Database
    public static MongoDatabase database;
    public static MongoCollection<Document> messageCollection;
    public static MongoCollection<Document> userCollection;
    public static String connectionString = "mongodb+srv://wacloneAPH:waclonemen3001@waclonecluster.etq8i.mongodb.net/test?retryWrites=true&w=majority";

    //Threads etc.
    public final static int Nthreads = 10;
    public static ExecutorService sendMessage; 
    public static Map<String, ClientInfo> onlineClients;
    public static BlockingQueue<Request> outbox;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp
    }

    public static String getActionString(RequestType r){
        String s="";
        if(r == RequestType.Auth){
            s="Auth";
        } else if(r == RequestType.NewChat){
            s="NewChat";
        } else if(r ==RequestType.Message){
            s="Message";
        } else if(r == RequestType.SignUp){
            s="SignUp";
        }

        return s;
    }

    public static synchronized void onlineClientsAddKey(String key, ClientInfo clientInfo) {
        onlineClients.put(key, clientInfo);
    }

    public static synchronized void onlineClientsRemoveKey(String Key) {
        onlineClients.remove(Key);
    }

}
