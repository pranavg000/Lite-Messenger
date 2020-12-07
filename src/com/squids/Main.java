package com.squids;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
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


    }
}
