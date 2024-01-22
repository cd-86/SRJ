package com.seergroup.srj.gl.matrix

import android.opengl.Matrix

class Matrix4x4(
    val data: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
) {
    fun setToIdentity() {
        Matrix.setIdentityM(data, 0);
    }

    fun lookAt(
        eyeX: Float,
        eyeY: Float,
        eyeZ: Float,
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        upX: Float,
        upY: Float,
        upZ: Float
    ) {
        Matrix.setLookAtM(
            data, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ
        )
    }

    fun lookAt(eye: Vec3, center: Vec3, up: Vec3) {
        lookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z)
    }

    fun frustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        Matrix.frustumM(data, 0, left, right, bottom, top, near, far)
    }

    fun frustum(left: Float, right: Float, bottom: Float, top: Float) {
        frustum(left, right, bottom, top, -1f, 1f)
    }

    fun frustum(width: Float, height: Float) {
        frustum(-width / 2, width / 2, -height / 2, height / 2)
    }

    fun ortho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        Matrix.orthoM(data, 0, left, right, bottom, top, near, far)
    }

    fun ortho(left: Float, right: Float, bottom: Float, top: Float) {
        ortho(left, right, bottom, top, -1f, 1f)
    }

    fun ortho(width: Float, height: Float) {
        ortho(-width / 2, width / 2, -height / 2, height / 2)
    }

    fun perspective(fov: Float, aspect: Float, near: Float, far: Float) {
        Matrix.perspectiveM(data, 0, fov, aspect, near, far)
    }

    fun perspective(fov: Float, aspect: Float) {
        perspective(fov, aspect, 1f, -1f)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(data, 0, angle, x, y, z)
    }

    fun rotate(angle: Float, vec: Vec3) {
        rotate(angle, vec.x, vec.y, vec.z)
    }

    fun scale(x: Float, y: Float, z: Float) {
        Matrix.scaleM(data, 0, x, y, z)
    }

    fun scale(vec: Vec3) {
        scale(vec.x, vec.y, vec.z)
    }

    fun scale(scale: Float) {
        scale(scale, scale, scale)
    }

    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(data, 0, x, y, z)
    }

    fun translate(vec: Vec3) {
        translate(vec.x, vec.y, vec.z)
    }

    operator fun times(mat: Matrix4x4): Matrix4x4 {
        val result = Matrix4x4()
        Matrix.multiplyMM(result.data, 0, data, 0, mat.data, 0)
        return result
    }

    operator fun times(vec: Vec3): Vec3 {
        val result = Vec3()
        Matrix.multiplyMV(
            result.data, 0, data, 0, floatArrayOf(vec.x, vec.y, vec.z, 1f), 0
        )
        return result
    }

    operator fun timesAssign(matrix4x4: Matrix4x4) {
        Matrix.multiplyMM(data, 0, data, 0, matrix4x4.data, 0)
    }
}