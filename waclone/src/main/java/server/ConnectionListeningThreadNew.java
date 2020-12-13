package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ConnectionListeningThreadNew extends Thread {

    private void registerChannel(Selector selector, SelectableChannel channel, int ops) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    private void startServer() throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(new InetSocketAddress(5000));
        System.out.println(serverChannel);
        Selector selector;

        selector = Selector.open();
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Connection Listening Socket ready and listening at port 5000");

        while (true) {
            int n = selector.select();
            if (n == 0)
                continue;
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey curKey = iter.next();

                if (!curKey.isValid()) {
                    continue;
                }

                if (curKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) curKey.channel();
                    SocketChannel channel = server.accept();
                    registerChannel(selector, channel, SelectionKey.OP_READ);
                } else if (curKey.isReadable()) {
                    curKey.interestOps(curKey.interestOps() & (~SelectionKey.OP_READ));
                    GlobalVariables.receiveMessage.execute(new ReceiveMessageTask(curKey));
                }

                iter.remove();
            }
        }
    }

    public void run() {
        System.out.println("Connection Listening Thread activated");
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
};
