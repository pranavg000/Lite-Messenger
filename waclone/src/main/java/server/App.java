package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
// import org.bson.Document;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        // Connecting to database and setting up database variables.
        try(MongoClient mongoClient = MongoClients.create(GlobalVariables.connectionString)){
            System.out.println("Connected to database.");

            GlobalVariables.database = mongoClient.getDatabase("wacloneDB");
            GlobalVariables.userCollection = GlobalVariables.database.getCollection("users");
            GlobalVariables.messageCollection = GlobalVariables.database.getCollection("messages");
            // GlobalVariables.messageCollection.insertOne(new Document("_id","helo"));
        }

        GlobalVariables.onlineClients = new HashMap<String,ClientInfo>();
        GlobalVariables.outbox = new LinkedBlockingDeque<Request>();
        GlobalVariables.sendMessage = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();

        System.out.println("End of app.java");

    }
}
