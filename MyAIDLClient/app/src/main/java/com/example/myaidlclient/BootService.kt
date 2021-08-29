package com.example.myaidlclient

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.aidl.IDataUpdate


/**
 * as版本4.2.1
 * gradle版本 6.7.1
 * compileSdkVersion 30
 * 这个app的作用是只有service来作为客户端发送数据，在另一个app作为服务端实现
 * 这个用途实现后台数据放到前台app的数据加载功能
 */
class BootService : Service() {
    companion object {
        private const val TAG = "BootService"
        private const val INTERVAL = 1000 * 10L //10s定时

    }
    private var mContext: Context? = null
    private var mAidlClient: IDataUpdate? = null

    override fun onBind(intent: Intent): IBinder? {
       return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "->>>MyAIDLClient onCreate")
        mContext = applicationContext

        //及时调用startForeground，对于O以后的还要注意Notification需要一个ChannelID
        //可以通过adb shell am start-foreground-service -n com.example.myaidlclient/.BootService
        // startservice自启动
        try {
            val CHANNEL_ONE_ID = "com.example.myaidlclient"
            val CHANNEL_ONE_NAME = "Channel One"
            var notificationChannel: NotificationChannel? = null
            notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)!!
            manager!!.createNotificationChannel(notificationChannel)
            startForeground(1, NotificationCompat.Builder(this, CHANNEL_ONE_ID).build())
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }

        //开启定时器
        startRecycler()
        //绑定aidl
        bindAidl()
    }

    private fun startRecycler() {
        Log.d(TAG, "->>>startRecycler")
        var clockIntent: Intent = Intent(mContext, BootUpReceiver::class.java)
        clockIntent.setAction(BootUpReceiver.ALARM_ACTION)

        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(mContext, 0, clockIntent, 0)
        val nextknock: Long = System.currentTimeMillis() + INTERVAL
        alarmManager.setRepeating(AlarmManager.RTC, nextknock, INTERVAL, pendingIntent)
    }

    private fun bindAidl() {
        Log.d(TAG, "->>>bind aidl")
        var aidlIntent: Intent = Intent()
        aidlIntent.setClassName("com.example.myaidlserver", "com.example.myaidlserver.DataUpdateService")
        mContext?.bindService(aidlIntent, mAidlConnect, Context.BIND_AUTO_CREATE)
    }

    private var mAidlConnect: ServiceConnection = object:ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "->>>mAidlConnect")
            mAidlClient = IDataUpdate.Stub.asInterface(service)
            Log.d(TAG, "->>>init view")
            initView()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "->>>mAidl disConnect")
            mAidlClient = null
        }
    }

    private fun initView() {
        Log.d(TAG, "->>>start initView")
        var ret = mAidlClient?.StartUp()
        Log.d(TAG, "->>>sleep 2s")
        Thread.sleep(2000)
        if (1 == ret)
        {
            for (i in 0  .. 100)
            {
                Log.d(TAG, "->>>send data update , date is $i")
                mAidlClient?.DialogUpdate(i)
                Thread.sleep(200)//控制发送时间间隔200ms一次
            }
        }else
        {
            Log.d(TAG, "->>>init failed")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "->>>MyAIDLClient onDestroy")
    }

}