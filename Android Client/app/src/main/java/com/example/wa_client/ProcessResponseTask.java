package com.example.wa_client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;

import static java.lang.Math.min;

public class ProcessResponseTask implements Runnable {

    private Request request;
//    private static String signUpRId;
//    private static String authRId;
    private MainActivity mainActivity;
    private BackgroundService backgroundService;
    private SharedPreferences sharedPreferences;
    private static boolean authenticated = false;
    private Context context;


    public ProcessResponseTask(Request request,BackgroundService backgroundService) {
        this.request = request;
        this.backgroundService = backgroundService;
        this.mainActivity = backgroundService.mainActivity;
        this.sharedPreferences = backgroundService.getSharedPreferences("apps",Context.MODE_PRIVATE);
    }

    public void run() {
        Request.RequestType action = request.getAction();
        if(action == Request.RequestType.SignUpSuccessful){
            String token = request.getToken();

            // Store token on disk
            if(sharedPreferences!=null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.commit();
            }

            SendRequestTask.setToken(token);

            authenticated=true;
        }
        else if(action == Request.RequestType.AuthSuccessful){
            authenticated=true;
        }
//        else if(!authenticated) {
//            Log.d("waclonedebug", request.toString());
//            Log.e("waclonedebug", "Auth not done. Can not do anything else");
//            return;
//        }
        else if(action == Request.RequestType.NewChatPositive){
            Log.v("waclonedebug", "NewChat with " + request.getData());
            String newChatReqId = request.getData();
            if(mainActivity!=null)
                mainActivity.addNewContact(mainActivity.getTempContact(newChatReqId));
        }
        else if(action == Request.RequestType.UserNotFound){
            String newChatReqId = request.getData();
            if(mainActivity!=null)
                mainActivity.removeTempContact(newChatReqId);
        }
        else if(action == Request.RequestType.Message){
            String senderId = request.getSenderId();
            Log.d("waclonedebug", "sid "+senderId);

            if(mainActivity==null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String messagePreview = request.getData();
                    messagePreview = messagePreview.substring(0,min(20,messagePreview.length()));
                    backgroundService.showNotificationNewMessage(senderId,messagePreview);
                }
                return;
            }
            Contact contact;
            if(mainActivity.clientIdToContacts.get(senderId) == null) {
                contact = new Contact(senderId,senderId);
//                globalVariables.mainActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                mainActivity.addNewContact(contact);
//                    }
//                });
                Log.d("waclonedebug", contact.getClientName());
            }

            else {
//                Log.d("waclonedebug", "existing client"+MainActivity.clientIdToContacts.get(senderId));
                contact = mainActivity.contacts.get(mainActivity.clientIdToContacts.get(senderId));
                Log.d("waclonedebug", "existing client found");
            }
            Message message = new Message(request.getData(), request.getTimeStamp(), senderId, contact.getClientName());

            Log.d("waclonedebug", message.getData());
            mainActivity.addNewChatMessage(message, senderId);
        }
        else if(action == Request.RequestType.MessageReceived){
            Log.d("waclonedebug", "Message " + request.getData() + " RECEIVED by receiver");
            mainActivity.receiveReceiptReceived(request.getSenderId(), request.getTimeStamp(), Long.parseLong(request.getData()));
        }
        else if(action == Request.RequestType.MessageRead){
            Log.d("waclonedebug", "Message " + request.getData() + " READ by receiver");
            mainActivity.readReceiptReceived(request.getSenderId(), request.getTimeStamp(), Long.parseLong(request.getData()));
        }
        
    }

}
