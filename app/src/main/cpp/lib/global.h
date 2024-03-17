#include <jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "FontFace.h"
#include "logcat.h"

jlong newFontFace(JNIEnv *env, jobject thiz, jobject am, jstring file_name);

void deleteFontFace(JNIEnv *env, jobject thiz, jlong instance);

jstring getErrorString(JNIEnv *env, jobject thiz, jlong instance);

jobject getCharacter(JNIEnv *env, jobject thiz, jlong instance, jchar character);

jint getTextureWidth(JNIEnv *env, jobject thiz, jlong instance);

jint getTextureHeight(JNIEnv *env, jobject thiz, jlong instance);

jobject getTextureData(JNIEnv *env, jobject thiz, jlong instance);


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass c = env->FindClass("com/seergroup/srj/nativelib/FontFace");
    if (c == nullptr) return JNI_ERR;

    // Register your class' native methods.
    static const JNINativeMethod methods[] = {
            {"newFontFace",      "(Landroid/content/res/AssetManager;Ljava/lang/String;)J",    reinterpret_cast<void *>(newFontFace)},
            {"deleteFontFace",   "(J)V",                                                       reinterpret_cast<void *>(deleteFontFace)},
            {"getErrorString",   "(J)Ljava/lang/String;",                                      reinterpret_cast<void *>(getErrorString)},
            {"getCharacter",     "(JC)Lcom/seergroup/srj/nativelib/FontFace$CharTextureInfo;", reinterpret_cast<void *>(getCharacter)},
            {"getTextureWidth",  "(J)I",                                                       reinterpret_cast<void *>(getTextureWidth)},
            {"getTextureHeight", "(J)I",                                                       reinterpret_cast<void *>(getTextureHeight)},
            {"getTextureData", "(J)Ljava/nio/ByteBuffer;",                                 reinterpret_cast<void *>(getTextureData)},

    };
    int rc = env->RegisterNatives(c, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;
}