package waclone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class App {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        PrintStream o = new PrintStream(new File("A.txt"));
        System.setOut(o);

        GlobalVariables.printer = new Semaphore(1);
        ArrayList<SampleSendingThread> threads = new ArrayList<SampleSendingThread>();
        for (int i = 0; i < GlobalVariables.Nclients; i++) {
            threads.add(new SampleSendingThread(Integer.toString(i)));
            threads.get(i).start();
        }
        while (GlobalVariables.threadsReady < GlobalVariables.Nclients)
            ;

        GlobalVariables.printer.acquire();
        System.out.println("All threads authenticated " + GlobalVariables.threadsReady);
        GlobalVariables.printer.release();
        for (int i = 0; i < GlobalVariables.Nclients; i++) {
            threads.get(i).join();
        }

        GlobalVariables.printer.acquire();
        System.out.println("Machayaaaaaaaaaaa!!!!!!!");
        GlobalVariables.printer.release();
    }
}