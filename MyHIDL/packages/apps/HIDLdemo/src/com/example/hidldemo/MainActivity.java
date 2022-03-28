package com.example.hidldemo;

import android.hardware.yangyang.V1_0.IYangyang;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    IYangyang iYangyangService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            iYangyangService = IYangyang.getService(); //获取服务
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void hidlTest(View view){
        if (iYangyangService != null){
            Log.d("MainActivity", "Yangyang48 | service is connect.");
            String s = null;
            try {
                s = iYangyangService.helloWorld("I am Yangyang48_java");//调用HAL层接口
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d("MainActivity", s);
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }
    }
}
