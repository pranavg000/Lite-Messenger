package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import static com.mongodb.client.model.Filters.*;

import org.bson.Document;

import server.GlobalVariables.RequestType;

public class ReceiveMessageTask implements Runnable {

    private String clientId;
    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocate(4096);
    private SelectionKey selectorKey;

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

    private Boolean processRequest(Request request) {
        RequestType reqType = request.getAction();
        String recieverId = request.getReceiverId();
        System.out.println(reqType);
        if (clientId.isEmpty())
            clientId = request.getSenderId();
        if (!GlobalVariables.checkClientOnline(clientId)) { // Client is not authenticated
            return handleAuth(request);
        } 

        ClientInfo clientInfo = GlobalVariables.getClientInfo(clientId);
        if(!request.getToken().equals(clientInfo.getToken())){
            Request rejectionMessage = new Request(RequestType.InvalidToken, GlobalVariables.serverId, clientId, "UNAUTHORISED ACCESS!!!", "NULL");
            GlobalVariables.sendMessageTo(clientId, rejectionMessage);
        }


        if (reqType == RequestType.NewChat) {
            System.out.println("New Chat");
            
            if(GlobalVariables.userCollection.countDocuments(eq("userId",recieverId)) > 0){
                Request approvalReq = new Request(RequestType.NewChatPositive, GlobalVariables.serverId, clientId, recieverId, "NULL");
                GlobalVariables.sendMessageTo(clientId, approvalReq);
            } else {
                Request rejectionReq = new Request(RequestType.UserNotFound, GlobalVariables.serverId, clientId, "USER NOT FOUND", "NULL");
                GlobalVariables.sendMessageTo(clientId, rejectionReq);
            }
            
        } else if (reqType == RequestType.Message) {
            System.out.println("Send message");
            
            return GlobalVariables.sendMessageTo(recieverId, request);
            
        } else if(reqType == RequestType.Disconnect){
            System.out.println("Client with id "+clientId+" wants to disconnect!");
            Request disconnectMessage = new Request(RequestType.POSITIVE, GlobalVariables.serverId, clientId, "Disconnected successfully!", "NULL");
            GlobalVariables.sendMessageTo(clientId, disconnectMessage);
            GlobalVariables.removeClientFromOnlineList(channel);
        
        } 
        else if(reqType == RequestType.MessageRead){
            System.out.println("Message Read");
            // Send to sender (Read receipt)
            Request readReceipt = new Request(RequestType.MessageRead, request.getSenderId(), recieverId, request.getRequestId(), "");
            return GlobalVariables.sendMessageTo(recieverId, readReceipt);
        }
        else {
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Unknown Command");
            // Unkown command return error response
            return false;
        }
        return true;
    }

    private Boolean isAuth(Request request) {
        // try {
        //     GlobalVariables.globalLocks.acquire();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        //     return false;
        // }
        if ((request.getAction() == RequestType.Auth)
                && (GlobalVariables.userCollection.countDocuments(eq("userId", request.getSenderId())) > 0)) {
            String userToken = (String) GlobalVariables.userCollection.find(eq("userId", request.getSenderId())).first()
                    .get("token");
            // GlobalVariables.globalLocks.release();
            if (userToken.equals(request.getToken())) {
                return true;
            }
        }
        // GlobalVariables.globalLocks.release();
        return false;
    }

    private Boolean handleAuth(Request request) {
        RequestType reqType = request.getAction();
        if (isAuth(request)) {
            GlobalVariables.addClientToOnlineList(channel, clientId, request.getToken());

            // Send Approval Document
            Request approvalMessage = new Request(RequestType.POSITIVE, GlobalVariables.serverId, clientId, "Authentication Successful!", "NULL");
            GlobalVariables.sendMessageTo(clientId, approvalMessage);

            // Deliver stored messages to user
            List<Document> messageList = GlobalVariables.fetchUnsendMessages(clientId);
            System.out.println(messageList);
            // Delete these messages from database
            for (Document message : messageList) {
                Request r = new Request(message);
                System.out.println(r.getRequestId());
                GlobalVariables.sendMessageTo(r.getReceiverId(), r);
            }
            System.out.println("Auth done for" + clientId);

            return true;

        } else if (reqType == RequestType.SignUp) {
            System.out.println(clientId + " has put up a sign up request.");
            // Only sign up if user doesn't already exist
            // try {
            //     GlobalVariables.globalLocks.acquire();
            // } catch (InterruptedException e1) {
            //     e1.printStackTrace();
            //     return false;
            // }
            if (GlobalVariables.userCollection.countDocuments(eq("userId", clientId)) == 0) {

                // Create token for new user
                String tokenToAssign = GlobalVariables.generateToken(20);
                // Add new user to Database
                GlobalVariables.userCollection
                        .insertOne(new Document().append("userId", clientId).append("token", tokenToAssign));
                // GlobalVariables.globalLocks.release();
                // Add client to online list, token = tokenToAssign
                GlobalVariables.addClientToOnlineList(channel, clientId, tokenToAssign);

                // Send Approval Document
                Request approvalMessage = new Request(RequestType.SignUpSuccessful, GlobalVariables.serverId, clientId, "Account created successfully!", tokenToAssign);
                GlobalVariables.sendMessageTo(clientId, approvalMessage);
                return true;

            } else {
                // GlobalVariables.globalLocks.release();
                // Reject if user already exists - Send Rejection Document
                GlobalVariables.addClientToOnlineList(channel, clientId, "NULL");
                Request rejectionMessage = new Request(RequestType.ERROR, GlobalVariables.serverId, clientId, "User already exists!!! Can't sign up!", "NULL");
                GlobalVariables.sendMessageTo(clientId, rejectionMessage);
                GlobalVariables.removeClientFromOnlineList(channel);

                return false;
            }
        } else {
            // The user is not authenticated has sent some non-auth message
            GlobalVariables.addClientToOnlineList(channel, clientId, "NULL");
            Request rejectionMessage = new Request(RequestType.ERROR, GlobalVariables.serverId, clientId, "UNAUTHORISED ACCESS!!!", "NULL");
            GlobalVariables.sendMessageTo(clientId, rejectionMessage);
            GlobalVariables.removeClientFromOnlineList(channel);
            return false;
        }
    }
    
    // private boolean GlobalVariables.sendMessageTo(String recieverId, Request request) {
        
    //     if (GlobalVariables.onlineClientsNew.containsKey(recieverId)) {
    //         GlobalVariables.rwlock.readLock().lock();
    //         ClientInfo recieverInfo = GlobalVariables.onlineClientsNew.get(recieverId);
    //         GlobalVariables.rwlock.readLock().unlock();
    //         GlobalVariables.sendMessage.execute(new SendMessageTask(recieverInfo.getChannel(), request));
    //         return true;
    //     }
    //     if(GlobalVariables.userCollection.countDocuments(eq("userId", recieverId)) > 0){
    //         System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Offline");
    //         GlobalVariables.messageCollection.insertOne(request.toDocument());
    //         // GlobalVariables.globalLocks.release();
    //         return true;
    //     } 
    //     System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Does Not Exist");
    //     // GlobalVariables.globalLocks.release();
    //     return false;
    // }

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

    
}
