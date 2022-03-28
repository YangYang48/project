#define LOG_TAG "android.hardware.yangyang@1.0-service"
#include <android/hardware/yangyang/1.0/IYangyang.h>
#include <hidl/LegacySupport.h>
using android::hardware::yangyang::V1_0::IYangyang;
using android::hardware::defaultPassthroughServiceImplementation;

int main(){
    return defaultPassthroughServiceImplementation<IYangyang>();

}
