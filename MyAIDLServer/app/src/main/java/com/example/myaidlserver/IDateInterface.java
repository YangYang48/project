package com.example.myaidlserver;

/**
 * 这是一个内部接口
 * 用于回调数据从service到主ui
 */
public interface IDateInterface {
    //是否开启数据发送，0是不准备发送，1是准备发送
    int StartUp();
    void DialogUpdate(int progress);
}
