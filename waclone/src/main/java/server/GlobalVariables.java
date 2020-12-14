package server;

import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
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
    
    // Threads etc.
    public final static int Nthreads = 10;
    public static ExecutorService sendMessage, receiveMessage;
    public static Map<String, ClientInfo> onlineClientsNew;
    public static Map<Channel, String> channelToClientId;
    public static BlockingQueue<Request> outbox;
    public static Semaphore globalLocks;

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

    public static synchronized void databaseInsertData(Request request) {
        try {
            globalLocks.acquire();
            messageCollection.insertOne(request.toDocument());
            globalLocks.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    public static synchronized List<Document> fetchUnsendMessages(String clientId) {
        List<Document> messages = new ArrayList<Document>();
        try {
            globalLocks.acquire();
            messages = messageCollection.find(eq("receiverId", clientId)).into(new ArrayList<Document>());
            messageCollection.deleteMany(eq("receiverId", clientId));
            globalLocks.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static synchronized void addClientToOnlineList(SocketChannel channel, String clientId) {
        try {
            globalLocks.acquire();
            onlineClientsNew.put(clientId, new ClientInfo(clientId, channel));
            channelToClientId.put(channel, clientId);
            globalLocks.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    public static synchronized boolean removeClientFromOnlineList(SocketChannel channel) {
        try {
            globalLocks.acquire();
            if (channelToClientId.containsKey(channel)) {
                String clientId = channelToClientId.get(channel);
                channelToClientId.remove(channel);
                onlineClientsNew.remove(clientId);
                globalLocks.release();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkClientOnline(String clientId) {
        try {
            globalLocks.acquire();
            boolean ans = onlineClientsNew.containsKey(clientId);
            globalLocks.release();
            return ans;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ClientInfo getClientInfo(String clientId) {
        try {
            globalLocks.acquire();
            ClientInfo ans = onlineClientsNew.get(clientId);
            globalLocks.release();
            return ans;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
