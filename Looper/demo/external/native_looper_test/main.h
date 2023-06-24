#ifndef NATIVE_LOOPER_TEST_MAIN_H
#define NATIVE_LOOPER_TEST_MAIN_H
#include <utils/Looper.h>
#include <utils/Timers.h>
#include <utils/Log.h>
#include <unistd.h>
#include <time.h>
#include <utils/threads.h>

//使用Android智能指针或者日志需要使用这个命名空间
using namespace android;
using namespace std;

class MyMessageHandler : public MessageHandler {
public:
    Vector<Message> messages;

    virtual void handleMessage(const Message& message);
    virtual ~MyMessageHandler(){}
};

struct MyLooperThread : public Thread {
public:
    MyLooperThread(Looper *looper)
        : mLooper(looper) {
    }
	
    virtual bool threadLoop();

protected:
    virtual ~MyLooperThread() {}

private:
    Looper *mLooper;
};

class MyPipe {
public:
    int sendFd;
    int receiveFd;
    
    MyPipe();
    ~MyPipe();
    status_t writeSignal();
    status_t readSignal();
};

#endif