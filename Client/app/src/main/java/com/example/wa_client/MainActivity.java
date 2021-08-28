package com.example.wa_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ContactAdapter adapter;
    public ArrayList<Contact> contacts;
    public HashMap<String, Integer> clientIdToContacts;
    public HashMap<String, Contact> tempClientIdToContacts;
    public HashMap<String, ArrayList<Message>> clientIdToMessages;
    public HashMap<String, MessageListAdapter> clientIdToMessageListAdapter;
    public String currentClientId;
    public String currentClientName;
    private RecyclerView recyclerView;
    public boolean isProcessing;

  public SharedPreferences sharedPreferences;
    BackgroundService mService;
    ServiceConnection  connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };
    Intent serviceIntent;
  
    public String currentChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentClientId = getIntent().getStringExtra("clientId");
        currentClientName = getIntent().getStringExtra("clientName");
        contacts = new ArrayList<>();
        clientIdToMessages = new HashMap<>();
        tempClientIdToContacts = new HashMap<>();
        clientIdToContacts = new HashMap<>();
        clientIdToMessageListAdapter = new HashMap<>();
        adapter = new ContactAdapter(this,contacts);
//        addNewContact(new Contact("Test0","123"));
        Toast toast = Toast.makeText(getApplicationContext(),"Start",Toast.LENGTH_SHORT);
        toast.show();
        sharedPreferences = getSharedPreferences("apps", Context.MODE_PRIVATE);

        serviceIntent = new Intent(this, BackgroundService.class);

//        while(!BackgroundService.isReady()){
////                Log.d("waclonedebug", "Creating Socket...");
//        }
////        if(!isMyServiceRunning()) {
//            startService(serviceIntent);
//        }

        Log.d("waclone", "mainActivity onCreate end");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, connection, Context.BIND_IMPORTANT);
        BackgroundService.mainActivity = this;

        Log.d("waclonedebug", "mainActivity onResume End");
//        while (mService==null)
//        {
//            ;
//        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
    }

//    private boolean isMyServiceRunning() {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
//                Log.i ("isMyServiceRunning?", true+"");
//                return true;
//            }
//        }
//        Log.i ("isMyServiceRunning?", false+"");
//        return false;
//    }

    public void setRecyclerviewChatList(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        recyclerView.setAdapter(adapter);
    }

    public void setRecyclerViewChatFragment(RecyclerView recyclerView, String clientId){
        recyclerView.setAdapter(clientIdToMessageListAdapter.get(clientId));
    }

    public void addNewContact(Contact newContact){
//        newContact.setClientName(newContact.getClientName());
//        Log.d("waclonedebug", "addNewContact: start"+newContact.getClientName());
//        contacts.add(newContact);
        clientIdToContacts.put(newContact.getClientId(),contacts.size());
        ArrayList<Message> messageList = new ArrayList<>();
        clientIdToMessages.put(newContact.getClientId(),messageList);
        clientIdToMessageListAdapter.put(newContact.getClientId(),new MessageListAdapter(currentClientId,messageList));
//        Log.d("waclonedebug", "addNewContact: mid1"+newContact.getClientName());
        isProcessing = true;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addContact(newContact);
                if(recyclerView != null)
                    recyclerView.scrollToPosition(contacts.size()-1);
                isProcessing = false;
            }
        });
        while (isProcessing);
//        Log.d("waclonedebug", "addNewContact: end"+newContact.getClientName());
    }

    public void addNewChatMessage(Message message, String clientId){
//        Log.d("waclonedebug", "addNewChatMessage: start");
        ArrayList<Message> messageList = clientIdToMessages.get(clientId);
        MessageListAdapter messageListAdapter = clientIdToMessageListAdapter.get(clientId);
        int pos = clientIdToContacts.get(clientId);
        Contact contact = contacts.get(pos);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("waclonedebug", "Current Chat: " + currentChat + " " + clientId);
                if(message.getSenderId() == clientId){
                    if(!clientId.equals(currentChat)){
                        contact.incrementUnseenMessages();
                    }
                    else{
                        message.setMessageRead(true);
                        mService.sendMessageService.submit(new SendRequestTask(Request.RequestType.MessageRead, clientId, String.valueOf(message.getTimeStamp()), currentClientId));
                    }
                }

                messageList.add(message);
                contact.setDisplayMessage(message.getData());
                adapter.notifyItemChanged(pos);
                messageListAdapter.notifyItemInserted(messageList.size()-1);
                messageListAdapter.scrollRecyclerView();
            }
        });

