package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import com.mongodb.client.MongoClients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.util.HashMap;

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

        GlobalVariables.globalLocks = new Semaphore(1);

        GlobalVariables.mongoClient = MongoClients.create(GlobalVariables.connectionString);
        GlobalVariables.database = GlobalVariables.mongoClient.getDatabase("wacloneDB");
        GlobalVariables.userCollection = GlobalVariables.database.getCollection("users");
        GlobalVariables.messageCollection = GlobalVariables.database.getCollection("messages");
        
        GlobalVariables.onlineClients = new HashMap<String,ClientInfo>();
        GlobalVariables.onlineClientsNew = new HashMap<String,ClientInfoNew>();
        GlobalVariables.channelToClientId = new HashMap<Channel,String>();
        GlobalVariables.outbox = new LinkedBlockingDeque<Request>();
        GlobalVariables.sendMessage = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        GlobalVariables.receiveMessage = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        // ConnectionListeningThread clt = new ConnectionListeningThread();
        ConnectionListeningThreadNew clt = new ConnectionListeningThreadNew();
        clt.start();

        System.out.println("End of app.java");

    }
}
