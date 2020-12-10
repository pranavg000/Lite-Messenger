package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.mongodb.client.MongoClients;

import org.bson.Document;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        // Connecting to Database and creating an instance.

        GlobalVariables.mongoClient = MongoClients.create("mongodb+srv://wacloneAPH:waclonemen3001@cluster0.etq8i.mongodb.net/test?retryWrites=true&w=majority");

        System.out.println("Connected to database.");

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
