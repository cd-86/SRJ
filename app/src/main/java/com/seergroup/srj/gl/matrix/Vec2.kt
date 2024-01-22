package com.seergroup.srj.gl.matrix

class Vec2(val data: FloatArray = FloatArray(2)) {

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

    constructor(x: Float, y: Float) : this() {
        setValue(x, y)
    }

    fun setValue(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    operator fun plus(vec: Vec2): Vec2 {
        return Vec2(x + vec.x, y + vec.y)
    }

    operator fun plus(v: Float): Vec2 {
        return Vec2(x + v, y + v)
    }

    operator fun minus(vec: Vec2): Vec2 {
        return Vec2(x - vec.x, y - vec.y)
    }

    operator fun minus(v: Float): Vec2 {
        return Vec2(x - v, y - v)
    }

    operator fun times(vec: Vec2): Vec2 {
        return Vec2(x * vec.x, y * vec.y)
    }

    operator fun times(v: Float): Vec2 {
        return Vec2(x * v, y * v)
    }

    operator fun div(vec: Vec2): Vec2 {
        return Vec2(x / vec.x, y / vec.y)
    }

    operator fun div(v: Float): Vec2 {
        return Vec2(x / v, y / v)
    }


    operator fun plusAssign(vec: Vec2) {
        x += vec.x
        y += vec.y
    }

    operator fun plusAssign(v: Float) {
        x += v
        y += v
    }

    operator fun minusAssign(vec: Vec2) {
        x -= vec.x
        y -= vec.y
    }

    operator fun minusAssign(v: Float) {
        x -= v
        y -= v
    }

    operator fun timesAssign(vec: Vec2) {
        x *= vec.x
        y *= vec.y
    }

    operator fun timesAssign(v: Float) {
        x *= v
        y *= v
    }

    operator fun divAssign(vec: Vec2) {
        x /= vec.x
        y /= vec.y
    }

    operator fun divAssign(v: Float) {
        x /= v
        y /= v
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Vec2) {
            return x == other.x && y == other.y
        }
        return false
    }
}