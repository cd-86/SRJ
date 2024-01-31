package com.seergroup.srj.gl.shaderProgram.UI

import android.opengl.GLES32
import com.seergroup.srj.gl.GLBuffer
import com.seergroup.srj.gl.GLShader
import com.seergroup.srj.gl.GLVertexArrayObject
import com.seergroup.srj.gl.matrix.Vec2
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Button(type: ButtonType = ButtonType.Up) : IUIElement {
    enum class ButtonType {
        Up, Down, Left, Right, LeftTurn, RightTurn
    }

    data class TexCoords(
        val topLeft: Vec2,
        val topRight: Vec2,
        val bottomLeft: Vec2,
        val bottomRight: Vec2
    ) {
        constructor(
            x0: Float,
            y0: Float,
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            x3: Float,
            y3: Float
        ) : this(Vec2(x0, y0), Vec2(x1, y1), Vec2(x2, y2), Vec2(x3, y3))
    }

    companion object {
        private val textCoordsMap = mapOf(
            ButtonType.Up to TexCoords(0f, 0f, 1f / 4f, 0f, 0f, 1f, 1f / 4f, 1f),
            ButtonType.Down to TexCoords(0f, 1f, 1f / 4f, 1f, 0f, 0f, 1f / 4f, 0f),
            ButtonType.Left to TexCoords(1f / 4f, 0f, 1f / 4f, 1f, 0f, 0f, 0f, 1f),
            ButtonType.Right to TexCoords(0f, 1f, 0f, 0f, 1f / 4f, 1f, 1f / 4f, 0f),
            ButtonType.LeftTurn to TexCoords(2f / 4f, 0f, 1f / 4f, 0f, 2f / 4f, 1f, 1f / 4f, 1f),
            ButtonType.RightTurn to TexCoords(1f / 4f, 0f, 2f / 4f, 0f, 1f / 4f, 1f, 2f / 4f, 1f)
        )

        private fun getTexCoords(type: ButtonType): TexCoords? {
            return textCoordsMap[type]
        }
    }

    var buttonType: ButtonType = type
        set(value) {
            if (field == value) return
            field = value
            isChanged = true
        }
    override var x: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            isChanged = true
        }
    override var y: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            isChanged = true
        }
    override var width: Float = 10f
        set(value) {
            if (field == value) return
            field = value
            isChanged = true
        }
    override var height: Float = 10f
        set(value) {
            if (field == value) return
            field = value
            isChanged = true
        }
    override var visible: Boolean = true

    private val vao = GLVertexArrayObject()
    private val vbo = GLBuffer()
    private val count: Int = 4
    private var isChanged = true

    constructor(x: Float, y: Float, width: Float, height: Float, type: ButtonType): this(type) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    constructor(x: Float, y: Float, type: ButtonType): this(type) {
        this.x = x
        this.y = y
    }

    init {
        vao.create()
        vbo.create()
        vao.bind()
        vbo.bind()
        vbo.allocate(20 * Float.SIZE_BYTES, GLES32.GL_DYNAMIC_DRAW) // 4 * 5 * 4 = 80
        GLShader.setVertexAttribPointer(0, 2, GLES32.GL_FLOAT, 5 * Float.SIZE_BYTES, 0)
        GLShader.enableAttributeArray(0)
        GLShader.setVertexAttribPointer(
            1,
            2,
            GLES32.GL_FLOAT,
            5 * Float.SIZE_BYTES,
            2 * Float.SIZE_BYTES
        )
        GLShader.enableAttributeArray(1)
        GLShader.setVertexAttribPointer(
            2,
            1,
            GLES32.GL_FLOAT,
            5 * Float.SIZE_BYTES,
            4 * Float.SIZE_BYTES
        )
        GLShader.enableAttributeArray(2)
        vbo.release()
        vao.release()
    }

    private fun updateBuff() {
        val tc = getTexCoords(buttonType)!!
        val d = floatArrayOf(
            x, y, tc.topLeft.x, tc.topLeft.y, 0.0f,
            x + width, y, tc.topRight.x, tc.topRight.y, 0.0f,
            x, y + height, tc.bottomLeft.x, tc.bottomLeft.y, 0.0f,
            x+width, y + height, tc.bottomRight.x, tc.bottomRight.y, 0.0f,
        )
        ByteBuffer.allocateDirect(d.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(d)
                position(0)
            }
        }.also {
            vbo.bind()
            vbo.write(0, d.size * Float.SIZE_BYTES, it)
            vbo.release()
        }
        isChanged = false
    }

    override fun press(point: Vec2) {
        TODO("Not yet implemented")
    }

    override fun release(point: Vec2) {
        TODO("Not yet implemented")
    }

    override fun move(point: Vec2) {
        TODO("Not yet implemented")
    }

    override fun draw() {
        if (isChanged)
            updateBuff()
        vao.bind()
        UIObject.shader.setFloat("uAlpha", 1f)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, count)
        vao.release()
    }

    override fun clearn() {
        vao.destroy()
        vbo.destroy()
    }

}