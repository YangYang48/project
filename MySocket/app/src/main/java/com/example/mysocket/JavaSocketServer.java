package com.example.mysocket;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaSocketServer {
    ServerSocket serverSocket = null;
    private final String TAG = "JavaSocketServer";
    public final int port = 2022;
    private int i = 0;

    public JavaSocketServer(){

        //输出服务器的IP地址
        try {
            InetAddress addr = InetAddress.getLocalHost();
            Log.d(TAG, "->>>local host:" + addr);
            serverSocket = new ServerSocket(port);
            Log.d(TAG,"->>>serverSocket success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startService(){

        try {
            Socket socket = null;
            //等待连接，每建立一个连接，就新建一个线程
            while(true){
                socket = serverSocket.accept();//等待一个客户端的连接，在连接之前，此方法是阻塞的
                Log.d(TAG, "->>>connect to"+socket.getInetAddress()+":"+socket.getLocalPort());
                new ConnectThread(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向客户端发送信息
    class ConnectThread extends Thread{
        Socket socket = null;

        public ConnectThread(Socket socket){
            super();
            this.socket = socket;
        }

        @Override
        public void run(){
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                while(true){
                    i++;
                    String msgRecv = dis.readUTF();
                    Log.d(TAG, "->>>msg from client:"+msgRecv);
                    //服务端的消息，只是在客户端的基本上加了一个符号
                    dos.writeUTF(msgRecv + i);
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
