package com.seergroup.srj.gl

import android.opengl.GLES32
import java.nio.IntBuffer

class GLVertexArrayObject {
    private var d: IntBuffer? = null

    fun create() {
        d?.also { destroy() }
        d = IntBuffer.allocate(1)
        GLES32.glGenVertexArrays(1, d)
    }

    fun bind() {
        d?.also { GLES32.glBindVertexArray(d!![0]) }
    }

    fun release(): Unit {
        GLES32.glBindVertexArray(0)
    }

    fun destroy() {
        d?.also {
            GLES32.glDeleteVertexArrays(1, d)
            d = null
        }
    }
}