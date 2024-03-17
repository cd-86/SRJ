//
// Created by User on 2024/3/3.
//

#ifndef SRJ_LOGCAT_H
#define SRJ_LOGCAT_H

#define LOG_TAG "SRJ-CPP"
#ifdef LOG_TAG
#include <android/log.h>
#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d]" fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d]" fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__)
#define LOGW(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d]" fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d]" fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__)
#else
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#endif

#endif //SRJ_LOGCAT_H
