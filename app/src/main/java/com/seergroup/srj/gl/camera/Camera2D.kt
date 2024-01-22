package com.seergroup.srj.gl.camera

import com.seergroup.srj.gl.matrix.Matrix4x4
import com.seergroup.srj.gl.matrix.Vec2
import com.seergroup.srj.gl.matrix.Vec3

class Camera2D(override var width: Float = 1f, override var height: Float = 1f) : ICamera {

    override val worldUp = Vec3()
    override val zoom = Vec3()
    override val center = Vec3()
    override val cameraPos = Vec3()
    override val viewMat = Matrix4x4()
    override val projectionMat = Matrix4x4()

    init {
        reset()
    }

    override fun reset(): Unit {
        worldUp.setValue(0f, 1f, 0f)
        zoom.setValue(0.5f, 0.5f, 0.5f)
        center.setValue(0f, 0f, 0f)
        cameraPos.setValue(0f, 0f, 10000f)
    }

    override fun updateView() {
        cameraPos.x = center.x
        cameraPos.y = center.y
        viewMat.lookAt(cameraPos, center, worldUp)
    }

    override fun updateProjection() {
        projectionMat.ortho(
            -width / 2 * zoom.x,
            width / 2 * zoom.x,
            -height / 2 * zoom.y,
            height / 2 * zoom.y,
            0.1f,
            10000f
        )
    }

    override fun move(x: Float, y: Float): Unit {
        center.x += x * zoom.x
        center.y += y * zoom.y
        updateView()
    }

    override fun move(vec: Vec2) {
        center.x += vec.x * zoom.x
        center.y += vec.y * zoom.y
        updateView()
    }

    override fun scale(v: Float): Unit {
        zoom.x *= v
        zoom.y *= v
        updateProjection()
    }
}