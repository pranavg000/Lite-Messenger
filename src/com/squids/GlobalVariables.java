package com.squids;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GlobalVariables {

    //Queue Semaphores
    public static Semaphore messageSem;
    public static Semaphore authSem;
    public static Semaphore newChatSem;

    //Request queues
    public static Queue<String> messageRequests;
    public static Queue<String> authRequests;
    public static Queue<String> newChatRequests;
}
