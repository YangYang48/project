LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := main.cpp

LOCAL_SHARED_LIBRARIES := \
                       liblog \
                       libutils \

LOCAL_C_INCLUDES := \
                 system/core/include/cutils \
                 external/native_looper_test

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE := native_looper_test

include $(BUILD_EXECUTABLE)