package com.example.wa_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {

    private final String serverId = "SERVER";
    SharedPreferences sharedPref;
    BackgroundService mService;
    String clientId;
    String clientName;
    String token;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("apps", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId","");
        clientName = sharedPref.getString("clientName","");
        token = sharedPref.getString("token", "NULL");
//        Log.d("k", "onCreate: "+clientId);

        // This is the first Activity, so initializations will happen here
//        initializations();
        serviceIntent = new Intent(this,BackgroundService.class);
        if(!isMyServiceRunning()) {
            startService(serviceIntent);
            Log.d("waclonedebug", "service started");
        }
//        Intent intent1 = new Intent(this, BackgroundService.class);
//        startService(intent1);

        Intent intent;
        if(clientId == "") {
            Toast.makeText(getApplicationContext(),"First Time",Toast.LENGTH_SHORT).show();
            intent = new Intent(this, RegisterActivity.class);
        }
        else{

            SendRequestTask.setToken(token);
            Log.d("waclonedebug", "auth sent "+token);
            intent = new Intent(this,MainActivity.class);

            intent.putExtra("clientId",clientId);
            intent.putExtra("clientName",clientName);
        }
        Log.d("waclonedebug", "mainactivity intent");
        startActivity(intent);
        finish();

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

//    private void initializations(){
////        Log.d("waclonedebug", "In initializations");
////        globalVariables.sendMessageService = Executors.newSingleThreadExecutor();
////        globalVariables.processResponseService = Executors.newSingleThreadExecutor();
////        globalVariables.sharedPref = sharedPref;
////
////        ReceivingThread receivingThread = new ReceivingThread(globalVariables);
////        receivingThread.start();
//
//
//
//    }

}