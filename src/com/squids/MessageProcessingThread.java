package com.squids;
import java.util.Queue;

public class MessageProcessingThread extends Thread{
    public void run(){
        System.out.println("Message processing thread activated.");

        while(true){
            try {
                GlobalVariables.messageSem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int size = GlobalVariables.messageRequests.size();
//            System.out.printf("%d\n", size);
            if(size != 0) {
                String nextMessage = GlobalVariables.messageRequests.remove();

                //Process Next message
                System.out.printf("Message being processed: %s\n", nextMessage);
            }
            GlobalVariables.messageSem.release();
        }
    }
}
