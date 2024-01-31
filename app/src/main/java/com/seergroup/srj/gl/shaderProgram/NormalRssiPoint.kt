package com.seergroup.srj.gl.shaderProgram

import android.opengl.GLES32
import com.seergroup.srj.gl.GLBuffer
import com.seergroup.srj.gl.GLShader
import com.seergroup.srj.gl.GLVertexArrayObject
import com.seergroup.srj.gl.camera.ICamera
import android.opengl.Matrix

class NormalRssiPoint {
    private val shader: GLShader
    private val vao = GLVertexArrayObject()
    private val vbo = GLBuffer()
    private val count: Int

    init {
        shader = GLShader(
            vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec2 aPos;
                layout (location = 1) in vec4 aColor;
                uniform mat4 uMatrix;
                out vec4 vColor;
                void main()
                {
                    gl_Position = uMatrix * vec4(aPos, 0, 1);
                    vColor = aColor;
                }
                """.trimIndent(),
            fragmentSource = """
                #version 320 es
                precision mediump float;
                in vec4 vColor;
                out vec4 FragColor;
                void main(){
                    FragColor = vColor;
                }
                """.trimIndent()
        )
        shader.link()

        val d = floatArrayOf(
            100.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 100.0f, 1.0f, 0.0f, 0.0f, 1.0f,
        )
        count = 3

        vao.create()
        vbo.create()
        vao.bind()
        vbo.bind()
        vbo.allocate(d)
        shader.setVertexAttribPointer(0, 2, GLES32.GL_FLOAT, 6 * Float.SIZE_BYTES, 0)
        shader.enableAttributeArray(0)
        shader.setVertexAttribPointer(
            1,
            4,
            GLES32.GL_FLOAT,
            6 * Float.SIZE_BYTES,
            2 * Float.SIZE_BYTES
        )
        shader.enableAttributeArray(1)
        vbo.release()
        vao.release()
    }

    fun clearn() {
        vao.destroy()
        vbo.destroy()
        shader.destroy()
    }

    fun draw(camera: ICamera) {
        shader.use()
        vao.bind()
        shader.setMat4("uMatrix", camera.projectionMat * camera.viewMat)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, count)
        vao.release()
        shader.release()
    }
}