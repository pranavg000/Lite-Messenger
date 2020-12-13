package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import com.mongodb.client.MongoClients;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        GlobalVariables.databaseLock = new Semaphore(1);
        GlobalVariables.mongoClient = MongoClients.create(GlobalVariables.connectionString);
        GlobalVariables.database = GlobalVariables.mongoClient.getDatabase("wacloneDB");
        GlobalVariables.userCollection = GlobalVariables.database.getCollection("users");
        GlobalVariables.messageCollection = GlobalVariables.database.getCollection("messages");
        
        GlobalVariables.onlineClients = new HashMap<String,ClientInfo>();
        GlobalVariables.outbox = new LinkedBlockingDeque<Request>();
        GlobalVariables.sendMessage = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();

        System.out.println("End of app.java");

    }
}
