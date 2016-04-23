package com.example.smartsend.smartsendapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by AGM TAZIM on 1/6/2016.
 */
public class GCMReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName cmp = new ComponentName(context.getPackageName(), GCMMessageHandler.class.getName());
        startWakefulService(context, (intent.setComponent(cmp)));
        setResultCode(Activity.RESULT_OK);
    }
}
