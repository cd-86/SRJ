package com.seergroup.srj.gl

import android.opengl.GLES32
import java.nio.IntBuffer

class GLShader {
    private var d = 0
    var vertexSource = String()
    var fragmentSource = String()
    var geometrySource = String()

    constructor(vertexSource: String, fragmentSource: String) {
        this.vertexSource = vertexSource
        this.fragmentSource = fragmentSource
    }

    constructor(vertexSource: String, fragmentSource: String, geometrySource: String) {
        this.vertexSource = vertexSource
        this.fragmentSource = fragmentSource
        this.geometrySource = geometrySource
    }

    fun link() {
        if (d != 0) destroy()
        if (vertexSource.isEmpty() || fragmentSource.isEmpty())
            throw RuntimeException("Vertex source or Fragment source is empty.")
        // 不要处理错误，让它崩溃
        val success = IntBuffer.allocate(1)
        // 顶点着色器
        val vertexShader = GLES32.glCreateShader(GLES32.GL_VERTEX_SHADER)
        GLES32.glShaderSource(vertexShader, vertexSource)
        GLES32.glCompileShader(vertexShader)
        GLES32.glGetShaderiv(vertexShader, GLES32.GL_COMPILE_STATUS, success)
        if (success[0] == 0) {
            throw RuntimeException(GLES32.glGetShaderInfoLog(vertexShader))
        }

        // 片段着色器
        val fragmentShader = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER)
        GLES32.glShaderSource(fragmentShader, fragmentSource)
        GLES32.glCompileShader(fragmentShader)
        GLES32.glGetShaderiv(fragmentShader, GLES32.GL_COMPILE_STATUS, success)
        if (success[0] == 0) {
            GLES32.glDeleteShader(vertexShader)
            throw RuntimeException(GLES32.glGetShaderInfoLog(fragmentShader))
        }

        var geometryShader: Int? = null
        // 几何着色器
        if (geometrySource.isNotEmpty()) {
            geometryShader = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER)
            GLES32.glShaderSource(geometryShader, geometrySource)
            GLES32.glCompileShader(geometryShader)
            GLES32.glGetShaderiv(geometryShader, GLES32.GL_COMPILE_STATUS, success)
            if (success[0] == 0) {
                GLES32.glDeleteShader(vertexShader)
                GLES32.glDeleteShader(fragmentShader)
                throw RuntimeException(GLES32.glGetShaderInfoLog(geometryShader))
            }
        }

        // 着色器程序
        d = GLES32.glCreateProgram()
        GLES32.glAttachShader(d, vertexShader)
        GLES32.glAttachShader(d, fragmentShader)
        if (geometryShader != null) {
            GLES32.glAttachShader(d, geometryShader)
        }
        GLES32.glLinkProgram(d)
        GLES32.glGetProgramiv(d, GLES32.GL_LINK_STATUS, success)
        if (success[0] == 0) {
            throw RuntimeException(GLES32.glGetProgramInfoLog(d))
        }
        // 删除
        GLES32.glDeleteShader(vertexShader)
        GLES32.glDeleteShader(fragmentShader)
        if (geometryShader != null) {
            GLES32.glDeleteShader(geometryShader)
        }
    }

    fun use() {
        GLES32.glUseProgram(d)
    }

    fun release() {
        GLES32.glUseProgram(0)
    }

    fun destroy() {
        GLES32.glDeleteProgram(d)
        d = 0
    }

    fun setVertexAttribPointer(location: Int, tupleSize: Int, type: Int, stride: Int, offset: Int) {
        GLES32.glVertexAttribPointer(location, tupleSize, type, false, stride, offset)
    }

    fun setVertexAttribPointer(name: String, tupleSize: Int, type: Int, stride: Int, offset: Int) {
        setVertexAttribPointer(GLES32.glGetAttribLocation(d, name), tupleSize, type, stride, offset)
    }

    fun enableAttributeArray(location: Int) {
        GLES32.glEnableVertexAttribArray(location)
    }

    fun enableAttributeArray(name: String) {
        enableAttributeArray(GLES32.glGetAttribLocation(d, name))
    }

    fun setBool(name: String, value: Boolean) {
        GLES32.glUniform1i(GLES32.glGetUniformLocation(d, name), if (value) 1 else 0)
    }

    fun setInt(name: String, value: Int) {
        GLES32.glUniform1i(GLES32.glGetUniformLocation(d, name), value)
    }

    fun setFloat(name: String, value: Float) {
        GLES32.glUniform1f(GLES32.glGetUniformLocation(d, name), value)
    }

    fun setVec2(name: String, x: Float, y: Float) {
        GLES32.glUniform2f(GLES32.glGetUniformLocation(d, name), x, y)
    }

    fun setVec2(name: String, value: FloatArray) {
        GLES32.glUniform2fv(GLES32.glGetUniformLocation(d, name), 1, value, 0)
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        GLES32.glUniform3f(GLES32.glGetUniformLocation(d, name), x, y, z)
    }

    fun setVec3(name: String, value: FloatArray) {
        GLES32.glUniform3fv(GLES32.glGetUniformLocation(d, name), 1, value, 0)
    }

    fun setVec4(name: String, x: Float, y: Float, z: Float, w: Float) {
        GLES32.glUniform4f(GLES32.glGetUniformLocation(d, name), x, y, z, w)
    }

    fun setVec4(name: String, value: FloatArray) {
        GLES32.glUniform4fv(GLES32.glGetUniformLocation(d, name), 1, value, 0)
    }

    fun setMat2(name: String, value: FloatArray) {
        GLES32.glUniformMatrix2fv(GLES32.glGetUniformLocation(d, name), 1, false, value, 0)
    }

    fun setMat3(name: String, value: FloatArray) {
        GLES32.glUniformMatrix3fv(GLES32.glGetUniformLocation(d, name), 1, false, value, 0)
    }

    fun setMat4(name: String, value: FloatArray) {
        GLES32.glUniformMatrix4fv(GLES32.glGetUniformLocation(d, name), 1, false, value, 0)
    }

    fun setTexture(name: String, texture: Int, index: Int) {
    }
}