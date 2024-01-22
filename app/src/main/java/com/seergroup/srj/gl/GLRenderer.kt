package com.seergroup.srj.gl

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import com.seergroup.srj.gl.camera.Camera2D
import com.seergroup.srj.gl.shaderProgram.NormalRssiPoint
import com.seergroup.srj.gl.shaderProgram.XYZAxis
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer : GLSurfaceView.Renderer {
    val camera = Camera2D()

    private lateinit var xyzAxis:XYZAxis
    private lateinit var normalRssiPoint: NormalRssiPoint

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        xyzAxis = XYZAxis()
        normalRssiPoint = NormalRssiPoint()
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        camera.width = width.toFloat()
        camera.height = height.toFloat()
        camera.updateView()
        camera.updateProjection()
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
//        normalRssiPoint.draw(camera)
        xyzAxis.draw(camera)
    }
}