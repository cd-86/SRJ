package com.seergroup.srj.gl.matrix

class Vec4(val data: FloatArray = FloatArray(4)) {

    var x: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }

    var y: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }

    var z: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    var w: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }

    constructor(x: Float, y: Float, z: Float, w: Float) : this() {
        setValue(x, y, z, w)
    }

    fun setValue(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    operator fun plus(vec: Vec4): Vec4 {
        return Vec4(x + vec.x, y + vec.y, z + vec.z, w + vec.w)
    }

    operator fun plus(v: Float): Vec4 {
        return Vec4(x + v, y + v, z + v, w + v)
    }

    operator fun minus(vec: Vec4): Vec4 {
        return Vec4(x - vec.x, y - vec.y, z - vec.z, w - vec.w)
    }

    operator fun minus(v: Float): Vec4 {
        return Vec4(x - v, y - v, z - v, w - v)
    }

    operator fun times(vec: Vec4): Vec4 {
        return Vec4(x * vec.x, y * vec.y, z * vec.z, w * vec.w)
    }

    operator fun times(v: Float): Vec4 {
        return Vec4(x * v, y * v, z * v, w * v)
    }

    operator fun div(vec: Vec4): Vec4 {
        return Vec4(x / vec.x, y / vec.y, z / vec.z, w / vec.w)
    }

    operator fun div(v: Float): Vec4 {
        return Vec4(x / v, y / v, z / v, w / v)
    }


    operator fun plusAssign(vec: Vec4) {
        x += vec.x
        y += vec.y
        z += vec.z
        w += vec.w
    }

    operator fun plusAssign(v: Float) {
        x += v
        y += v
        z += v
        w += v
    }

    operator fun minusAssign(vec: Vec4) {
        x -= vec.x
        y -= vec.y
        z -= vec.z
        w -= vec.w
    }

    operator fun minusAssign(v: Float) {
        x -= v
        y -= v
        z -= v
        w -= v
    }

    operator fun timesAssign(vec: Vec4) {
        x *= vec.x
        y *= vec.y
        z *= vec.z
        w *= vec.w
    }

    operator fun timesAssign(v: Float) {
        x *= v
        y *= v
        z *= v
        w *= v
    }

    operator fun divAssign(vec: Vec4) {
        x /= vec.x
        y /= vec.y
        z /= vec.z
        w /= vec.w
    }

    operator fun divAssign(v: Float) {
        x /= v
        y /= v
        z /= v
        w /= v
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Vec4) {
            return x == other.x && y == other.y && z == other.z && w == other.w
        }
        return false
    }
}