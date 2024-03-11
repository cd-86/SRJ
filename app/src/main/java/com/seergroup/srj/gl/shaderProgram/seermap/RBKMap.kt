package com.seergroup.srj.gl.shaderProgram.seermap

import android.opengl.GLES32
import com.seergroup.srj.gl.GLBuffer
import com.seergroup.srj.gl.GLShader
import com.seergroup.srj.gl.GLVertexArrayObject
import com.seergroup.srj.gl.camera.ICamera

class RBKMap {
    private val bgShader: GLShader
    private val bgBuffObj = object {
        val vao = GLVertexArrayObject()
        val vbo = GLBuffer()
        var count = 0
    }
    private val posShader: GLShader
    private val posBuffObj = object {
        val vao = GLVertexArrayObject()
        val vbo = GLBuffer()
        var count = 0
    }

    private val lineShader: GLShader
    private val lineBuffObj = object {
        val vao = GLVertexArrayObject()
        val vbo = GLBuffer()
        val ebo = GLBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER)
        var count = 0
    }

    init {
        bgShader = GLShader(
            vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec2 aPos; // x, y
                uniform mat4 uMatrix;
                void main()
                {
                    gl_Position = uMatrix * vec4(aPos, 0.0, 1.0);
                }
                """.trimIndent(),
            fragmentSource = """
                #version 320 es
                precision mediump float;
                out vec4 FragColor;
                void main(){
                    FragColor = vec4(1.0, 1.0, 1.0, 1.0);
                }
                """.trimIndent()
        )
        bgShader.link()
        bgBuffObj.vao.create()
        bgBuffObj.vbo.create()
        bgBuffObj.vao.bind()
        bgBuffObj.vbo.bind()
        bgShader.setVertexAttribPointer(0, 2, GLES32.GL_FLOAT, 2 * Float.SIZE_BYTES, 0)
        bgShader.enableAttributeArray(0)
        bgBuffObj.vbo.release()
        bgBuffObj.vao.release()

        posShader = GLShader(
            vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec3 aPos; // x, y, type 0: 黑色 1: 红色
                void main()
                {
                    gl_Position = vec4(aPos, 1.0);
                }
                """.trimIndent(),
            geometrySource = """
                #version 320 es
                precision mediump float;
                layout (points) in;
                layout (triangle_strip, max_vertices = 4) out;
                uniform mat4 uMatrix;
                uniform float uPointHalfWidth;
                out vec4 vColor;
                void main(){
                    vec4 pos = gl_in[0].gl_Position;
                    if (pos.z == 1.0) {
                        vColor = vec4(1.0, 0.0, 0.0, 1.0);
                    } else {
                        vColor = vec4(0.0, 0.0, 0.0, 1.0);
                    }
                    gl_Position = uMatrix * vec4(pos.x - uPointHalfWidth, pos.y + uPointHalfWidth, 0.0, 1.0);
                    EmitVertex();
                    gl_Position = uMatrix * vec4(pos.x + uPointHalfWidth, pos.y + uPointHalfWidth, 0.0, 1.0);
                    EmitVertex();
                    gl_Position = uMatrix * vec4(pos.x - uPointHalfWidth, pos.y - uPointHalfWidth, 0.0, 1.0);
                    EmitVertex();
                    gl_Position = uMatrix * vec4(pos.x + uPointHalfWidth, pos.y - uPointHalfWidth, 0.0, 1.0);
                    EmitVertex();
                    EndPrimitive();
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
        posShader.link()
        posBuffObj.vao.create()
        posBuffObj.vbo.create()
        posBuffObj.vao.bind()
        posBuffObj.vbo.bind()
        posShader.setVertexAttribPointer(0, 3, GLES32.GL_FLOAT, 3 * Float.SIZE_BYTES, 0)
        posShader.enableAttributeArray(0)
        posBuffObj.vbo.release()
        posBuffObj.vao.release()

        lineShader = GLShader(
            vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec2 aPos;
                layout (location = 1) in vec4 aColor;
                out vec4 vColor;
                uniform mat4 uMatrix;
                void main()
                {
                    gl_Position = uMatrix * vec4(aPos, 0.0, 1.0);
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
        lineShader.link()
        lineBuffObj.vao.create()
        lineBuffObj.vbo.create()
        lineBuffObj.ebo.create()
        lineBuffObj.vao.bind()
        lineBuffObj.vbo.bind()
        lineBuffObj.ebo.bind()
        lineShader.setVertexAttribPointer(0, 2, GLES32.GL_FLOAT, 6 * Float.SIZE_BYTES, 0)
        lineShader.enableAttributeArray(0)
        lineShader.setVertexAttribPointer(1, 4, GLES32.GL_FLOAT, 6 * Float.SIZE_BYTES, 2 * Float.SIZE_BYTES)
        lineShader.enableAttributeArray(1)
        lineBuffObj.vbo.release()
        lineBuffObj.vao.release()
        lineBuffObj.ebo.release()
    }

    fun clearn() {
        bgShader.destroy()
        bgBuffObj.vao.destroy()
        bgBuffObj.vbo.destroy()

        posShader.destroy()
        posBuffObj.vao.destroy()
        posBuffObj.vbo.destroy()

        lineShader.destroy()
        lineBuffObj.vao.destroy()
        lineBuffObj.vbo.destroy()
        lineBuffObj.ebo.destroy()
    }

    fun setBound(bound: Bound, w:Float = 0F) {
        if (bound.xMax == Float.NEGATIVE_INFINITY) {
            bgBuffObj.count = 0
        } else {
            bgBuffObj.count = 4
            bgBuffObj.vbo.bind()
            val vertex = floatArrayOf(
                bound.xMin - w, bound.yMax + w,
                bound.xMax + w, bound.yMax + w,
                bound.xMin - w, bound.yMin - w,
                bound.xMax + w, bound.yMin - w
            )
            bgBuffObj.vbo.allocate(vertex)
            bgBuffObj.vbo.release()
        }
    }

    fun setNormalPosAndRssiPosVertex(vertex: PosVertex) {
        posBuffObj.count = vertex.vertexCount
        posBuffObj.vbo.bind()
        posBuffObj.vbo.allocate(vertex.data)
        posBuffObj.vbo.release()
    }

    fun setLineVertex(vertex: LineVertex) {
        lineBuffObj.count = vertex.indices.size
        lineBuffObj.vbo.bind()
        lineBuffObj.vbo.allocate(vertex.data)
        lineBuffObj.vbo.release()
        lineBuffObj.ebo.bind()
        lineBuffObj.ebo.allocate(vertex.indices)
        lineBuffObj.ebo.release()
    }

    fun draw(camera: ICamera) {
        val matrix = camera.projectionMat * camera.viewMat
        // 绘制背景
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)
        bgShader.use()
        bgBuffObj.vao.bind()
        bgShader.setMat4("uMatrix", matrix)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, bgBuffObj.count)
        bgBuffObj.vao.release()
        bgShader.release()
        // 绘制地图上的点
        posShader.use()
        posBuffObj.vao.bind()
        // 优化点在缩放很小时看不清
        var halfSize = 0.05F
        if (camera.zoom.x < 0.002)
            halfSize = 0.005F
        else if (camera.zoom.x < 0.02) {
            halfSize = camera.zoom.x / 0.02F * 0.05F
        }
        posShader.setFloat("uPointHalfWidth", halfSize)
        posShader.setMat4("uMatrix", matrix)
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0, posBuffObj.count)
        posBuffObj.vao.release()
        posShader.release()
        // 绘制地图上的线
        lineShader.use()
        lineBuffObj.vao.bind()
        lineShader.setMat4("uMatrix", matrix)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, lineBuffObj.count, GLES32.GL_UNSIGNED_INT, 0)
        lineBuffObj.vao.release()
        lineShader.release()

        GLES32.glDisable(GLES32.GL_BLEND)
    }
}