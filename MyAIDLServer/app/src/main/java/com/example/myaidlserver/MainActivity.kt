package com.example.myaidlserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() , IDateInterface{

    companion object {
        private const val TAG = "MainActivity"
        private const val MESSAGE_INIT = 1001
        private const val MESSAGE_UPDATE = 1002
    }

    private var SeekBarFlag = false
    private var sb: SeekBar? = null
    private var tv:TextView? = null
    private var textString:String? = null

    private var mHandler: Handler = Handler(Looper.getMainLooper()){
        when(it?.what)
        {
            MESSAGE_INIT->{
                if (!SeekBarFlag) return@Handler true
                sb?.visibility = View.VISIBLE
                textString = textString + "\n\t" + "message init successful"
                tv?.text = textString
            }
            MESSAGE_UPDATE ->{
                if (!SeekBarFlag) return@Handler true
                var pro = it?.obj as Int
                Log.d(TAG, "->>>mHandler | pro = $pro")
                sb?.progress = pro
                if (pro % 5 == 0)
                {
                    textString = textString + "\n\t" + "data update progress is $pro"
                    tv?.text = textString
                }
            }
            else ->{
                Log.d(TAG, "->>>failed, handler excepted message")
            }

        }
        return@Handler true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //启动aidlservice
        Log.d(Companion.TAG, "->>>myaidlserver oncreate")
        var intent: Intent = Intent(this, DataUpdateService::class.java)
        startService(intent)

        //set interface
        DataUpdateService.iDateInterface = this

        //tv init
        textString = "myaidlserver oncreate"
        tv = findViewById(R.id.tv)
        tv?.text = textString
        //startinit seekbar
        sb = findViewById(R.id.sb)
        SeekBarFlag = null != sb
        sb?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                Log.d(TAG,"->>>listener seekBar,progress = $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        sb?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "->>>myaidlserver ondestory")
        //remove interface
        DataUpdateService.iDateInterface = null
    }

    override fun StartUp(): Int {
        Log.d(TAG, "->>>interface|startup")
        mHandler.sendEmptyMessage(MESSAGE_INIT)
        return if (SeekBarFlag) 1 else 0
    }

    override fun DialogUpdate(progress: Int) {
        Log.d(TAG, "->>>interface|update progress = $progress")
        var message: Message = Message.obtain(mHandler, MESSAGE_UPDATE, progress)
        mHandler.sendMessage(message)
    }
}