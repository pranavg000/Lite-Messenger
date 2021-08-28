package com.example.wa_client;
import java.util.UUID;

public class Request {
    private String requestId;
    private RequestType action;
    private String senderId;
    private String receiverId;
    private String data;
    private String token;
    private long timeStamp;

    public static enum RequestType {
        Auth, NewChat, Message, SignUp, Disconnect, POSITIVE, ERROR, InvalidToken, UserNotFound, MessageReceived, SignUpSuccessful, AuthSuccessful, NewChatPositive, MessageRead
    }

    Request(RequestType action, String senderId, String receiverId, String data, String token) {
        this.requestId = UUID.randomUUID().toString();
        this.action = action;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.data = data;
        this.token = token;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String t) {
        token = t;
    }

    public RequestType getAction() {
        return action;
    }

    public void setAction(RequestType action) {
        this.action = action;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "Request [requestId=" + requestId + ", action=" + action.name() + ", data=" + data + ", receiverId=" + receiverId + ", senderId=" + senderId
                + ", token=" + token + ", timeStamp=" + String.valueOf(timeStamp) + "]";
    }
}

