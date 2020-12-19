package waclone_db_message_tester;

import java.util.Map;
import java.util.concurrent.Semaphore;

class GlobalVariables {

    public static Semaphore printer;
    public final static String serverId = "SERVER";
    public static boolean senderThreadsReady=false;
    public static boolean receiverThreadsReady=false;
    public static boolean authenticatedSendingThreadsReady=false;
    public static boolean authenticatedReceivingThreadsReady=false;
    public static boolean authenticationThreadsReady=false;
    public static Map<String,String> tokens;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp, Disconnect, POSITIVE, ERROR, InvalidToken, UserNotFound, MessageReceived
    }

}