//        Log.d("waclonedebug", "addNewChatMessage: end");
    }

//    public void receivedReceiveReceipt(String reqId, String clientId, long receiveTimeStamp){
////        Contact contact = getContact(clientId);
//        ArrayList<Message> messageList = clientIdToMessages.get(clientId);
//        MessageListAdapter messageListAdapter = clientIdToMessageListAdapter.get(clientId);
//        for(int i=messageList.size()-1; i>=0; i--){
//            Message message = messageList.get(i);
//            if(message.getMessageID() == reqId) {
//                final int pos = i;
//                this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        message.setReceiveTimeStamp(receiveTimeStamp);
//                        messageListAdapter.notifyItemChanged(pos);
//                    }
//                });
//                return;
//            }
//        }
//
//    }

    public void sendMessage(String receiverId, String data, RecyclerView recyclerView){
        Log.d("waclonedebug", "sendMessage: "+receiverId);
        Message message = new Message(data,System.currentTimeMillis(),currentClientId,currentClientName);
        addNewChatMessage(message,receiverId);
        //Add sending logic
        mService.sendMessageService.submit(new SendRequestTask(Request.RequestType.Message, receiverId, data, currentClientId));
    }

    public void addTempContact(String clientId, String clientName) {
        tempClientIdToContacts.put(clientId, new Contact(clientName, clientId));
    }

    public void sendNewChatRequest(String newChatClientId) {
        mService.sendMessageService.submit(new SendRequestTask(Request.RequestType.NewChat, newChatClientId, "", currentClientId));

    }

    public void removeTempContact(String clientId) {
        tempClientIdToContacts.remove(clientId);
    }

    public Contact getTempContact(String clientId) {
        return tempClientIdToContacts.get(clientId);
    }

    public Contact getContact(String clientId) {
        return contacts.get(clientIdToContacts.get(clientId));
    }

    public void readReceiptReceived(String clientId, long sendersTimestamp, long myTimestamp) {
        ArrayList<Message> messageList = clientIdToMessages.get(clientId);
        MessageListAdapter messageListAdapter = clientIdToMessageListAdapter.get(clientId);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=messageList.size()-1;i>=0;i--){
                    Message message = messageList.get(i);
                    if(message.getReadTimeStamp() > 0) break;
                    if(message.getSenderId().equals(currentClientId) && message.getTimeStamp() <= myTimestamp){
                        message.setReadTimeStamp(sendersTimestamp);
                        Log.d("waclonedebug", "Read RCPT: " + message.getData() + " " + sendersTimestamp);
                        messageListAdapter.notifyItemChanged(i);
                    }
                }
            }
        });

    }

    public void receiveReceiptReceived(String clientId, long sendersTimestamp, long myTimestamp) {
        ArrayList<Message> messageList = clientIdToMessages.get(clientId);
        MessageListAdapter messageListAdapter = clientIdToMessageListAdapter.get(clientId);
        Log.d("waclonedebug", "OL "+String.valueOf(sendersTimestamp) + " " + String.valueOf(myTimestamp));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=messageList.size()-1;i>=0;i--){
                    Message message = messageList.get(i);
                    Log.d("waclonedebug", "IL "+String.valueOf(message.getReceiveTimeStamp()) + " " + String.valueOf(message.getTimeStamp()));
                    if(message.getReceiveTimeStamp() > 0) break;
                    if(message.getSenderId().equals(currentClientId) && message.getTimeStamp() <= myTimestamp){
                        message.setReceiveTimeStamp(sendersTimestamp);
                        Log.d("waclonedebug", "Receive RCPT: " + message.getData() + " " + sendersTimestamp);
                        messageListAdapter.notifyItemChanged(i);
                    }
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}