package server;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class GlobalVariables {
    public static Map<String,BlockingQueue<Request>> clientSendBox;
    public static enum RequestType { 
        Auth, NewChat, Message
    }
}
