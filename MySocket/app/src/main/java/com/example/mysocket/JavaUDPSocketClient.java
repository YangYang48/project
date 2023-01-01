package com.example.mysocket;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class JavaUDPSocketClient {
    private final String TAG = "JavaUDPSocketClient";
    private final int PORT = 2023;
    DatagramSocket datagramSocket = null;
    DatagramPacket datagramPacket = null;
    InetAddress addr = null;
    JavaUDPSocketClient() throws UnknownHostException {
        //1.创建服务器端DatagramSocket，指定端口号
         addr = InetAddress.getLocalHost();
         String IP_ADDRESS = addr.toString().split("/")[1];
         Log.d(TAG, "->>>addr = " + addr + " , IP_ADDRESS = " + IP_ADDRESS);
    }

    public void StartClient() throws IOException {
        //2.创建数据报，包含发送的数据信息
        byte[] data = "username:yangyang48;passwd:2023-01-01".getBytes();
        datagramPacket = new DatagramPacket(data, data.length, addr, PORT);
        //3.创建DatagramSocket对象
        datagramSocket = new DatagramSocket();
        //4.向服务器端发送数据报
        datagramSocket.send(datagramPacket);

        //接收服务端信息
        //1.创建数据报，用于接收服务器端响应的数据
        byte[] data2 = new byte[1024];
        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
        //2.接收服务器响应的数据
        datagramSocket.receive(packet2);
        //3.读取数据
        String reply = new String(data2, 0, packet2.getLength());
        Log.d(TAG, "->>>UDP client reply = " + reply);
        //4.关闭资源
        datagramSocket.close();
    }
}
