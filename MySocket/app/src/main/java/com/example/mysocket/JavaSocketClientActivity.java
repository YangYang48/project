package com.example.mysocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
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

    EditText udp_message = null; //需要发送的内容
    Button udp_getAdress = null; //获取本机IP地址
    Button udp_connect = null; //连接并发送
    Button udp_startServer = null; //启动服务端
    TextView udp_adress = null; //ip地址
    TextView udp_reply = null; //服务器回复的消息

    //handler
    Handler handler = null;
    Socket soc = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;

    //broadcast 多播和广播
    private EditText content;
    private static String MULTICAST_IP = "224.0.0.1";
    private static String BROADCAST_IP = "255.255.255.255";
    private static int MULTICAST_PORT = 2047;
    private static int BROADCAST_PORT = 2048;
    private String serverHost = "";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "->>>onCreate");
        initView();
        initBroadcastReceive();
    }

    @SuppressLint("LongLogTag")
    private void initBroadcastReceive() {
        Log.d(TAG, "->>>initBroadcastReceive");
        onBroadcastReceive();
        onMulticastSendReceive();
    }

    @SuppressLint("LongLogTag")
    private void initView() {
        //TCP控件
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

        //UDP控件
        udp_message = findViewById(R.id.udp_message);
        udp_getAdress = findViewById(R.id.udp_getAdress);
        udp_connect = findViewById(R.id.udp_connect);
        udp_startServer = findViewById(R.id.udp_startServer);

        udp_adress = findViewById(R.id.udp_adress);
        udp_reply = findViewById(R.id.udp_reply);
        udp_getAdress.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    Log.d(TAG,"->>>UDP local host:"+addr);
                    runOnUiThread(() -> udp_adress.setText(addr.toString().split("/")[1]));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        udp_startServer.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    new JavaUDPSocketServer().StartService();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            Toast.makeText(JavaSocketClientActivity.this,"UDP服务已启动",Toast.LENGTH_SHORT).show();
        });
        udp_connect.setOnClickListener(v -> {
            IP_ADDRESS = udp_adress.getText().toString();
            //不能放到子线程，android.os.NetworkOnMainThreadException是说不要在主线程中访问网络
            // 这个是android3.0版本开始就强制程序不能在主线程中访问网络，要把访问网络放在独立的线程中。
            new Thread(() -> {
                try {
                    new JavaUDPSocketClient().StartClient();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        handler = new Handler(msg -> {
            Bundle b = msg.getData(); //获取消息中的Bundle对象
            String str = b.getString("data"); //获取键为data的字符串的值
            tv_reply.setText("please input num " + str);
            return false;
        });
        //广播/组播内容
        content = findViewById(R.id.content);
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
                    /*InetAddress address = InetAddress.getByName("www.baidu.com");
                    String name = address.getHostName();
                    // public String getHostAddress()
                    String ip = address.getHostAddress();
                    Log.d(TAG, name + "---" + ip);*/
                    soc = new Socket(IP_ADDRESS, PORT);
                    //获取socket的输入输出流
                    dis = new DataInputStream(soc.getInputStream());
                    dos = new DataOutputStream(soc.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Log.d(TAG, "clinet getprot = " + soc.getPort() + " ,getip = " + soc.getInetAddress());
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

    /**
     * 广播发送
     */
    public void onBroadcastSend(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inetAddress = InetAddress.getByName(BROADCAST_IP);
                    DatagramSocket datagramSocketSend = new DatagramSocket();
                    byte[] data = content.getText().toString().getBytes();
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);
                    datagramSocketSend.send(datagramPacket);
                    // 发送设置为广播
                    datagramSocketSend.setBroadcast(true);
                    datagramSocketSend.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 组播发送
     */
    public void onMulticastSend(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //IP组
                    InetAddress inetAddress = InetAddress.getByName(MULTICAST_IP);
                    //组播监听端口
                    MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
                    multicastSocket.setTimeToLive(1);
                    //加入该组
                    multicastSocket.joinGroup(inetAddress);
                    //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
                    byte[] data = content.getText().toString().getBytes();
                    DatagramPacket dataPacket = new DatagramPacket(data, data.length, inetAddress, MULTICAST_PORT);
                    multicastSocket.send(dataPacket);
                    multicastSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 广播接受
     */
    public void onBroadcastReceive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建接收数据报套接字并将其绑定到本地主机上的指定端口
                    DatagramSocket datagramSocket = new DatagramSocket(BROADCAST_PORT);
                    while (true) {
                        byte[] buf = new byte[1024];
                        final DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        datagramSocket.receive(datagramPacket);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                serverHost = datagramPacket.getAddress().getHostAddress();
                                final String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength())
                                        + " from " + datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort();
                                Toast.makeText(JavaSocketClientActivity.this, "广播接受=" + message, Toast.LENGTH_LONG).show();
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 组播接受
     */
    private void onMulticastSendReceive() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inetAddress = InetAddress.getByName(MULTICAST_IP);
                    MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
                    multicastSocket.joinGroup(inetAddress);
                    byte buf[] = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, inetAddress, MULTICAST_PORT);

                    while (true) {
                        multicastSocket.receive(datagramPacket);
                        final String message = new String(buf, 0, datagramPacket.getLength());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(JavaSocketClientActivity.this, "组播接受=" + message, Toast.LENGTH_LONG).show();
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }


        }).start();
    }
}