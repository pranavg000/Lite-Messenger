package com.example.wa_client;

import android.util.Log;

public class SendRequestTask implements Runnable {

    private Request request;
    private static SendRequest sendRequest;
    private static String token;
    private String clientId;

    public SendRequestTask(Request.RequestType requestType, String receiverId, String data, String clientId) {
        this.clientId = clientId;
        this.request = new Request(requestType, clientId, receiverId, data, token);
    }

    public static void setToken(String token_m){
        token = token_m;
    }

    public static void setSendRequest(SendRequest sendRequest_m){
        sendRequest = sendRequest_m;
        Log.d("waclonedebug", "setSendRequest: "+(sendRequest_m!=null));
    }

    public static boolean isReady()
    {
        return (sendRequest!=null);
    }
    private void storeAndCloseConnection() {
        Log.d("waclonedebug", "Server is Offline, save to disk");
    }

    public void run() {
//        while(!SendRequestTask.isReady()){
//            ;
//        }
        Log.d("waclonedebug", request.getAction().name() + " " + String.valueOf(request.getTimeStamp()));
        Request.RequestType reqType = request.getAction();
//        if(reqType == Request.RequestType.NewChat){
//            globalVariables.addNewChatToMap(request.getRequestId(), request.getReceiverId());
//        }
        if(!sendRequest.sendRequestSafe(request)){
            storeAndCloseConnection();
        }
    }
}
