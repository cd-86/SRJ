package com.seergroup.srj.gl.camera

import com.seergroup.srj.gl.matrix.Matrix4x4
import com.seergroup.srj.gl.matrix.Vec2
import com.seergroup.srj.gl.matrix.Vec3

interface ICamera {
    val width: Float
    val height: Float
    val worldUp: Vec3
    val zoom: Vec3
    val center: Vec3
    val cameraPos: Vec3
    val viewMat: Matrix4x4
    val projectionMat: Matrix4x4
    fun reset()
    fun updateView()
    fun updateProjection()
    fun move(x: Float, y: Float)

    fun move(vec: Vec2)
    fun scale(v: Float)

}