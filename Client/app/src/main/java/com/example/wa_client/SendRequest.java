package com.example.wa_client;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

public class SendRequest {
    private Gson gson;
    private Socket socket;
    private String ipAddress;
    private int port;
    private DataOutputStream outputStream;

    public SendRequest(String ipAddress, int port) {
        this.socket = null;
        this.ipAddress = ipAddress;
        this.port = port;

        try {
            socket = new Socket(ipAddress, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e) {
            Log.d("waclonedebug", "socket Error");
            e.printStackTrace();
            return;
        }
        gson = new Gson();
    }

//    public boolean sendRequestSafe(Request.RequestType requestType, String senderId, String data) {
//        Request request;
//        if(requestType == Request.RequestType.SignUp){
//            request = new Request(requestType, clientId, senderId, data, "NULL");
//        }
//        else{
//            if(token != "NULL")
//                request = new Request(requestType, clientId, senderId, data, token);
//            else
//                return false;
//        }
//        return sendRequest(request);
//    }

    public boolean sendRequestSafe(Request request) {
        if(request.getAction() != Request.RequestType.SignUp && request.getToken() == "NULL"){
                return false;
        }
        return sendRequest(request);
    }

    private boolean sendRequest(Request request) {
        try {
            outputStream.writeUTF(gson.toJson(request));
            return true;
        } catch (IOException e) {
            Log.d("waclonedebug", "sendRequest: failed");
            e.printStackTrace();
            return false;
        }
    }

    public Socket getSocket(){
        return socket;
    }

    protected void finalize() {
        try{
            outputStream.close();
            socket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
