package com.seergroup.srj.gl.shaderProgram.seermap

data class PosVertex(
    val data: FloatArray = FloatArray(0),
    val vertexCount: Int = 0
)
data class LineVertex(
    val data: FloatArray = FloatArray(0),
    val indices: IntArray = IntArray(0)
)

typealias MeshVertex = LineVertex