package com.seergroup.srj.gl.shaderProgram.UI

import android.graphics.BitmapFactory
import android.util.Log
import com.seergroup.srj.Global
import com.seergroup.srj.gl.GLShader
import com.seergroup.srj.gl.GLTexture

object UIObject {

    val shader = GLShader(
        vertexSource = """
                #version 320 es
                precision mediump float;
                layout (location = 0) in vec2 aPos;
                layout (location = 1) in vec2 aTexCoord;
                layout (location = 2) in float aTextureIndex;
                uniform vec2 uSize;
                out vec2 vTexCoord;
                flat out int vTextureIndex;
                void main()
                {
                    gl_Position = vec4(aPos.x / uSize.x * 2.0 - 1.0, (1.0 - aPos.y / uSize.y) * 2.0 - 1.0, 0.0, 1.0);
                    vTexCoord = aTexCoord;
                    vTextureIndex = int(aTextureIndex);
                }
                """.trimIndent(), fragmentSource = """
                #version 320 es
                precision mediump float;
                uniform sampler2D uTexture0;
                uniform sampler2D uTexture1;
                uniform float uAlpha;
                in vec2 vTexCoord;
                flat in int vTextureIndex;
                out vec4 FragColor;
                void main(){
                    if(vTextureIndex == 0){
                        FragColor = texture(uTexture0, vTexCoord);
                    } else if(vTextureIndex == 1){
                        FragColor = texture(uTexture1, vTexCoord);
                    }
                    FragColor.a *= uAlpha;
                }
                """.trimIndent()
    ).apply { link() }

    val texture0 = GLTexture().apply {
        try {
            Global.assets?.open("texture/touch_button.png").use {
                setImage(BitmapFactory.decodeStream(it))
            }
        } catch (e:Exception){
            Log.d("SRJ-${this.javaClass.name}", e.message.toString())
        }
    }

    fun destroy() {
        texture0.destroy()
        shader.destroy()
    }
}