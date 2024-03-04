//
// Created by User on 2024/3/3.
//
#include "global.h"

jlong newFontFace(JNIEnv *env, jobject thiz, jobject am, jstring file_name) {
    // 获取 assetManager 对象
    AAssetManager *assetManager = AAssetManager_fromJava(env, am);
    if (!assetManager) {
        return 0;
    }
    // 打开文件
    const char *fileName = env->GetStringUTFChars(file_name, 0);
    AAsset *asset = AAssetManager_open(assetManager, fileName, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_name, fileName);
    if (!asset) {
        return 0;
    }
    size_t file_size = AAsset_getLength(asset);
    FT_Byte *file_base = new FT_Byte[file_size];
    AAsset_read(asset, file_base, file_size);
    AAsset_close(asset);
    FontFace *fontFace = new FontFace(file_base, file_size);
    delete[] file_base;
    return reinterpret_cast<jlong>(fontFace);
}

void deleteFontFace(JNIEnv *env, jobject thiz, jlong instance) {
    if (!instance) return;
    FontFace *fontFace = reinterpret_cast<FontFace *>(instance);
    delete fontFace;
}

jstring getErrorString(JNIEnv *env, jobject thiz, jlong instance) {
    if (!instance) return nullptr;
    FontFace *fontFace = reinterpret_cast<FontFace *>(instance);
    return env->NewStringUTF(fontFace->getErrorString().c_str());
}

jobject getCharacter(JNIEnv *env, jobject thiz, jlong instance, jchar character) {
    if (!instance) return nullptr;
    FontFace *fontFace = reinterpret_cast<FontFace *>(instance);
    FontFace::TextureInfo info = fontFace->getCharInfo(character);
    jclass cls = env->FindClass("com/seergroup/srj/nativelib/FontFace$CharTextureInfo");
    if (cls == nullptr) return nullptr;
    jmethodID method = env->GetMethodID(cls, "<init>", "(IIIIII)V");
    if (method == nullptr) return nullptr;
    return env->NewObject(cls, method, (int)info.width, (int)info.height, info.bearingX, info.bearingY, info.advance, (int)info.xOffset);
}
