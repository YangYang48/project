#ifndef ANDROID_HARDWARE_YANGYANG_V1_0_YANGYANG_H
#define ANDROID_HARDWARE_YANGYANG_V1_0_YANGYANG_H

#include <android/hardware/yangyang/1.0/IYangyang.h>
#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>

namespace android {
namespace hardware {
namespace yangyang {
namespace V1_0 {
namespace implementation {

using ::android::hardware::hidl_array;
using ::android::hardware::hidl_memory;
using ::android::hardware::hidl_string;
using ::android::hardware::hidl_vec;
using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::sp;

struct Yangyang : public IYangyang {
    // Methods from IYangyang follow.
    Return<void> helloWorld(const hidl_string& name, helloWorld_cb _hidl_cb) override;

    // Methods from ::android::hidl::base::V1_0::IBase follow.

};

// FIXME: most likely delete, this is only for passthrough implementations
 extern "C" IYangyang* HIDL_FETCH_IYangyang(const char* name);

}  // namespace implementation
}  // namespace V1_0
}  // namespace yangyang
}  // namespace hardware
}  // namespace android

#endif  // ANDROID_HARDWARE_YANGYANG_V1_0_YANGYANG_H
