package com.seergroup.srj.gl

import android.graphics.Bitmap
import android.opengl.GLES32
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.IntBuffer

class GLTexture {
    var d: IntBuffer? = null
        private set

    var width = 0
    var height = 0

    fun create() {
        d?.also { destroy() }
        d = IntBuffer.allocate(1)
        GLES32.glGenTextures(1, d)
    }

    fun setImage(bitmap: Bitmap) {
        d ?: create()
        bind()
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_LINEAR_MIPMAP_LINEAR
        )
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)
        release()
        width = bitmap.width
        height = bitmap.height
    }

    fun setImage(imgData: ByteArray, width: Int, height: Int, format: Int = GLES32.GL_RGBA) {
        d ?: create()
        bind()
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_LINEAR_MIPMAP_LINEAR
        )
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D,
            0,
            format,
            width,
            height,
            0,
            format,
            GLES32.GL_UNSIGNED_BYTE,
            ByteBuffer.wrap(imgData)
        )
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)
        release()
        this.width = width
        this.height = height
    }

    inline fun setImage(f: () -> Unit) {
        d ?: create()
        bind()
        f()
        release()
    }

    fun bind() {
        d?.also { GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, d!![0]) }
    }

    fun active() {
        d?.also { GLES32.glActiveTexture(GLES32.GL_TEXTURE0) }
    }

    fun active(index: Int) {
        d?.also { GLES32.glActiveTexture(GLES32.GL_TEXTURE0 + index) }
    }

    fun release(): Unit {
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
    }

    fun destroy() {
        d?.also {
            GLES32.glDeleteTextures(1, d)
            d = null
        }
    }
}