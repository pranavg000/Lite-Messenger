package waclone_db_message_tester;

import java.util.concurrent.Semaphore;

class GlobalVariables {

    public static Semaphore printer;
    public static boolean senderThreadsReady;
    public static boolean receiverThreadsReady;
    public static enum RequestType { 
        Auth, NewChat, Message, SignUp
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
        }

        return s;
    }

}