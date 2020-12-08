package server;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting");

        ConnectionListeningThread clt = new ConnectionListeningThread();
        clt.start();
    }
}
