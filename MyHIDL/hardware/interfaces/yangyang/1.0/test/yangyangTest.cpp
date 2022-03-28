#include <android/hardware/yangyang/1.0/IYangyang.h>
#include <hidl/Status.h>
#include <hidl/LegacySupport.h> 
#include <utils/misc.h>
#include <hidl/HidlSupport.h>
#include <stdio.h>

using ::android::hardware::hidl_string;
using ::android::sp;
using android::hardware::yangyang::V1_0::IYangyang;

int main(){
    android::sp<IYangyang> service = IYangyang::getService();
    if (service == nullptr){
        printf("->>> yangyang48 | Failed to get service\n");
        return -1;
    }

    service->helloWorld("I am Yangyang48", [&](hidl_string result){
        printf("%s\n", result.c_str());
    });
    return 0;
}


