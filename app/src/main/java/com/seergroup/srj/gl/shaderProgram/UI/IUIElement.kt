package com.seergroup.srj.gl.shaderProgram.UI

import com.seergroup.srj.gl.matrix.Vec2


interface IUIElement {
    var x: Float
    var y: Float
    var width: Float
    var height: Float
    var visible: Boolean

    fun press(point: Vec2)
    fun release(point: Vec2)
    fun move(point: Vec2)
    fun draw()
    fun clearn()
}