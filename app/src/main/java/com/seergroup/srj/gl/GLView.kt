package com.seergroup.srj.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.google.protobuf.util.JsonFormat
import com.seergroup.srj.Global
import com.seergroup.srj.gl.matrix.Vec2
import com.seergroup.srj.nativelib.FontFace
import kotlin.math.sqrt

class GLView(context: Context) : GLSurfaceView(context) {
    val touchMap = hashMapOf<Int, Vec2>()
    private val renderer: GLRenderer

    init {
        setEGLContextClientVersion(3)
        renderer = GLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        // TEST
        val f = FontFace()
        f.destroy()
        Log.d("TAG-MessageMap", ":1 ")
        Global.assets?.open("bosch.smap")?.also {
            Log.d("TAG-MessageMap", ":2 ")
            var mapBuilder = rbk.protocol.MessageMap.Message_Map.newBuilder()
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(it.readBytes().toString(Charsets.UTF_8), mapBuilder)
                val map = mapBuilder.build()
                for (p in map.advancedPointListList) {
                    Log.d("TAG-MessageMap", ": ${p.instanceName}")
                }
            } catch (e: Exception) {
                Log.d("TAG-MessageMap", "Exception: ${e.message}")
            }
        }


        //TEST END
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    touchMap[id] = Vec2(event.getX(i), event.getY(i))
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when (event.pointerCount) {
                    1 -> {  // 单点
                        val id = event.getPointerId(0)
                        val p = touchMap[id] ?: return true
                        val x = event.getX(0)
                        val y = event.getY(0)
                        val dx = x - p.x
                        val dy = y - p.y
                        renderer.camera.move(-dx, dy)
                        p.x = x
                        p.y = y
                        requestRender()
                    }

                    else -> {  // 大于1点，则取其中两点
                        val points = mutableListOf<Pair<Vec2, Vec2>>()
                        for (i in 0 until event.pointerCount) {
                            val id = event.getPointerId(i)
                            val p = touchMap[id] ?: continue
                            val newP = Vec2()
                            event.getX(i).let { newP.x = it }
                            event.getY(i).let { newP.y = it }
                            points.add(Pair(p, newP))
                        }
                        if (points.size < 2) return true

                        val p1 = points[0].first
                        val p2 = points[1].first
                        val p = p1 - p2
                        val d = sqrt(p.x * p.x + p.y * p.y)

                        val np1 = points[0].second
                        val np2 = points[1].second
                        val np = np1 - np2
                        val nd = sqrt(np.x * np.x + np.y * np.y)

                        val scale = d / nd
                        renderer.camera.scale(scale)
//                        renderer.camera.move(-np1.x - p1.x, np1.y - p1.y)
                        p1.setValue(np1.x, np1.y)
                        p2.setValue(np2.x, np2.y)
                        requestRender()
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
//                for (i in 0 until event.pointerCount) {
//                }
            }
        }
        return true
    }
}