package server;

import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
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
    public final static String serverId = "SERVER";
    public static ExecutorService sendMessage, receiveMessage;
    public static Map<String, ClientInfo> onlineClientsNew;
    public static Map<Channel, String> channelToClientId;
    public static BlockingQueue<Request> outbox;
    // public static Semaphore globalLocks;
    public static ReadWriteLock rwlock;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp, Disconnect, POSITIVE, ERROR, InvalidToken, UserNotFound, MessageReceived, SignUpSuccessful, AuthSuccessful, NewChatPositive
    }


    // public static String getActionString(RequestType r){
    //     String s="";
    //     if(r == RequestType.Auth){
    //         s="Auth";
    //     } else if(r == RequestType.NewChat){
    //         s="NewChat";
    //     } else if(r ==RequestType.Message){
    //         s="Message";
    //     } else if(r == RequestType.SignUp){
    //         s="SignUp";
    //     } else if(r==RequestType.POSITIVE){
    //         s="POSITIVE";
    //     } else if(r==RequestType.ERROR){
    //         s="ERROR";
    //     } else if(r==RequestType.Disconnect){
    //         s="Disconnect";
    //     }

    //     return s;
    // }

    public static synchronized void databaseInsertData(Request request) {
        // try {
        //     globalLocks.acquire();
        //     messageCollection.insertOne(request.toDocument());
        //     globalLocks.release();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        messageCollection.insertOne(request.toDocument());


        return;
    }

    public static synchronized List<Document> fetchUnsendMessages(String clientId) {
        List<Document> messages = new ArrayList<Document>();
        // try {
        //     globalLocks.acquire();
        //     messages = messageCollection.find(eq("receiverId", clientId)).into(new ArrayList<Document>());
        //     messageCollection.deleteMany(eq("receiverId", clientId));
        //     globalLocks.release();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        messages = messageCollection.find(eq("receiverId", clientId)).into(new ArrayList<Document>());
        messageCollection.deleteMany(eq("receiverId", clientId));

        return messages;
    }

    public static synchronized void addClientToOnlineList(SocketChannel channel, String clientId, String token) {
        
        rwlock.writeLock().lock();
        onlineClientsNew.put(clientId, new ClientInfo(clientId, channel, token));
        channelToClientId.put(channel, clientId);
        rwlock.writeLock().unlock();
            
        return;
    }

    public static synchronized boolean removeClientFromOnlineList(SocketChannel channel) {
        rwlock.writeLock().lock();
        if (channelToClientId.containsKey(channel)) {
            String clientId = channelToClientId.get(channel);
            channelToClientId.remove(channel);
            onlineClientsNew.remove(clientId);
            rwlock.writeLock().unlock();
            return true;
        }
        rwlock.writeLock().unlock();
        return false;
    }

    public static boolean checkClientOnline(String clientId) {
        rwlock.readLock().lock();
        boolean ans = onlineClientsNew.containsKey(clientId);
        rwlock.readLock().unlock();
        return ans;
    }

    public static ClientInfo getClientInfo(String clientId) {
        rwlock.readLock().lock();
        ClientInfo ans = onlineClientsNew.get(clientId);
        rwlock.readLock().unlock();
        return ans;
    }

    public static String generateToken(int len) {
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		return sb.toString();
    }

    public static boolean sendMessageTo(String recieverId, Request request) {
        
        if (GlobalVariables.onlineClientsNew.containsKey(recieverId)) {
            GlobalVariables.rwlock.readLock().lock();
            ClientInfo recieverInfo = GlobalVariables.onlineClientsNew.get(recieverId);
            GlobalVariables.rwlock.readLock().unlock();
            GlobalVariables.sendMessage.execute(new SendMessageTask(recieverInfo.getChannel(), request));
            return true;
        }
        if(GlobalVariables.userCollection.countDocuments(eq("userId", recieverId)) > 0){
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Offline");
            GlobalVariables.messageCollection.insertOne(request.toDocument());
            // GlobalVariables.globalLocks.release();
            return true;
        } 
        System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Does Not Exist");
        // GlobalVariables.globalLocks.release();
        return false;
    }
    
}
