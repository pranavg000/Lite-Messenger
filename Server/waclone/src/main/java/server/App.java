package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.mongodb.client.MongoClients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class App {
    public static void main(String[] args) {
        PrintStream o;
        try {
            o = new PrintStream(new File("ServerLogs.txt"));
            System.setOut(o);
            System.setErr(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Starting Server");

        GlobalVariables.mongoClient = MongoClients.create(GlobalVariables.connectionString);
        GlobalVariables.database = GlobalVariables.mongoClient.getDatabase("wacloneDB");
        GlobalVariables.userCollection = GlobalVariables.database.getCollection("users");
        GlobalVariables.messageCollection = GlobalVariables.database.getCollection("messages");
        GlobalVariables.groupCollection = GlobalVariables.database.getCollection("groups");
        
        GlobalVariables.onlineClientsNew = new HashMap<String,ClientInfo>();
        GlobalVariables.channelToClientId = new HashMap<Channel,String>();
        GlobalVariables.outbox = new LinkedBlockingDeque<Request>();
        GlobalVariables.sendMessageService = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        GlobalVariables.receiveMessageService = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        GlobalVariables.rwlock = new ReentrantReadWriteLock();

        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();

        System.out.println("End of app.java");

    }
}
