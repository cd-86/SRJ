package com.seergroup.srj.gl

import android.opengl.GLES32
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class GLBuffer(var type: Int = GLES32.GL_ARRAY_BUFFER) {

    private var d: IntBuffer? = null
    var usagePattern: Int = GLES32.GL_STATIC_DRAW
    fun create() {
        d?.also { destroy() }
        d = IntBuffer.allocate(1)
        GLES32.glGenBuffers(1, d)
    }

    fun bind() {
        d?.also { GLES32.glBindBuffer(type, d!![0]) }
    }

    fun release(): Unit {
        GLES32.glBindBuffer(type, 0)
    }

    fun destroy() {
        d?.also {
            GLES32.glDeleteBuffers(1, d)
            d = null
        }
    }

    fun allocate(data: Buffer, count: Int, usagePattern: Int? = null) {
        usagePattern?.also { this.usagePattern = usagePattern }
        GLES32.glBufferData(type, count, data, this.usagePattern)
    }

    fun allocate(count: Int, usagePattern: Int?) {
        usagePattern?.also { this.usagePattern = usagePattern }
        GLES32.glBufferData(type, count, null, this.usagePattern)
    }

    fun allocate(array: FloatArray, usagePattern: Int? = null) {
        ByteBuffer.allocateDirect(array.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(array)
                position(0)
            }
        }.also {
            allocate(it, array.size * Float.SIZE_BYTES, usagePattern)
        }
    }

    fun allocate(array: IntArray, usagePattern: Int? = null) {
        ByteBuffer.allocateDirect(array.size * Int.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asIntBuffer().apply {
                put(array)
                position(0)
            }
        }.also {
            allocate(it, array.size * Int.SIZE_BYTES, usagePattern)
        }
    }

    fun write(offset: Int, count: Int, data: Buffer) {
        GLES32.glBufferSubData(type, offset, count, data)
    }
}