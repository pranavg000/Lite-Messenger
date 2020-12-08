package server;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class GlobalVariables {
    public static Map<String,LinkedBlockingQueue<Request>> clientSendBox;
}
