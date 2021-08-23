package com.example.myrecyclerview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button


/**
 * as版本4.2.1
 * gradle版本 6.7.1
 * compileSdkVersion 30
 */
class MainActivity : AppCompatActivity() , View.OnClickListener{
    companion object {
        private const val TAG = "MainActivity"
    }

    private var bt1: Button? = null
    private var bt2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        bt1 = findViewById(R.id.btSimpleRecycerView)
        bt2 = findViewById(R.id.btWifisettings)
        bt1?.setOnClickListener(this)
        bt2?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when( v?.id)
        {
            R.id.btSimpleRecycerView ->
            {
                Log.d(TAG, "->onclick|btSimpleRecycerView")
                startActivity(Intent(this, SimpleRecyclerView::class.java))
            }

            R.id.btWifisettings ->
            {
                Log.d(TAG, "->onclick|btWifisettings")
                startActivity(Intent(this, WifiSettings::class.java))
            }

        }
    }




}