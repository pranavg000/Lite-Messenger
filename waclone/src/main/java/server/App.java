package server;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        GlobalVariables.databaseLock = new Semaphore(1);

        GlobalVariables.onlineClients = new HashMap<String,ClientInfo>();
        GlobalVariables.outbox = new LinkedBlockingDeque<Request>();
        GlobalVariables.sendMessage = Executors.newFixedThreadPool(GlobalVariables.Nthreads);
        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();

        System.out.println("End of app.java");

    }
}
