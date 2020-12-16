package waclone_db_message_tester;

import java.util.Map;
import java.util.concurrent.Semaphore;

class GlobalVariables {

    public static Semaphore printer;
    public static boolean senderThreadsReady=false;
    public static boolean receiverThreadsReady=false;
    public static boolean authenticatedSendingThreadsReady=false;
    public static boolean authenticatedReceivingThreadsReady=false;
    public static boolean authenticationThreadsReady=false;
    public static Map<String,String> tokens;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp, Disconnect, POSITIVE, ERROR
    }
    public static String getActionString(RequestType r){
        String s="";
        if(r == RequestType.Auth){
            s="Auth";
        } else if(r == RequestType.NewChat){
            s="NewChat";
        } else if(r ==RequestType.Message){
            s="Message";
        } else if(r == RequestType.SignUp){
            s="SignUp";
        } else if(r == RequestType.POSITIVE){
            s="POSITIVE";
        } else if(r == RequestType.ERROR){
            s="ERROR";
        } else if(r==RequestType.Disconnect){
            s="Disconnect";
        }

        return s;
    }

}