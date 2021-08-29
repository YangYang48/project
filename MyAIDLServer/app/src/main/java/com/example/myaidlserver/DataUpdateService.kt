package com.example.myaidlserver

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.aidl.IDataUpdate

/*
* service不能直接更新ui
* 借助回调更新ui
* */
class DataUpdateService : Service() {
    companion object {
        private const val TAG = "DataUpdateService"
        private const val MESSAGE_UPDATE = 1001
        var iDateInterface: IDateInterface? = null
    }

    //设置一个标记位判断是否初始化dialog成功，此标记位为二次保护，防止dialog未初始化后继续发送
    private var DialogFlag = false

    private  val dataUpdateBinder: IDataUpdate.Stub  = object : IDataUpdate.Stub(){

        override fun StartUp(): Int {
            Log.d(TAG, "->>>begin to start")
            initView()
            if (DialogFlag)
            {
                Log.d(TAG, "->>>initDialog successful")
                return 1
            }
            return 0
        }

        override fun DialogUpdate(progress: Int) {
            if (!DialogFlag) return
            Log.d(TAG, "->>>data has update to $progress")
            dataUpdate(progress)
        }

    }

    private fun dataUpdate(progress: Int) {
        Log.d(TAG, "->>>dataUpdate|progres = $progress")
        iDateInterface?.DialogUpdate(progress)
    }

    private fun initView(): Int {
        Log.d(TAG, "->>>initDialog")
        DialogFlag = 1 == iDateInterface?.StartUp()
        return 0;
    }

    override fun onBind(intent: Intent): IBinder {
        return dataUpdateBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "->>>DataUpdateService onCreate")
    }
}