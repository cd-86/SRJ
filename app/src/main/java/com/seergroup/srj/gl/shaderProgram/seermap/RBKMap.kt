package com.seergroup.srj.gl.shaderProgram.seermap

import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.util.Log
import com.seergroup.srj.Global
import com.seergroup.srj.gl.GLBuffer
import com.seergroup.srj.gl.GLShader
import com.seergroup.srj.gl.GLTexture
import com.seergroup.srj.gl.GLVertexArrayObject
import com.seergroup.srj.gl.camera.ICamera
import java.nio.ByteBuffer

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

    private val meshShader: GLShader
    private val meshBuffObj = object {
        val vao = GLVertexArrayObject()
        val vbo = GLBuffer()
        val ebo = GLBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER)
        var count = 0
    }

    private val fontTexture: GLTexture
    private val mapLineTexture: GLTexture
    private val spinTexture: GLTexture
    private val tagTexture: GLTexture
    private val stationTexture: GLTexture

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
        lineShader.setVertexAttribPointer(
            1,
            4,
            GLES32.GL_FLOAT,
            6 * Float.SIZE_BYTES,
            2 * Float.SIZE_BYTES
        )
        lineShader.enableAttributeArray(1)
        lineBuffObj.vbo.release()
        lineBuffObj.vao.release()
        lineBuffObj.ebo.release()

        meshShader = GLShader(
            vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec2 aPos;
                layout (location = 1) in vec2 aTexCoord;
                layout (location = 2)  in float aType;
                layout (location = 3) in vec4 aColor;
                out vec4 vColor;
                out vec2 vTexCoord;
                out vec2 vTexCoord2;
                flat out int vType;
                flat out int vNeedTexCoord2;
                uniform mat4 uMatrix;
                void main(){
                    gl_Position = uMatrix * vec4(aPos, 0.0, 1.0);
                    vTexCoord = aTexCoord;
                    vType = int(aType);
                    if (vType == 5) {
                        vNeedTexCoord2 = int(aColor.z);
                        vTexCoord2 = aColor.xy;
                    } else {
                        vColor = aColor;
                    }
                }
                """.trimIndent(),
            fragmentSource = """
                #version 320 es
                precision mediump float;
                in vec2 vTexCoord;
                in vec2 vTexCoord2;
                in vec4 vColor;
                flat in int vType; // (0 color, 1 font, 2 line, 3 tag, 4 spin, 5 scenePoint)
                flat in int vNeedTexCoord2;
                uniform sampler2D uFontTexture;
                uniform sampler2D uLineTexture;
                uniform sampler2D uTagPosTexture;
                uniform sampler2D uSpinTexture;
                uniform sampler2D uSceneTexture;
                uniform float uFontTextureWidth;
                uniform float uFontTextureHeight;
                out vec4 FragColor;
                void main(){
                    float r;
                    switch(vType){
                        case 0:
                            FragColor = vColor;
                            break;
                        case 1:
                            r = texture(uFontTexture, vec2(vTexCoord.x / uFontTextureWidth, vTexCoord.y / uFontTextureHeight)).r;
                            FragColor = vec4(vColor.rgb, vColor.a * r);
                            break;
                        case 2:
                            r = texture(uLineTexture, vTexCoord).r;
                            FragColor = vec4(vColor.rgb, vColor.a * r);
                            break;
                        case 3:
                            FragColor = texture(uTagPosTexture, vTexCoord);
                            break;
                        case 4:
                            FragColor = texture(uSpinTexture, vTexCoord);
                            break;
                        case 5:
                            vec4 color = texture(uSceneTexture, vTexCoord);
                            if (vNeedTexCoord2 == 1) {
                                color *= texture(uSceneTexture, vTexCoord2);
                            }
                            FragColor = color;
                            break;
                        default:
                            discard;
                    };
                }
                """.trimIndent()
        )
        meshShader.link()
        meshBuffObj.vao.create()
        meshBuffObj.vbo.create()
        meshBuffObj.ebo.create()
        meshBuffObj.vao.bind()
        meshBuffObj.vbo.bind()
        meshBuffObj.ebo.bind()
        meshShader.setVertexAttribPointer(0, 2, GLES32.GL_FLOAT, 9 * Float.SIZE_BYTES, 0)
        meshShader.enableAttributeArray(0)
        meshShader.setVertexAttribPointer(
            1,
            2,
            GLES32.GL_FLOAT,
            9 * Float.SIZE_BYTES,
            2 * Float.SIZE_BYTES
        )
        meshShader.enableAttributeArray(1)
        meshShader.setVertexAttribPointer(
            2,
            1,
            GLES32.GL_FLOAT,
            9 * Float.SIZE_BYTES,
            4 * Float.SIZE_BYTES
        )
        meshShader.enableAttributeArray(2)
        meshShader.setVertexAttribPointer(
            3,
            4,
            GLES32.GL_FLOAT,
            9 * Float.SIZE_BYTES,
            5 * Float.SIZE_BYTES
        )
        meshShader.enableAttributeArray(3)
        meshBuffObj.vbo.release()
        meshBuffObj.vao.release()
        meshBuffObj.ebo.release()

        mapLineTexture = GLTexture().apply {
            val data = byteArrayOf(255.toByte(), 255.toByte(), 0.toByte())
            setImage {
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_WRAP_S,
                    GLES32.GL_REPEAT
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_WRAP_T,
                    GLES32.GL_REPEAT
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_MIN_FILTER,
                    GLES32.GL_NEAREST
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_MAG_FILTER,
                    GLES32.GL_NEAREST
                )
                GLES32.glTexImage2D(
                    GLES32.GL_TEXTURE_2D,
                    0,
                    GLES32.GL_LUMINANCE,
                    3,
                    1,
                    0,
                    GLES32.GL_LUMINANCE,
                    GLES32.GL_UNSIGNED_BYTE,
                    ByteBuffer.wrap(data)
                )
            }
        }

        fontTexture = GLTexture()

        spinTexture = GLTexture().apply {
            try {
                Global.assets?.open("texture/spin.png").use {
                    setImage(BitmapFactory.decodeStream(it))
                }
            } catch (e: Exception) {
                Log.d("SRJ-${this.javaClass.name}", e.message.toString())
            }
        }

        tagTexture = GLTexture().apply {
            try {
                Global.assets?.open("texture/tag.png").use {
                    setImage(BitmapFactory.decodeStream(it))
                }
            } catch (e: Exception) {
                Log.d("SRJ-${this.javaClass.name}", e.message.toString())
            }
        }

        stationTexture = GLTexture().apply {
            try {
                Global.assets?.open("texture/scene_station.png").use {
                    setImage(BitmapFactory.decodeStream(it))
                }
            } catch (e: Exception) {
                Log.d("SRJ-${this.javaClass.name}", e.message.toString())
            }
        }
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

        meshShader.destroy()
        meshBuffObj.vao.destroy()
        meshBuffObj.vbo.destroy()
        meshBuffObj.ebo.destroy()
    }

    fun setBound(bound: Bound, w: Float = 0F) {
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

    fun setMeshVertex(vertex: MeshVertex) {
        meshBuffObj.count = vertex.indices.size
        meshBuffObj.vbo.bind()
        meshBuffObj.vbo.allocate(vertex.data)
        meshBuffObj.vbo.release()
        meshBuffObj.ebo.bind()
        meshBuffObj.ebo.allocate(vertex.indices)
        meshBuffObj.ebo.release()
    }

    private fun updateFontTexture() {
        if (fontTexture.width == 0 || fontTexture.width != Global.fontFace!!.textureWidth() || fontTexture.height != Global.fontFace!!.textureHeight()) {
            fontTexture.width = Global.fontFace!!.textureWidth()
            fontTexture.height = Global.fontFace!!.textureHeight()
            fontTexture.setImage {
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_WRAP_S,
                    GLES32.GL_REPEAT
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_WRAP_T,
                    GLES32.GL_REPEAT
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_MIN_FILTER,
                    GLES32.GL_LINEAR_MIPMAP_LINEAR
                )
                GLES32.glTexParameteri(
                    GLES32.GL_TEXTURE_2D,
                    GLES32.GL_TEXTURE_MAG_FILTER,
                    GLES32.GL_LINEAR
                )
                GLES32.glTexImage2D(
                    GLES32.GL_TEXTURE_2D,
                    0,
                    GLES32.GL_LUMINANCE,
                    fontTexture.width,
                    fontTexture.height,
                    0,
                    GLES32.GL_LUMINANCE,
                    GLES32.GL_UNSIGNED_BYTE,
                    Global.fontFace!!.textureData()
                )
                GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)
            }
        }
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
        // 绘制地图上的网格
        meshShader.use()
        meshBuffObj.vao.bind()
        meshShader.setMat4("uMatrix", matrix)
        updateFontTexture()
        meshShader.setFloat("uFontTextureWidth", fontTexture.width.toFloat())
        meshShader.setFloat("uFontTextureHeight", fontTexture.height.toFloat())
        fontTexture.active(0)
        fontTexture.bind()
        mapLineTexture.active(1)
        mapLineTexture.bind()
        spinTexture.active(2)
        spinTexture.bind()
        tagTexture.active(3)
        tagTexture.bind()
        stationTexture.active(4)
        stationTexture.bind()
        meshShader.setInt("uFontTexture", 0)
        meshShader.setInt("uLineTexture", 1)
        meshShader.setInt("uSpinTexture", 2)
        meshShader.setInt("uTagPosTexture", 3)
        meshShader.setInt("uSceneTexture", 4)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, meshBuffObj.count, GLES32.GL_UNSIGNED_INT, 0)
        meshBuffObj.vao.release()
        meshShader.release()

        GLES32.glDisable(GLES32.GL_BLEND)
    }
}