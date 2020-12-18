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
        } else if (reqType == RequestType.NewChat) {
            System.out.println("New Chat");
        } else if (reqType == RequestType.Message) {
            System.out.println("Send message");
            ClientInfo clientInfo = GlobalVariables.getClientInfo(clientId);
            String userToken = clientInfo.getToken();
            // String userToken = GlobalVariables.userCollection
            //     .find(eq("userId",clientId)).first().getString("token");
            if(userToken.equals(request.getToken())){
                return sendMessageTo(recieverId, request);
            } else {
                System.out.println("UNAUTHORISED ACCESS WAS MADE!!! CLIENT PRETENDING TO BE " + clientId);
                System.out.println("Actual token:"+userToken+" Used token: "+request.getToken());
                return false;
            }
        } else if(reqType == RequestType.Disconnect){
            ClientInfo clientInfo = GlobalVariables.getClientInfo(clientId);
            String userToken = clientInfo.getToken();
            if(userToken.equals(request.getToken())){
                System.out.println("Client with id "+clientId+" wants to disconnect!");
                // Document disconnectDocument = new Document().append("senderId","SERVER").append("receiverId", clientId)
                //     .append("action", "POSITIVE").append("data","Disconnected successfully!").append("token","NULL");
                Request disconnectMessage = new Request(RequestType.POSITIVE, "SERVER", clientId, "Disconnected successfully!", "NULL");
                sendMessageTo(clientId, disconnectMessage);
                GlobalVariables.removeClientFromOnlineList(channel);

            } else {
                // Document disconnectDocument = new Document().append("senderId","SERVER").append("receiverId", clientId)
                //     .append("action", "ERROR").append("data","UNAUTHORISED ACCESS!!!").append("token","NULL");
                Request disconnectMessage = new Request(RequestType.ERROR, "SERVER", clientId, "UNAUTHORISED ACCESS!!!", "NULL");
                sendMessageTo(clientId, disconnectMessage);
            }
        }else {
            System.out.println("FFFFFFFFFFFFFFFFFFFFFF Unknown Command");
            // Unkown command return error response
            return false;
        }
        return true;
    }

    private Boolean isAuth(Request request) {
        try {
            GlobalVariables.globalLocks.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        if ((request.getAction() == RequestType.Auth)
                && (GlobalVariables.userCollection.countDocuments(eq("userId", request.getSenderId())) > 0)) {
            String userToken = (String) GlobalVariables.userCollection.find(eq("userId", request.getSenderId())).first()
                    .get("token");
            GlobalVariables.globalLocks.release();
            if (userToken.equals(request.getToken())) {
                return true;
            }
        }
        GlobalVariables.globalLocks.release();
        return false;
    }

    private Boolean handleAuth(Request request) {
        RequestType reqType = request.getAction();
        System.out.println(reqType + "HI");
        if (isAuth(request)) {
            GlobalVariables.addClientToOnlineList(channel, clientId, request.getToken());

            // Send Approval Document
            // Document approvalDoc = new Document().append("senderId", "SERVER").append("receiverId", clientId)
            //         .append("action", "POSITIVE").append("token", "NULL").append("data", "Authentication Successful!");
            Request approvalMessage = new Request(RequestType.POSITIVE, "SERVER", clientId, "Authentication Successful!", "NULL");
            // Request approvalMessage = new Request(approvalDoc);
            sendMessageTo(clientId, approvalMessage);

            // Deliver stored messages to user
            List<Document> messageList = GlobalVariables.fetchUnsendMessages(clientId);
            System.out.println(messageList);
            // Delete these messages from database
            for (Document message : messageList) {
                Request r = new Request(message);
                sendMessageTo(r.getReceiverId(), r);
            }
            System.out.println("Auth done for" + clientId);

            return true;

        } else if (reqType == RequestType.SignUp) {
            System.out.println(clientId + " has put up a sign up request.");
            // Only sign up if user doesn't already exist
            try {
                GlobalVariables.globalLocks.acquire();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return false;
            }
            if (GlobalVariables.userCollection.countDocuments(eq("userId", clientId)) == 0) {

                // Create token for new user
                String tokenToAssign = GlobalVariables.generateToken(20);
                // Add new user to Database
                GlobalVariables.userCollection
                        .insertOne(new Document().append("userId", clientId).append("token", tokenToAssign));
                GlobalVariables.globalLocks.release();
                // Add client to online list, token = tokenToAssign
                GlobalVariables.addClientToOnlineList(channel, clientId, tokenToAssign);

                // Send Approval Document
                // Document approvalDoc = new Document().append("senderId", "SERVER").append("receiverId", clientId)
                //         .append("action", "POSITIVE").append("data", "Account created successfully!")
                //         .append("token", tokenToAssign);
                Request approvalMessage = new Request(RequestType.POSITIVE, "SERVER", clientId, "Account created successfully!", tokenToAssign);
                // Request approvalMessage = new Request(approvalDoc);
                sendMessageTo(clientId, approvalMessage);
                return true;

            } else {
                GlobalVariables.globalLocks.release();
                // Reject if user already exists - Send Rejection Document
                GlobalVariables.addClientToOnlineList(channel, clientId, "NULL");
                // Document rejectionDoc = new Document().append("senderId", "SERVER").append("receiverId", clientId)
                //     .append("action","ERROR").append("data","User already exists!!! Can't sign up!").append("token","NULL");
                Request rejectionMessage = new Request(RequestType.ERROR, "SERVER", clientId, "User already exists!!! Can't sign up!", "NULL");
                // Request rejectionMessage = new Request(rejectionDoc);
                sendMessageTo(clientId, rejectionMessage);
                GlobalVariables.removeClientFromOnlineList(channel);

                return false;
            }
        } else {
            // The user is not authenticated has sent some non-auth message
            GlobalVariables.addClientToOnlineList(channel, clientId, "NULL");
            // Document rejectionDoc = new Document().append("senderId", "SERVER").append("receiverId",clientId)
            //     .append("token","NULL").append("action","ERROR").append("data","UNAUTHORISED ACCESS!!!");
            Request rejectionMessage = new Request(RequestType.ERROR, "SERVER", clientId, "UNAUTHORISED ACCESS!!!", "NULL");
            // Request rejectionMessage = new Request(rejectionDoc);
            sendMessageTo(clientId, rejectionMessage);
            GlobalVariables.removeClientFromOnlineList(channel);
            return false;
        }
    }
    
    private boolean sendMessageTo(String recieverId, Request request) {
        try {
			GlobalVariables.globalLocks.acquire();
		} catch (InterruptedException e) {
            e.printStackTrace();
            return false;
		}
        if (GlobalVariables.onlineClientsNew.containsKey(recieverId)) {
            ClientInfo recieverInfo = GlobalVariables.onlineClientsNew.get(clientId);
            GlobalVariables.globalLocks.release();
            GlobalVariables.sendMessage.execute(new SendMessageTask(recieverInfo.getChannel(), request));
            return true;
        } else {
            if(GlobalVariables.userCollection.countDocuments(eq("userId", recieverId)) > 0){
                System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Offline");
                GlobalVariables.messageCollection.insertOne(request.toDocument());
                GlobalVariables.globalLocks.release();
                return true;
            } else {
                System.out.println("FFFFFFFFFFFFFFFFFFFFFF Reciever Does Not Exist");
                GlobalVariables.globalLocks.release();
                return false;
            }
        }
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

    
}
