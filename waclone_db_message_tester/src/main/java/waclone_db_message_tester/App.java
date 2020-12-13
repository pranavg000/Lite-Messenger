package waclone_db_message_tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
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

        Scanner scanner = new Scanner(System.in);

        System.out.println("Scanner ready");
        while(true){
            int type = scanner.nextInt();
            if(type==0){
                ArrayList<SampleSendingThread> threads = new ArrayList<SampleSendingThread>();
                for (int i = 0; i < 10; i++) {
                    threads.add(new SampleSendingThread(Integer.toString(i)));
                    threads.get(i).start();
                }
                while(true){
                    boolean f=false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {

                            f = true;
                            break;
                        }
                    }
                    if(!f){
                        break;
                    }
                }
                GlobalVariables.printer.acquire();
                GlobalVariables.senderThreadsReady = true;
                System.out.println("All sender threads authenticated.");
                GlobalVariables.printer.release();

                for (int i = 0; i < 10; i++) {
                    threads.get(i).join();
                }
                GlobalVariables.printer.acquire();
                System.out.println("Sender threads done. Scanner ready.");
                GlobalVariables.printer.release();

            } else if(type==1){
                ArrayList<SampleReceivingThread> threads = new ArrayList<SampleReceivingThread>();
                for (int i = 10; i < 20; i++) {
                    threads.add(new SampleReceivingThread(Integer.toString(i)));
                    threads.get(i-10).start();
                }
                while(true){
                    boolean f=false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {
                            f = true;
                            break;
                        }
                    }
                    if(!f){
                        break;
                    }
                }
                GlobalVariables.printer.acquire();
                GlobalVariables.receiverThreadsReady = true;
                System.out.println("All receiver threads authenticated.");
                GlobalVariables.printer.release();

                for (int i = 0; i < 10; i++) {
                    threads.get(i).join();
                }

                GlobalVariables.printer.acquire();
                System.out.println("Receiver threads done. Scanner ready.");
                GlobalVariables.printer.release();

            }
        }
    }
}
