package com.example.wa_client;

public class Message {
    private String messageID;
    private String data;
    private long timeStamp;
    private String senderId;
    private String senderName;
    private long receiveTimeStamp;
    private long readTimeStamp;
    private boolean messageRead;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getReceiveTimeStamp() {
        return receiveTimeStamp;
    }

    public void setReceiveTimeStamp(long receiveTimeStamp) {
        this.receiveTimeStamp = receiveTimeStamp;
    }

    public long getReadTimeStamp() {
        return readTimeStamp;
    }

    public void setReadTimeStamp(long readTimeStamp) {
        this.readTimeStamp = readTimeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    public Message(String data, long timeStamp, String senderId, String senderName) {
        this.data = data;
        this.timeStamp = timeStamp;
        this.senderId = senderId;
        this.senderName = senderName;
        this.messageRead = false;
        this.readTimeStamp = 0;
        this.receiveTimeStamp = 0;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
