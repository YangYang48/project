#include "Yangyang.h"

namespace android {
namespace hardware {
namespace yangyang {
namespace V1_0 {
namespace implementation {

// Methods from IYangyang follow.
Return<void> Yangyang::helloWorld(const hidl_string& name, helloWorld_cb _hidl_cb) {
    char buf[100];
    ::memset(buf, 0x00, 100);
    ::snprintf(buf, 100, "->>> Yangyang::helloWorld | Hello World, %s", name.c_str());
    hidl_string result(buf);
    _hidl_cb(result);
    return Void();
}


// Methods from ::android::hidl::base::V1_0::IBase follow.

IYangyang* HIDL_FETCH_IYangyang(const char* /* name */) {
    return new Yangyang();
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace yangyang
}  // namespace hardware
}  // namespace android
