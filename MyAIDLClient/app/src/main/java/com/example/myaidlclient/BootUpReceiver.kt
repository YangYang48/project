package com.example.myaidlclient

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class BootUpReceiver : BroadcastReceiver(){
    companion object {
        private const val TAG = "BootUpReceiver"
        private const val BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"
        private const val AIDL_ACTION = "com.example.action.MYAIDL_CLIENT"
        const val ALARM_ACTION = "com.example.action.ALARM_ACTION"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "->>>onReceive")
        if (BOOT_ACTION.equals(intent?.action))
        {
            Log.d(TAG, "->>>action is BOOT_ACTION")
            var intent : Intent = Intent(context, BootService::class.java)
            //Android8不允许创建后台服务的情况下尝试使用该方法startService
            context?.startForegroundService(intent)
        } else if (ALARM_ACTION.equals(intent?.action))
        {
            Log.d(TAG, "->>>action is ALARM_ACTION")
            var intent : Intent = Intent(context, BootService::class.java)
            context?.startForegroundService(intent)
        } else if (AIDL_ACTION.equals(intent?.action)) {
            Log.d(TAG, "->>>action is AIDL_ACTION")
            var intent : Intent = Intent(context, BootService::class.java)
            context?.startForegroundService(intent)
        } else
        {
            Log.d(TAG, "->>>other action, stop service")
            var intent : Intent = Intent(context, BootService::class.java)
            context?.stopService(intent)
        }
    }
}