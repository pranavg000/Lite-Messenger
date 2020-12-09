package waclone;

import java.util.concurrent.Semaphore;

class GlobalVariables {

    public static Semaphore printer;
    public static boolean threadsReady = false;
    public static enum RequestType { 
        Auth, NewChat, Message
    }
}