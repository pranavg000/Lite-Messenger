package com.example.wa_client;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class GlobalVariables extends Application {
    public ExecutorService sendMessageService, processResponseService;
    public MainActivity mainActivity;
    public final String serverId = "SERVER";
    public String clientId;
    public SharedPreferences sharedPref;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String test;
    private HashMap<String, String> newChatReqId = new HashMap<>();

    public void addNewChatToMap(String requestId, String newUserId){
        newChatReqId.put(requestId, newUserId);
    }
    public String getNewChatFromMap(String requestId){
        return newChatReqId.get(requestId);
    }
    public String removeNewChatFromMap(String requestId){
        String chatId = newChatReqId.get(requestId);
        newChatReqId.remove(requestId);
        return chatId;
    }

}
