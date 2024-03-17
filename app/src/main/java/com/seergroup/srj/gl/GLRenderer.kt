package com.seergroup.srj.gl

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import com.google.protobuf.util.JsonFormat
import com.seergroup.srj.Global
import com.seergroup.srj.gl.camera.Camera2D
import com.seergroup.srj.gl.shaderProgram.UI.Button
import com.seergroup.srj.gl.shaderProgram.UI.IUIElement
import com.seergroup.srj.gl.shaderProgram.UI.UIObject
import com.seergroup.srj.gl.shaderProgram.XYZAxis
import com.seergroup.srj.gl.shaderProgram.seermap.RBKMap
import com.seergroup.srj.gl.shaderProgram.seermap.RBKMapReader
import rbk.protocol.MessageMap
import rbk.protocol.model.MessageModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer : GLSurfaceView.Renderer {
    val camera = Camera2D()
    private val uiList = mutableListOf<IUIElement>()
    private lateinit var xyzAxis: XYZAxis
    private lateinit var rbkMap: RBKMap

    fun addUIElement(ui: IUIElement) {
        uiList.add(ui)
    }

    fun removeUIElement(ui: IUIElement) {
        uiList.forEach {
            if (it == ui) {
                it.clearn()
                return
            }
        }
        uiList.remove(ui)
    }

    fun removeUIElementAt(index: Int) {
        if (index >= 0 && index < uiList.size) {
            uiList[index].clearn()
            uiList.removeAt(index)
        }
    }

    fun clearUIElement() {
        uiList.forEach {
            it.clearn()
        }
        uiList.clear()
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)
        xyzAxis = XYZAxis()
        rbkMap = RBKMap()

        // UP
        var x = Global.scale * 100f
        var y = Global.screenHeight - Global.scale * 180f
        var width = Global.scale * 60f
        var height = Global.scale * 60f
        val btn_up = Button(x, y, width, height, Button.ButtonType.Up)
        // DOWN
        y = Global.screenHeight - Global.scale * 100f
        val btn_down = Button(x, y, width, height, Button.ButtonType.Down)
        // LEFT TURN
        x = Global.screenWidth - Global.scale * 180f
        y = Global.screenHeight - Global.scale * 140f
        val btn_left_turn = Button(x, y, width, height, Button.ButtonType.LeftTurn)
        // RIGHT TURN
        x = Global.screenWidth - Global.scale * 100f
        val btn_right_turn = Button(x, y, width, height, Button.ButtonType.RightTurn)

        addUIElement(btn_up)
        addUIElement(btn_down)
        addUIElement(btn_left_turn)
        addUIElement(btn_right_turn)

        // TEST
        if(Global.assets == null)
            return
        var map: MessageMap.Message_Map
        var model: MessageModel.Message_Model
        Global.assets!!.open("FCWL.smap").use {
            val mapBuilder = MessageMap.Message_Map.newBuilder()
            JsonFormat.parser().merge(it.readBytes().toString(Charsets.UTF_8), mapBuilder)
            map = mapBuilder.build()
        }
        Global.assets!!.open("robot.model").use {
            val modelBuilder = MessageModel.Message_Model.newBuilder()
            JsonFormat.parser().merge(it.readBytes().toString(Charsets.UTF_8), modelBuilder)
            model = modelBuilder.build()
        }
        val reader = RBKMapReader()
        reader.read(map, model)
        rbkMap.setNormalPosAndRssiPosVertex(reader.posVertex)
        rbkMap.setBound(reader.bound, 10F)
        rbkMap.setLineVertex(reader.lineVertex)
        rbkMap.setMeshVertex(reader.meshVertex)
        // TEST END
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
        rbkMap.draw(camera)
        xyzAxis.draw(camera)

        GLES32.glEnable(GLES32.GL_BLEND)
        UIObject.shader.use()
        UIObject.texture0.active()
        UIObject.texture0.bind()
        UIObject.shader.setInt("uTexture0", 0)
        UIObject.shader.setVec2("uSize", camera.width, camera.height)
        uiList.forEach {
            if (it.visible)
                it.draw()
        }
        UIObject.shader.release()
        UIObject.texture0.release()
        GLES32.glDisable(GLES32.GL_BLEND)
    }
}