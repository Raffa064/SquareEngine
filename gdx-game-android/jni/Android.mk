LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Nome da biblioteca a ser gerada
LOCAL_MODULE    := minha_biblioteca

# Lista de arquivos de origem C/C++
LOCAL_SRC_FILES := test.c

include $(BUILD_SHARED_LIBRARY)

