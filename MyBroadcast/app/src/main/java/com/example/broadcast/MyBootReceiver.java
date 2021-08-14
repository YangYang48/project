package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBootReceiver extends BroadcastReceiver {
    private final String TAG = "MyBootReceiver";
    public final static String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction()))
        {
            Log.d(TAG, "->boot receiver get"+Intent.ACTION_BOOT_COMPLETED);
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        }
    }
}
