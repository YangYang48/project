#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativetid_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}extern "C"
JNIEXPORT jint JNICALL
Java_com_example_nativetid_MainActivity_getTid(JNIEnv *env, jclass clazz, jlong native_peer) {
    auto *tid = reinterpret_cast<uint32_t *>(native_peer +sizeof(uint32_t)+sizeof(int)
                                            +sizeof(int)+sizeof(uint32_t));
    return reinterpret_cast<jint>((int) *tid);
}