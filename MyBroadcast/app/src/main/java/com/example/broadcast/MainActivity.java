package com.example.broadcast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * as版本4.2.1
 * gradle版本 6.7.1
 * compileSdkVersion 30
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "MainActivity";
    private final String MY_DYNAMIC_BROADCAST = "android.intent.action.dynamicBroadcast";
    private final String MY_ORDERED_BROADCAST = "android.intent.action.orderedBroadcast";
    private Button btDynamic,btNomal,btOrdered;
    private TextView tv;
    private Intent intent;
    private MyTanabataReceiver myTanabataReceiver;
    private OrderedReceiver1 orderedReceiver1;
    private OrderedReceiver2 orderedReceiver2;
    private OrderedReceiver3 orderedReceiver3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initBroadcast();
    }

    private void initView() {
        Log.d(TAG, "->initView");
        btDynamic = findViewById(R.id.bt1);
        btNomal = findViewById(R.id.bt2);
        btOrdered = findViewById(R.id.bt4);
        btDynamic.setOnClickListener(this);
        btNomal.setOnClickListener(this);
        btOrdered.setOnClickListener(this);
        tv = findViewById(R.id.tv1);

    }

    private void initBroadcast()
    {
        Log.d(TAG ,"->initBroadcast");
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MY_DYNAMIC_BROADCAST);
        registerReceiver(dynamicBroadcast, itFilter);

        //注册标准广播--七夕
        myTanabataReceiver = new MyTanabataReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyTanabataReceiver.ACTION_SENDLOVE);
        registerReceiver(myTanabataReceiver, intentFilter);

        //动态注册有序广播
        //有序广播需要设置优先级-1000-1000，值越大优先级越高
        orderedReceiver1 = new OrderedReceiver1();
        IntentFilter orderedfilter1 = new IntentFilter();
        orderedfilter1.addAction(MY_ORDERED_BROADCAST);
        orderedfilter1.setPriority(1000);
        this.registerReceiver(orderedReceiver1, orderedfilter1);

        orderedReceiver2 = new OrderedReceiver2();
        IntentFilter orderedfilter2 = new IntentFilter();
        orderedfilter2.addAction(MY_ORDERED_BROADCAST);
        orderedfilter2.setPriority(999);
        this.registerReceiver(orderedReceiver2, orderedfilter2);

        orderedReceiver3 = new OrderedReceiver3();
        IntentFilter orderedfilter3 = new IntentFilter();
        orderedfilter3.addAction(MY_ORDERED_BROADCAST);
        orderedfilter3.setPriority(998);
        this.registerReceiver(orderedReceiver3, orderedfilter3);
    }

    //别忘了将广播取消掉哦~
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dynamicBroadcast);
        unregisterReceiver(myTanabataReceiver);
        unregisterReceiver(orderedReceiver1);
        unregisterReceiver(orderedReceiver2);
        unregisterReceiver(orderedReceiver3);
    }

    /**
     * 匿名内部类
     * adb发送给广播
     * adb shell am broadcast -a android.intent.action.dynamicBroadcast --es "myString" "dynamicBroadcast successed"
     */
    private BroadcastReceiver dynamicBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MY_DYNAMIC_BROADCAST.equals(intent.getAction()))
            {
                String myString = intent.getStringExtra("myString");
                Log.d(TAG, "->dynamicBroadcast receiver has receive, mystring is "+myString);
                Toast.makeText(context, "->dynamicBroadcast receiver has receive, mystring is "+myString,
                        Toast.LENGTH_LONG).show();
            }
        }
    };


    /**
     * 有序广播接收1，优先级1000
     */
    public class OrderedReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MY_ORDERED_BROADCAST.equals(intent.getAction()))
            {
                boolean orderedBroadcast = isOrderedBroadcast();
                if (orderedBroadcast) {
                    Log.d(TAG, "->Priority 1000 is orderedBroadcast");
                } else {
                    Log.d(TAG, "->Priority 1000 is not orderedBroadcast");
                    abortBroadcast();
                }
                String resultData = getResultData();
                if (null != resultData)
                {
                    Log.d(TAG, "->OrderedReceiver1|resultData = "+resultData);
                    //有序广播里终止广播
                    //abortBroadcast();
                    setResultData("有序广播1收到，传达给有序广播2");
                    Toast.makeText(context, "有序广播接收1的内容："+resultData, Toast.LENGTH_SHORT).show();
                }else
                {
                    Log.d(TAG, "->OrderedReceiver1");
                    //有序广播里终止广播
                    //abortBroadcast();
                    setResultData("有序广播1收到，传达给有序广播2");
                    Toast.makeText(context, "有序广播接收1的内容：", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /**
     * 有序广播接收2，优先级999
     */
    public class OrderedReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MY_ORDERED_BROADCAST.equals(intent.getAction()))
            {
                boolean orderedBroadcast = isOrderedBroadcast();
                if (orderedBroadcast) {
                    Log.d(TAG, "->Priority 999 is orderedBroadcast");
                } else {
                    Log.d(TAG, "->Priority 999 is not orderedBroadcast");
                    abortBroadcast();
                }
                String resultData = getResultData();
                Log.d(TAG, "->OrderedReceiver2|resultData = "+resultData);
                //有序广播里终止广播
                //abortBroadcast();
                setResultData("有序广播2收到，传达给有序广播3");
                Toast.makeText(context, "有序广播接收2的内容："+resultData, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 有序广播接收3，优先级998
     */
    public class OrderedReceiver3 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MY_ORDERED_BROADCAST.equals(intent.getAction()))
            {
                boolean orderedBroadcast = isOrderedBroadcast();
                if (orderedBroadcast) {
                    Log.d(TAG, "->Priority 998 is orderedBroadcast");
                } else {
                    Log.d(TAG, "->Priority 998 is not orderedBroadcast");
                    abortBroadcast();
                }
                String resultData = getResultData();
                Log.d(TAG, "->OrderedReceiver3|resultData = "+resultData);
                //有序广播里终止广播
                //abortBroadcast();
                setResultData("有序广播3收到，继续传递");
                Toast.makeText(context, "有序广播接收3的内容："+resultData, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt1:
                Log.d(TAG, "->onclick btDynamic");
                intent = new Intent(MY_DYNAMIC_BROADCAST);
                intent.putExtra("myString", "动态广播");
                sendBroadcast(intent);
                if (null != tv)
                {
                    tv.setText("动态广播用例");
                }
                break;
            case R.id.bt2:
                Log.d(TAG, "->onclick btNomal");
                intent = new Intent(MyTanabataReceiver.ACTION_SENDLOVE);
                intent.putExtra("love", "send 爱你");
                intent.putExtra("days", 10000);
                sendBroadcast(intent);
                if (null != tv)
                {
                    tv.setText("标准广播用例");
                }
                break;
            case R.id.bt4:
                Log.d(TAG, "->onclick btOrdered");
                intent = new Intent(MY_ORDERED_BROADCAST);
                sendOrderedBroadcast(intent, null, new OrderedReceiver3(), null,
                        Activity.RESULT_OK, "现在发送有序广播给有序广播1", null);
                if (null != tv)
                {
                    tv.setText("有序广播用例");
                }
                break;
            default:
                break;
        }
    }
}