package com.example.wa_client;

public class Contact {
    private String clientName;
    private String clientId;
    private String lastSeenTime;
    private String displayMessage;
    private int numberUnseenMessages;

    public int getNumberUnseenMessages() {
        return numberUnseenMessages;
    }

    public void setNumberUnseenMessages(int numberUnseenMessages) {
        this.numberUnseenMessages = numberUnseenMessages;
    }

    public void incrementUnseenMessages() {
        numberUnseenMessages++;
    }

    public Contact(String clientName, String clientId) {
        this.clientName = clientName;
        this.clientId = clientId;
        this.lastSeenTime = "6PM";
        this.displayMessage = "";
        this.numberUnseenMessages = 0;
    }
    public Contact(String clientName, String clientId, String lastSeenTime, String displayMessage) {
        this.clientName = clientName;
        this.clientId = clientId;
        this.lastSeenTime = lastSeenTime;
        this.displayMessage = displayMessage;
        this.numberUnseenMessages = 0;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(String lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
}
