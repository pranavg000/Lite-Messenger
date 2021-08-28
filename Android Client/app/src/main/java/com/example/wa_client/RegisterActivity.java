package com.example.wa_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private final String serverId = "SERVER";
    private Button registerButton;
    private EditText clientIde;
    private EditText clientNamee;
    BackgroundService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        clientIde = findViewById(R.id.clientIdInput);
        clientNamee = findViewById(R.id.clientNameInput);
        registerButton = findViewById(R.id.register);
        Intent intent = new Intent(this,MainActivity.class);
        Intent serviceIntent = new Intent(this,BackgroundService.class);
        ServiceConnection connection = new ServiceConnection() {

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
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("waclonedebug", "First");
                Log.d("waclonedebug", "Second");
                String clientId = clientIde.getText().toString();
                String clientName = clientNamee.getText().toString();

                SharedPreferences sharedPref = getSharedPreferences("apps",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("clientId",clientId);
                editor.putString("clientName",clientName);
                editor.commit();

                Log.d("waclonedebug", "Other 2 committed");


                // Send SignUp Request
                mService.clientId = clientId;
                if(mService.sendMessageService == null) Log.d("waclonedebug", "Problem");
                if(mService.processResponseService == null) Log.d("waclonedebug", "Problem pms");
                mService.sendMessageService.submit(new SendRequestTask(Request.RequestType.SignUp,  serverId, "",clientId));
                Log.d("waclonedebug", "Submitted");
                intent.putExtra("clientId",clientId);
                intent.putExtra("clientName",clientName);
                unbindService(connection);
                startActivity(intent);
                finish();
            }
        });

    }
}