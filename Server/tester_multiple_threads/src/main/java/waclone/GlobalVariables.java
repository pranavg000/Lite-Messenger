package waclone;

import java.util.concurrent.Semaphore;

class GlobalVariables {

    public static Semaphore printer;
    public static int Nclients = 100;
    public static int threadsReady = 0;
    public static enum RequestType { 
        Auth, NewChat, Message
    }
    
}