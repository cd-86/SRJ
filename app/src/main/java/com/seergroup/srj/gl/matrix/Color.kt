package com.seergroup.srj.gl.matrix

import kotlin.math.max
import kotlin.math.min

class OpenGLColor(val data: FloatArray = FloatArray(4)) {
    var r: Float
        get() = data[0]
        set(value) {
            data[0] = max(0F, min(1F, value))
        }

    var g: Float
        get() = data[1]
        set(value) {
            data[1] = max(0F, min(1F, value))
        }

    var b: Float
        get() = data[2]
        set(value) {
            data[2] = max(0F, min(1F, value))
        }

    var a: Float
        get() = data[3]
        set(value) {
            data[3] = max(0F, min(1F, value))
        }

    constructor(r: Float, g: Float, b: Float, a: Float) : this() {
        setValue(r, g, b, a)
    }

    fun setValue(r: Float, g: Float, b: Float, a: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is OpenGLColor) {
            return r == other.r && g == other.g && b == other.b && a == other.a
        }
        return false
    }
}

class Color(val data: IntArray = IntArray(4)) {
    var r: Int
        get() = data[0]
        set(value) {
            data[0] = max(0, min(255, value))
        }

    var g: Int
        get() = data[1]
        set(value) {
            data[1] = max(0, min(255, value))
        }

    var b: Int
        get() = data[2]
        set(value) {
            data[2] = max(0, min(255, value))
        }

    var a: Int
        get() = data[3]
        set(value) {
            data[3] = max(0, min(255, value))
        }

    constructor(r: Int, g: Int, b: Int, a: Int) : this() {
        setValue(r, g, b, a)
    }

    fun setValue(r: Int, g: Int, b: Int, a: Int) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    fun toOpenGLColor(): OpenGLColor {
        return OpenGLColor(
            r.toFloat() / 255F,
            g.toFloat() / 255F,
            b.toFloat() / 255F,
            a.toFloat() / 255F
        )
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Color) {
            return r == other.r && g == other.g && b == other.b && a == other.a
        }
        return false
    }
}