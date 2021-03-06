package com.example.mysocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class JavaSocketClientActivity extends AppCompatActivity {

    //IP地址和端口号
    public static String IP_ADDRESS = "";
    private  final String TAG = "JavaSocketClientActivity";
    public static int PORT = 2022;
    //三个控件
    EditText et_message = null; //需要发送的内容
    Button bt_getAdress = null; //获取本机IP地址
    Button bt_connect = null; //连接并发送
    Button bt_startServer = null; //启动服务端
    TextView tv_adress = null; //ip地址
    TextView tv_reply = null; //服务器回复的消息
    //handler
    Handler handler = null;
    Socket soc = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "->>>onCreate");
        initView();
    }

    @SuppressLint("LongLogTag")
    private void initView() {
        et_message = findViewById(R.id.et_message);
        bt_getAdress = findViewById(R.id.bt_getAdress);
        bt_connect = findViewById(R.id.bt_connect);
        bt_startServer = findViewById(R.id.bt_startServer);

        tv_adress = findViewById(R.id.tv_adress);
        tv_reply = findViewById(R.id.tv_reply);
        bt_getAdress.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    Log.d(TAG,"->>>local host:"+addr);
                    runOnUiThread(() -> tv_adress.setText(addr.toString().split("/")[1]));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        bt_startServer.setOnClickListener(v -> {
            new Thread(() -> new JavaSocketServer().startService()).start();
            Toast.makeText(JavaSocketClientActivity.this,"服务已启动",Toast.LENGTH_SHORT).show();
        });
        bt_connect.setOnClickListener(v -> {
            IP_ADDRESS = tv_adress.getText().toString();
            new ConnectionThread(et_message.getText().toString()).start();
        });
        handler = new Handler(msg -> {
            Bundle b = msg.getData(); //获取消息中的Bundle对象
            String str = b.getString("data"); //获取键为data的字符串的值
            tv_reply.append(str);
            return false;
        });
    }

    //新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {
        String message = null;

        @SuppressLint("LongLogTag")
        public ConnectionThread(String msg) {
            Log.d(TAG, "->>>ConnectionThread|msg = " + msg);
            message = msg;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            if (soc == null) {
                try {
                    Log.d(TAG,"->>>new socket");
                    if ("".equals(IP_ADDRESS)) {
                        return;
                    }
                    InetAddress address = InetAddress.getByName("www.baidu.com");
                    String name = address.getHostName();
                    // public String getHostAddress()
                    String ip = address.getHostAddress();
                    Log.d(TAG, name + "---" + ip);
                    soc = new Socket(IP_ADDRESS, PORT);
                    //获取socket的输入输出流
                    dis = new DataInputStream(soc.getInputStream());
                    dos = new DataOutputStream(soc.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                dos.writeUTF(message);
                dos.flush();
                messageRecv = dis.readUTF();//如果没有收到数据，会阻塞
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("data", messageRecv);
                msg.setData(b);
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}