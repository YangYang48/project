#ifdef LOG_TAG
#undef LOG_TAG
#define LOG_TAG "native_looper_test"
#endif

#include "main.h"

void MyMessageHandler::handleMessage(const Message& message) {
    ALOGD("[Thread=%d] %s message.what=%d \n", gettid(), __func__, message.what);
    messages.push(message);
}

//使用Android源码中的Thread，run就会调用threadLoop，如果返回true不断循环调用threadLoop，直到返回false，不再调用
bool MyLooperThread::threadLoop() {
    if(mLooper == NULL)
        return false;
    //调用pollOnce里面也会循环处理，底层使用了epoll机制，传入的-1就是没有内容epoll直接调用下去
    int32_t ret = mLooper->pollOnce(-1);
    switch (ret) {
        case Looper::POLL_WAKE:
        case Looper::POLL_CALLBACK:
            return true;
        case Looper::POLL_ERROR:
            ALOGE("Looper::POLL_ERROR");
            return true;
        case Looper::POLL_TIMEOUT:
            // timeout (should not happen)
            return true;
        default:
            // should not happen
            ALOGE("Looper::pollOnce() returned unknown status %d", ret);
            return true;
    }
}

MyPipe::MyPipe() {
    int fds[2];
    //这里调用真正的底层linux管道初始化
    ::pipe(fds);

    receiveFd = fds[0];
    sendFd = fds[1];
}

MyPipe::~MyPipe() {
    if (sendFd != -1) {
        ::close(sendFd);
    }

    if (receiveFd != -1) {
        ::close(receiveFd);
    }
}

status_t MyPipe::writeSignal() {
    ssize_t nWritten = ::write(sendFd, "1", 1);
    return nWritten == 1 ? 0 : -errno;
}

status_t MyPipe::readSignal() {
    char buf[1];
    ssize_t nRead = ::read(receiveFd, buf, 1);
    return nRead == 1 ? 0 : nRead == 0 ? -EPIPE : -errno;
}

//回调类
class MyCallbackHandler {
public:
    MyCallbackHandler() : callbackCount(0) {}
    void setCallback(const sp<Looper>& looper, int fd, int events) {
        //往native looper注册fd，回调为staticHandler，参数为MyCallbackHandler.this
        looper->addFd(fd, 0, events, staticHandler, this);
    }

protected:
    int handler(int fd, int events) {
        callbackCount++;
        ALOGD("[Thread=%d] %s fd=%d, events=%d, callbackCount=%d\n", gettid(), __func__, fd, events, callbackCount);
        return 0;
    }

private:
    static int staticHandler(int fd, int events, void* data) {
        return static_cast<MyCallbackHandler*>(data)->handler(fd, events);
    }
    int callbackCount;
};

int main(int argc, char ** argv)
{
    //测试消息机制
    // Looper的轮询处理工作在新线程中
    sp<Looper> mLooper = new Looper(true);
    sp<MyLooperThread> mLooperThread = new MyLooperThread(mLooper.get());
    mLooperThread->run("LooperThread");

    // 测试消息的发送与处理
    sp<MyMessageHandler> handler = new MyMessageHandler();
    ALOGD("[Thread=%d] sendMessage message.what=%d \n", gettid(), 1);
    mLooper->sendMessage(handler, Message(1));
    ALOGD("[Thread=%d] sendMessage message.what=%d \n", gettid(), 2);
    mLooper->sendMessage(handler, Message(2));
    sleep(1);
    
    // 测试监测fd与回调callback
    MyPipe pipe;
    MyCallbackHandler mCallbackHandler;
    mCallbackHandler.setCallback(mLooper, pipe.receiveFd, Looper::EVENT_INPUT);
    ALOGD("[Thread=%d] writeSignal 1\n", gettid());
    pipe.writeSignal(); // would cause FD to be considered signalled
    sleep(1);
    mCallbackHandler.setCallback(mLooper, pipe.receiveFd, Looper::EVENT_INPUT);
    ALOGD("[Thread=%d] writeSignal 2\n", gettid());
    pipe.writeSignal();
    
    sleep(1);
    mLooperThread->requestExit();
    mLooper.clear();
}