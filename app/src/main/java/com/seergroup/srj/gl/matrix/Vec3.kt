package com.seergroup.srj.gl.matrix

class Vec3(val data: FloatArray = FloatArray(3)) {

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

    constructor(x: Float, y: Float, z: Float) : this() {
        setValue(x, y, z)
    }

    fun setValue(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun plus(vec: Vec3): Vec3 {
        return Vec3(x + vec.x, y + vec.y, z + vec.z)
    }

    operator fun plus(v: Float): Vec3 {
        return Vec3(x + v, y + v, z + v)
    }

    operator fun minus(vec: Vec3): Vec3 {
        return Vec3(x - vec.x, y - vec.y, z - vec.z)
    }

    operator fun minus(v: Float): Vec3 {
        return Vec3(x - v, y - v, z - v)
    }

    operator fun times(vec: Vec3): Vec3 {
        return Vec3(x * vec.x, y * vec.y, z * vec.z)
    }

    operator fun times(v: Float): Vec3 {
        return Vec3(x * v, y * v, z * v)
    }

    operator fun div(vec: Vec3): Vec3 {
        return Vec3(x / vec.x, y / vec.y, z / vec.z)
    }

    operator fun div(v: Float): Vec3 {
        return Vec3(x / v, y / v, z / v)
    }


    operator fun plusAssign(vec: Vec3) {
        x += vec.x
        y += vec.y
        z += vec.z
    }

    operator fun plusAssign(v: Float) {
        x += v
        y += v
        z += v
    }

    operator fun minusAssign(vec: Vec3) {
        x -= vec.x
        y -= vec.y
        z -= vec.z
    }

    operator fun minusAssign(v: Float) {
        x -= v
        y -= v
        z -= v
    }

    operator fun timesAssign(vec: Vec3) {
        x *= vec.x
        y *= vec.y
        z *= vec.z
    }

    operator fun timesAssign(v: Float) {
        x *= v
        y *= v
        z *= v
    }

    operator fun divAssign(vec: Vec3) {
        x /= vec.x
        y /= vec.y
        z /= vec.z
    }

    operator fun divAssign(v: Float) {
        x /= v
        y /= v
        z /= v
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Vec3) {
            return x == other.x && y == other.y && z == other.z
        }
        return false
    }
}