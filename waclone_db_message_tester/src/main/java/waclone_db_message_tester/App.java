package waclone_db_message_tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        PrintStream o = new PrintStream(new File("A.txt"));
        System.setOut(o);

        GlobalVariables.printer = new Semaphore(1);
        GlobalVariables.tokens = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Scanner ready");
        while (true) {

            int type = scanner.nextInt();
            if (type == 0) {
                ArrayList<SampleSendingThread> threads = new ArrayList<SampleSendingThread>();
                for (int i = 0; i < 10; i++) {
                    threads.add(new SampleSendingThread(Integer.toString(i)));
                    threads.get(i).start();
                }
                while (true) {
                    boolean f = false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {

                            f = true;
                            break;
                        }
                    }
                    if (!f) {
                        break;
                    }
                }
                GlobalVariables.printer.acquire();
                GlobalVariables.senderThreadsReady = true;
                GlobalVariables.printer.release();

                System.out.println("All sender threads authenticated.");

                for (int i = 0; i < 10; i++) {
                    threads.get(i).join();
                }

                System.out.println("Sender threads done. Scanner ready.");
                GlobalVariables.printer.acquire();
                GlobalVariables.senderThreadsReady = false;
                GlobalVariables.printer.release();

            } else if (type == 1) {
                ArrayList<SampleReceivingThread> threads = new ArrayList<SampleReceivingThread>();
                for (int i = 10; i < 20; i++) {
                    threads.add(new SampleReceivingThread(Integer.toString(i)));
                    threads.get(i - 10).start();
                }
                while (true) {
                    boolean f = false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {
                            f = true;
                            break;
                        }
                    }
                    if (!f) {
                        break;
                    }
                }
                GlobalVariables.printer.acquire();
                GlobalVariables.receiverThreadsReady = true;
                GlobalVariables.printer.release();

                System.out.println("All receiver threads authenticated.");

                for (int i = 0; i < 10; i++) {
                    threads.get(i).join();
                }

                System.out.println("Receiver threads done. Scanner ready.");
                GlobalVariables.printer.acquire();
                GlobalVariables.receiverThreadsReady = false;
                GlobalVariables.printer.release();

            } else if(type==2){
                ArrayList<SampleAuthenticatedSendingThreads> threads = new ArrayList<SampleAuthenticatedSendingThreads>();
                for (int i = 0; i < 10; i++) {
                    threads.add(new SampleAuthenticatedSendingThreads(Integer.toString(i)));
                    threads.get(i).start();
                }

                while (true) {
                    boolean f = false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {
                            f = true;
                            break;
                        }
                    }
                    if (!f) {
                        break;
                    }
                }

                GlobalVariables.printer.acquire();
                GlobalVariables.authenticatedSendingThreadsReady=true;
                GlobalVariables.printer.release();

                System.out.println("All sample authenticated sending threads ready");

                for(int i=0;i<10;i++){
                    threads.get(i).join();
                }

                System.out.println("All sample authenticated sending threads done!!!");

            } else if(type==3) {

                ArrayList<SampleAuthenticatedReceivingThreads> threads = new ArrayList<SampleAuthenticatedReceivingThreads>();
                for (int i = 0; i < 10; i++) {
                    threads.add(new SampleAuthenticatedReceivingThreads(Integer.toString(i)));
                    threads.get(i).start();
                }

                while (true) {
                    boolean f = false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {
                            f = true;
                            break;
                        }
                    }
                    if (!f) {
                        break;
                    }
                }

                GlobalVariables.printer.acquire();
                GlobalVariables.authenticatedSendingThreadsReady=true;
                GlobalVariables.printer.release();

                System.out.println("All sample authenticated sending threads ready");

                for(int i=0;i<10;i++){
                    threads.get(i).join();
                }

                System.out.println("All sample authenticated sending threads done!!!");

            } else if(type==4) {
                ArrayList<AuthenticationTestThread> threads = new ArrayList<AuthenticationTestThread>();
                for (int i = 0; i < 10; i++) {
                    threads.add(new AuthenticationTestThread(Integer.toString(i)));
                    threads.get(i).start();
                }
                while (true) {
                    boolean f = false;
                    for (int i = 0; i < 10; i++) {
                        if (!threads.get(i).isAuthenticated) {
                            f = true;
                            break;
                        }
                    }
                    if (!f) {
                        break;
                    }
                }
                GlobalVariables.printer.acquire();
                GlobalVariables.authenticationThreadsReady=true;
                GlobalVariables.printer.release();

                System.out.println("All authentication threads ready");

                for(int i=0;i<10;i++){
                    threads.get(i).join();
                }

                System.out.println("All authentication threads done!!!");


            } else {
                scanner.close();
                return;
            }
        }
    }
}
