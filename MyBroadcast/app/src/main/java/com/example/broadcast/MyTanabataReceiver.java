package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyTanabataReceiver extends BroadcastReceiver {
    private final String TAG = "MyTanabataReceiver";
    public final static String ACTION_SENDLOVE = "android.intent.action.SENDLOVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_SENDLOVE.equals(intent.getAction())) {
            //adb 发广播
            //adb shell am broadcast -a android.intent.action.SENDLOVE -n com.example.broadcast/.MyTanabataReceiver
            // --es "love" "爱你" --ei "days" 10000
            String myloveString = intent.getStringExtra("love");
            int myloveInt = intent.getIntExtra("days", -1);
            Log.d(TAG, "->myloveString = " + myloveString + " myloveInt = " + myloveInt);
            Toast.makeText(context, "播放黄老板的【perfect】\n\t" + myloveString + myloveInt + "年", Toast.LENGTH_LONG).show();
        }
    }
}
