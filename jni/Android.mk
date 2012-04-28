LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := UInputManager
LOCAL_SRC_FILES := UInputManager.c

LOCAL_CFLAGS    := -std=c99

include $(BUILD_SHARED_LIBRARY)
