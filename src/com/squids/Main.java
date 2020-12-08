package com.squids;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        //Setting up Semaphores
        GlobalVariables.messageSem = new Semaphore(1);
        GlobalVariables.authSem = new Semaphore(1);
        GlobalVariables.newChatSem = new Semaphore(1);

        //Setting up queues
        GlobalVariables.messageRequests = new LinkedList<>();
        GlobalVariables.authRequests = new LinkedList<>();
        GlobalVariables.newChatRequests = new LinkedList<>();

        //Creating and starting listening threads
        MessageListeningThread mlt = new MessageListeningThread();
        AuthListeningThread alt = new AuthListeningThread();
        NewChatListeningThread nlt = new NewChatListeningThread();
        mlt.start();
        alt.start();
        nlt.start();

        //Create and start processing threads
        MessageProcessingThread mpt = new MessageProcessingThread();
        mpt.start();


    }
}
