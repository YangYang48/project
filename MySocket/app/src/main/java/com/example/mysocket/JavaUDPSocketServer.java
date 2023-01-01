package com.example.mysocket;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class JavaUDPSocketServer {
    private final String TAG = "JavaUDPSocketServer";
    private final int PORT = 2023;
    DatagramSocket datagramSocket = null;
    DatagramPacket datagramPacket = null;
    JavaUDPSocketServer() throws SocketException, UnknownHostException {
        //1.创建服务器端DatagramSocket，指定端口号
        InetAddress addr = InetAddress.getLocalHost();
        datagramSocket = new DatagramSocket(PORT, addr);
    }

    public void StartService() throws IOException {
        //2.创建数据报，用于接收客户端发送的数据
        byte[] data = new byte[1024];
        datagramPacket = new DatagramPacket(data, data.length);
        Log.d(TAG, "->>>UDP server is start ,wait client connected... ");
        //3.接收客户端发送的数据
        //receive方法是阻塞方法
        datagramSocket.receive(datagramPacket);
        //4.读取数据
        String info = new String(data, 0, datagramPacket.getLength());
        Log.d(TAG, "->>>UDP server : info = " + info);

        //响应客户端
        //1.定于客户端地址、端口号、数据
        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        byte[] data2 = "hello".getBytes();
        //2.创建数据报，包含响应的数据信息
        DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
        datagramSocket.send(packet2);
        //4.关闭资源
        //datagramSocket.close();
    }

}
