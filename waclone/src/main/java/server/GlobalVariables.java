package server;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

public class GlobalVariables {

    // Database
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> messageCollection;
    public static MongoCollection<Document> userCollection;
    public static String connectionString = "mongodb+srv://wacloneAPH:waclonemen3001@waclonecluster.etq8i.mongodb.net/test?retryWrites=true&w=majority";
    public static Semaphore databaseLock;

    // Threads etc.
    public final static int Nthreads = 10;
    public static ExecutorService sendMessage, receiveMessage;
    public static Map<String, ClientInfo> onlineClients;
    public static Map<String, ClientInfoNew> onlineClientsNew;
    public static Map<Channel, String> channelToClientId;
    public static BlockingQueue<Request> outbox;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp
    }

    public static String getActionString(RequestType r) {
        String s = "";
        if (r == RequestType.Auth) {
            s = "Auth";
        } else if (r == RequestType.NewChat) {
            s = "NewChat";
        } else if (r == RequestType.Message) {
            s = "Message";
        } else if (r == RequestType.SignUp) {
            s = "SignUp";
        }

        return s;
    }

    public static synchronized void onlineClientsAddKey(String key, ClientInfo clientInfo) {
        onlineClients.put(key, clientInfo);
    }

    public static synchronized void onlineClientsRemoveKey(String Key) {
        onlineClients.remove(Key);
    }

    public static synchronized void databaseInsertData(Request request) {
        GlobalVariables.messageCollection.insertOne(request.toDocument());
    }

    public static synchronized List<Document> fetchUnsendMessages(String clientId) {
        List<Document> messages = GlobalVariables.messageCollection.find(eq("receiverId", clientId)).into(new ArrayList<Document>());
        GlobalVariables.messageCollection.deleteMany(eq("receiverId", clientId));
        return messages;
    }
}
