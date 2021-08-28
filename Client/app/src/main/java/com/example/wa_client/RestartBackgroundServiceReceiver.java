package com.example.wa_client;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class RestartBackgroundServiceReceiver extends BroadcastReceiver {
    JobScheduler jobScheduler;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("waclondebug", "onReceive: RestartBroadcast");
        Intent mIntent = new Intent(context,BackgroundServiceNew.class);
        BackgroundServiceNew.enqueueWork(context,BackgroundServiceNew.class,1213,mIntent);
        Log.d("waclondebug", "onReceive: RestartBroadcast1");

//        scheduleJob(context);
    }

    public void scheduleJob(Context context) {
        Log.d("waclonedebug", "scheduleJob: RestartBroadcast1");

        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        Log.d("waclonedebug", "scheduleJob: RestartBroadcast2");

        ComponentName componentName = new ComponentName(context,
                JobService.class);
        Log.d("waclonedebug", "scheduleJob: RestartBroadcast3");
        JobInfo jobInfo = new JobInfo.Builder(1, componentName).setOverrideDeadline(0).setPersisted(true).build();
        Log.d("waclonedebug", "scheduleJob: RestartBroadcast4");
        jobScheduler.schedule(jobInfo);
        Log.d("waclonedebug", "scheduleJob: RestartBroadcast5");

    }
}