#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "MyCrossComplie"
// __VA_ARGS__ 代表 ...的可变参数
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG,  __VA_ARGS__);

extern "C"
{
    //c++调用c需要额外定义extern c
    extern int get();
}

extern "C" JNIEXPORT jstring
Java_com_example_mycrosscompile_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGD("get:(%d)\n", get());
    return env->NewStringUTF(hello.c_str());
}