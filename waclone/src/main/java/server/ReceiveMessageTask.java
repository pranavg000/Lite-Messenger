package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bson.Document;

import server.GlobalVariables.RequestType;

public class ReceiveMessageTask implements Runnable {

    private String clientId;
    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocate(4096);
    private SelectionKey selectorKey;

    private Boolean isAuth(Request request) {
        if (request.getAction() == RequestType.Auth)
            return true;
        return false;
    }

    private void sendMessageTo(String recieverId, Request request) {
        try {
			GlobalVariables.globalLocks.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
            return;
		}
        if (GlobalVariables.onlineClientsNew.containsKey(recieverId)) {
            ClientInfoNew recieverInfo = GlobalVariables.onlineClientsNew.get(clientId);
            GlobalVariables.globalLocks.release();
            GlobalVariables.sendMessage.execute(new SendMessageTaskNew(recieverInfo.getChannel(), request));
        } else {
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Offline");
            GlobalVariables.messageCollection.insertOne(request.toDocument());
            GlobalVariables.globalLocks.release();
        }
        return;
    }

    private Boolean processRequest(Request request) {
        RequestType reqType = request.getAction();
        String recieverId = request.getReceiverId();
        System.out.println(reqType);
        if (clientId.isEmpty())
            clientId = request.getSenderId();
        if (!GlobalVariables.checkClientOnline(clientId)) {
            System.out.println(reqType + "HI");
            if (isAuth(request)) {
                GlobalVariables.addClientToOnlineList(channel, clientId);
                List<Document> messageList = GlobalVariables.fetchUnsendMessages(clientId);
                // Deliver stored messages to user
                System.out.println(messageList);
                // Delete these messages from database
                for (Document message : messageList) {
                    Request r = new Request(message);
                    sendMessageTo(r.getReceiverId(), r);
                }
                System.out.println("Auth done for" + clientId);
            }
        } else if (reqType == RequestType.NewChat) {
            System.out.println("New Chat");
        } else if (reqType == RequestType.Message) {
            System.out.println("Send message");
            sendMessageTo(recieverId, request);
        } else {
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Unknown Command");
            // Unkown command return error response
            return false;
        }
        return true;
    }

    private void closeConnection() {
        try {
            System.out.println("Closing Channel");
            GlobalVariables.removeClientFromOnlineList(channel);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReceiveMessageTask(SelectionKey selectorKey) {
        this.selectorKey = selectorKey;
        clientId = "";
        channel = (SocketChannel) selectorKey.channel();
    }

    public void run() {
        Gson gson = new Gson();
        Request request;
        String input = "";
        buffer.clear();
        try {
            while (channel.read(buffer) > 0)
                ;
        } catch (IOException e1) {
            e1.printStackTrace();
            closeConnection();
            return;
        }
        int len = buffer.position();
        System.out.println(len);
        if (len <= 0) {
            closeConnection();
            return;
        }
        int stp = 0;
        String completeInput = new String(buffer.array(), StandardCharsets.UTF_8);
        while (stp < len) {
            short clen = buffer.getShort(stp);
            input = completeInput.substring(stp + 2, clen + stp + 2);
            stp += 2 + clen;
            System.out.println(input);
            System.out.println(clen);
            try {
                request = gson.fromJson(input, Request.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return;
            }
            processRequest(request);
        }
        selectorKey.interestOps(selectorKey.interestOps() | SelectionKey.OP_READ);
        selectorKey.selector().wakeup();
        return;
    }
}
