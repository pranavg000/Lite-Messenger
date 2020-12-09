package waclone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException
    {
        PrintStream o = new PrintStream(new File("A.txt")); 
        System.setOut(o); 

        GlobalVariables.printer = new Semaphore(1);
        ArrayList<SampleSendingThread> threads = new ArrayList<SampleSendingThread>();
        for(int i=0;i<100;i++){
            threads.add(new SampleSendingThread(Integer.toString(i)));
            threads.get(i).start();
        }
        while(true){

            boolean f=false;
            for(int i=0;i<100;i++){
                if(!threads.get(i).isAuthenticated){

                    f=true;
                    break;
                }
            }
            if(!f){
                break;
            }
        }

        GlobalVariables.printer.acquire();
        GlobalVariables.threadsReady = true;
        System.out.println("All threads authenticated "+GlobalVariables.threadsReady);
        GlobalVariables.printer.release();

    }
}
