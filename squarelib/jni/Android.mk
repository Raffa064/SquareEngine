LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Nome da biblioteca a ser gerada
LOCAL_MODULE    := square

# Lista de arquivos de origem C/C++
LOCAL_SRC_FILES := square.c

include $(BUILD_SHARED_LIBRARY)

