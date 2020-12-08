package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

public class SendRequest {

    private Gson gson;
    private Socket socket;
    private String ipAddress;
    private int port;
    private String myPhoneNo;

    public SendRequest(String ipAddress_m, int port_m, String myPhoneNo_m) {

        socket = null;
        ipAddress = ipAddress_m;
        port = port_m;
        myPhoneNo = myPhoneNo_m;

        try {
            socket = new Socket(ipAddress, port);
        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
        gson = new Gson();
    }

    public boolean sendAuth() {
        Request request = new Request();
        request.setAction("Connect");
        request.setData("Let me innnn!!!");
        request.setSenderId(myPhoneNo);

        return sendRequest(request);
    }

    public boolean sendMessage(String receiver, String message) {
        Request request = new Request();
        request.setAction("Message");
        request.setData(message);
        request.setSenderId(myPhoneNo);
        request.setRecieverId(receiver);

        return sendRequest(request);
    }

    public boolean sendNewChat(String phoneNo) {
        Request request = new Request();
        request.setAction("New Chat");
        request.setData(phoneNo + " se baat karni hai mujhe");
        request.setSenderId(myPhoneNo);
        return sendRequest(request);
    }

    private boolean sendRequest(Request request) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(gson.toJson(request));
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void finalize() {  
        try{
            socket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }

}
