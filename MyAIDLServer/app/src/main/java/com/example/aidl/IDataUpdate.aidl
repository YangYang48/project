// IDataUpdate.aidl
package com.example.aidl;


/**
 * 这是一个aidl定义接口
 * 这个aidl接口主要用来发送数据
 */
interface IDataUpdate {
    //是否开启数据发送，0是不准备发送，1是准备发送
    int StartUp();
    void DialogUpdate(int progress);
}