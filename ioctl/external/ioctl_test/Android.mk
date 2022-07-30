LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ioctl_test
LOCAL_SRC_FILES += ioctl_test.c
LOCAL_C_INCLUDE += $(LOCAL_PATH)/
LOCAL_C_FLAG := -Wreturn-local-addr -Wwriting_strings -fpermissive -fexceptions -Wall -Wno-unused-variable -lgcc_s -std=c99
#编译生成动态文件so
include $(BUILD_SHARE_LIBRARY)
include $(call all_makefiles_under, $(LOCAL_PATH))