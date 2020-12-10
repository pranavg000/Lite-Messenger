package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        // Connecting to Database and creating an instance.
        GlobalVariables.uri = new MongoClientURI("mongodb://wacloneAPH:waclonemen3001@cluster0.etq8i.mongodb.net/wacloneDB?retryWrites=true&w=majority");
        try {
            GlobalVariables.mongoClient = new MongoClient(GlobalVariables.uri);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Connected to database.");

        GlobalVariables.database = GlobalVariables.mongoClient.getDB("wacloneDB");
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
