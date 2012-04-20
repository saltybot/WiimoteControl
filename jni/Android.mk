LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := UInputManager
LOCAL_SRC_FILES := UInputManager.c

include $(BUILD_SHARED_LIBRARY)
