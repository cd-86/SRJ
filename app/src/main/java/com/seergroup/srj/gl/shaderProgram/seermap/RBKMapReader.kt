package com.seergroup.srj.gl.shaderProgram.seermap

import com.seergroup.srj.Global
import com.seergroup.srj.gl.matrix.Color
import com.seergroup.srj.gl.matrix.OpenGLColor
import com.seergroup.srj.gl.matrix.Vec2
import com.seergroup.srj.gl.matrix.Vec3
import com.seergroup.srj.nativelib.FontFace
import rbk.protocol.MessageMap
import rbk.protocol.model.MessageModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


class RBKMapReader {
    var bound: Bound = Bound()
    var posVertex: PosVertex = PosVertex()
    var lineVertex: LineVertex = LineVertex()
    var meshVertex: MeshVertex = MeshVertex()

    fun read(rbkMap: MessageMap.Message_Map, model: MessageModel.Message_Model) {
        bound = Bound()
        posVertex = readNormalPosAndRssiPos(rbkMap.normalPosListList, rbkMap.rssiPosListList, bound)
        lineVertex = readAdvancedCurveAndAdvancedLine(
            rbkMap.advancedCurveListList,
            rbkMap.advancedLineListList
        )
        meshVertex = readMapMesh(
            model,
            rbkMap.reflectorPosListList,
            rbkMap.advancedAreaListList,
            rbkMap.advancedPointListList,
            rbkMap.binLocationsListList,
            rbkMap.tagPosListList,
            bound
        )
    }

    companion object {
        const val CURVE_PRECISION = 100
        const val CIRCLE_PRECISION = 100
        const val MESH_TYPE_COLOR = 0F
        const val MESH_TYPE_FONT = 1F
        const val MESH_TYPE_LINE = 2F
        const val MESH_TYPE_TAG = 3F
        const val MESH_TYPE_SPIN = 4F
        const val MESH_TYPE_SCENE_POINTS = 5F
        /******************************************************************************************/
        /**
         * 读取 NormalPos 和 RSSIPos，创建 points 图元的顶点数据， 如果提供 Bound 则更新地图边界信息。
         *
         * @param normalPosList 包含 NormalPos 的列表
         * @param rssiPosList 包含 RSSIPos 的列表
         * @param bound 可选的边界对象，如果提供，函数将更新该对象的xMax、yMax、xMin和yMin属性，以包含所有输入位置的范围。
         * @return 顶点数据 和 顶点的数量
         */
        fun readNormalPosAndRssiPos(
            normalPosList: List<MessageMap.Message_MapPos>,
            rssiPosList: List<MessageMap.Message_MapRSSIPos>,
            bound: Bound? = null
        ): PosVertex {
            // 初始化浮点数组，大小为normal和RSSI总数的3倍（每个顶点包含x、y、顶点类型）
            val arr = FloatArray((normalPosList.size + rssiPosList.size) * 3)
            var i = 0
            // 如果提供了边界对象，更新边界信息并填充数组
            if (bound != null) {
                normalPosList.forEach {
                    arr[i++] = it.x.toFloat()
                    arr[i++] = it.y.toFloat()
                    arr[i++] = 0F
                    bound.xMax = max(it.x.toFloat(), bound.xMax)
                    bound.yMax = max(it.y.toFloat(), bound.yMax)
                    bound.xMin = min(it.x.toFloat(), bound.xMin)
                    bound.yMin = min(it.y.toFloat(), bound.yMin)
                }
                rssiPosList.forEach {
                    arr[i++] = it.x.toFloat()
                    arr[i++] = it.y.toFloat()
                    arr[i++] = 0F
                    bound.xMax = max(it.x.toFloat(), bound.xMax)
                    bound.yMax = max(it.y.toFloat(), bound.yMax)
                    bound.xMin = min(it.x.toFloat(), bound.xMin)
                    bound.yMin = min(it.y.toFloat(), bound.yMin)
                }
            } else {
                // 如果未提供边界对象，仅填充数组
                normalPosList.forEach {
                    arr[i++] = it.x.toFloat()
                    arr[i++] = it.y.toFloat()
                    arr[i++] = 0F
                }
                rssiPosList.forEach {
                    arr[i++] = it.x.toFloat()
                    arr[i++] = it.y.toFloat()
                    arr[i++] = 0F
                }
            }
            return PosVertex(arr, arr.size / 3)
        }

        /*******************************************************************************************/
        /**
         * 读取 AdvancedCurve 和 AdvancedLine，并创建 triangles 图元顶点数据
         *
         * @param advancedCurveList 包含 AdvancedCurve 的列表
         * @param advancedLineList 包含 AdvancedLine 的列表
         * @return 顶点 和 triangles 索引
         */
        fun readAdvancedCurveAndAdvancedLine(
            advancedCurveList: List<MessageMap.Message_AdvancedCurve>,
            advancedLineList: List<MessageMap.Message_AdvancedLine>
        ): LineVertex {
            val vertexList = mutableListOf<Float>()
            val indicesList = mutableListOf<Int>()
            // AdvancedLine
            advancedLineList.forEach {
                val list = listOf(
                    Vec2(it.line.startPos.x.toFloat(), it.line.startPos.y.toFloat()),
                    Vec2(it.line.endPos.x.toFloat(), it.line.endPos.y.toFloat())
                )
                calcLineMesh(list, 0, Color(255, 0, 0, 128), vertexList, indicesList)
            }
            // AdvancedCurve
            val bezierPathMap = mutableMapOf<String, Pair<MessageMap.Message_AdvancedCurve, Int>>()
            val degenerateBezierMap =
                mutableMapOf<String, Pair<MessageMap.Message_AdvancedCurve, Int>>()
            val straightPathMap =
                mutableMapOf<String, Pair<MessageMap.Message_AdvancedCurve, Int>>()
            val arcPathMap = mutableMapOf<String, Pair<MessageMap.Message_AdvancedCurve, Int>>()
            val NURBS6Map = mutableMapOf<String, Pair<MessageMap.Message_AdvancedCurve, Int>>()
            advancedCurveList.forEach {
                val key = it.endPos.instanceName + "-" + it.startPos.instanceName
                when (it.className) {
                    "BezierPath" -> {
                        if (bezierPathMap.containsKey(key) &&
                            it.controlPos1.x == bezierPathMap[key]!!.first.controlPos2.x &&
                            it.controlPos1.y == bezierPathMap[key]!!.first.controlPos2.y &&
                            it.controlPos2.x == bezierPathMap[key]!!.first.controlPos1.x &&
                            it.controlPos2.x == bezierPathMap[key]!!.first.controlPos1.x
                        ) {
                            bezierPathMap[key] = Pair(it, 2)
                        } else {
                            bezierPathMap[it.instanceName] = Pair(it, 1)
                        }
                    }

                    "DegenerateBezier" -> {
                        if (degenerateBezierMap.containsKey(key) &&
                            it.controlPos1.x == degenerateBezierMap[key]!!.first.controlPos2.x &&
                            it.controlPos1.y == degenerateBezierMap[key]!!.first.controlPos2.y &&
                            it.controlPos2.x == degenerateBezierMap[key]!!.first.controlPos1.x &&
                            it.controlPos2.x == degenerateBezierMap[key]!!.first.controlPos1.x
                        ) {
                            degenerateBezierMap[key] = Pair(it, 2)
                        } else {
                            degenerateBezierMap[it.instanceName] = Pair(it, 1)
                        }
                    }

                    "StraightPath" -> {
                        if (straightPathMap.containsKey(key)) {
                            straightPathMap[key] = Pair(it, 2)
                        } else {
                            straightPathMap[it.instanceName] = Pair(it, 1)
                        }
                    }

                    "ArcPath" -> {
                        if (arcPathMap.containsKey(key) &&
                            it.controlPos1.x == arcPathMap[key]!!.first.controlPos1.x &&
                            it.controlPos1.y == arcPathMap[key]!!.first.controlPos1.y
                        ) {
                            arcPathMap[key] = Pair(it, 2)
                        } else {
                            arcPathMap[it.instanceName] = Pair(it, 1)
                        }
                    }

                    "NURBS6" -> {
                        if (NURBS6Map.containsKey(key) &&
                            it.controlPos1.x == arcPathMap[key]!!.first.controlPos4.x &&
                            it.controlPos1.y == arcPathMap[key]!!.first.controlPos4.y &&
                            it.controlPos2.x == arcPathMap[key]!!.first.controlPos3.x &&
                            it.controlPos2.y == arcPathMap[key]!!.first.controlPos3.y &&
                            it.controlPos3.x == arcPathMap[key]!!.first.controlPos2.x &&
                            it.controlPos3.y == arcPathMap[key]!!.first.controlPos2.y &&
                            it.controlPos4.x == arcPathMap[key]!!.first.controlPos1.x &&
                            it.controlPos4.y == arcPathMap[key]!!.first.controlPos1.y
                        ) {
                            NURBS6Map[key] = Pair(it, 2)
                        } else {
                            NURBS6Map[it.instanceName] = Pair(it, 1)
                        }
                    }
                }
            }
            // 贝塞尔
            bezierPathMap.forEach { (_, pair) ->
                val list: List<Vec2> = List<Vec2>(CURVE_PRECISION) {
                    val v = it.toFloat() / (CURVE_PRECISION.toFloat() - 1F)
                    val s = 1F - v
                    Vec2(
                        (s * s * s) * pair.first.startPos.pos.x.toFloat() +
                                3 * (s * s) * v * pair.first.controlPos1.x.toFloat() +
                                3 * s * (v * v) * pair.first.controlPos2.x.toFloat() +
                                (v * v * v) * pair.first.endPos.pos.x.toFloat(),
                        (s * s * s) * pair.first.startPos.pos.y.toFloat() +
                                3 * (s * s) * v * pair.first.controlPos1.y.toFloat() +
                                3 * s * (v * v) * pair.first.controlPos2.y.toFloat() +
                                (v * v * v) * pair.first.endPos.pos.y.toFloat()
                    )
                }
                calcLineMesh(list, pair.second, Color(170, 85, 0, 160), vertexList, indicesList)
            }
            // 高阶贝塞尔
            degenerateBezierMap.forEach { (_, pair) ->
                val list: List<Vec2> = List<Vec2>(CURVE_PRECISION) {
                    val v = it.toFloat() / (CURVE_PRECISION.toFloat() - 1F)
                    val s = 1F - v
                    Vec2(
                        (s * s * s * s * s) * pair.first.startPos.pos.x.toFloat() +
                                5 * (s * s * s * s) * v * pair.first.controlPos1.x.toFloat() +
                                10 * (s * s * s) * (v * v) * pair.first.controlPos1.x.toFloat() +
                                10 * (s * s) * (v * v * v) * pair.first.controlPos2.x.toFloat() +
                                5 * s * (v * v * v * v) * pair.first.controlPos2.x.toFloat() +
                                (v * v * v * v * v) * pair.first.endPos.pos.x.toFloat(),
                        (s * s * s * s * s) * pair.first.startPos.pos.y.toFloat() +
                                5 * (s * s * s * s) * v * pair.first.controlPos1.y.toFloat() +
                                10 * (s * s * s) * (v * v) * pair.first.controlPos1.y.toFloat() +
                                10 * (s * s) * (v * v * v) * pair.first.controlPos2.y.toFloat() +
                                5 * s * (v * v * v * v) * pair.first.controlPos2.y.toFloat() +
                                (v * v * v * v * v) * pair.first.endPos.pos.y.toFloat()
                    )
                }
                calcLineMesh(list, pair.second, Color(170, 200, 0, 160), vertexList, indicesList)
            }
            // 直线
            straightPathMap.forEach { (_, pair) ->
                val list = listOf(
                    Vec2(pair.first.startPos.pos.x.toFloat(), pair.first.startPos.pos.y.toFloat()),
                    Vec2(pair.first.endPos.pos.x.toFloat(), pair.first.endPos.pos.y.toFloat())
                )
                calcLineMesh(list, pair.second, Color(18, 150, 219, 120), vertexList, indicesList)
            }
            // 圆弧线
            arcPathMap.forEach { (_, pair) ->
                var list: List<Vec2>
                val x1 = pair.first.startPos.pos.x.toFloat()
                val y1 = pair.first.startPos.pos.y.toFloat()
                val x2 = pair.first.controlPos1.x.toFloat()
                val y2 = pair.first.controlPos1.y.toFloat()
                val x3 = pair.first.endPos.pos.x.toFloat()
                val y3 = pair.first.endPos.pos.y.toFloat()
                val A = x1 * (y2 - y3) - y1 * (x2 - x3) + x2 * y3 - x3 * y2
                if (abs(A) <= 1E-12) {
                    list = listOf(Vec2(x1, y1), Vec2(x3, y3))
                } else {
                    val x1x1py1y1 = x1 * x1 + y1 * y1
                    val x2x2py2y2 = x2 * x2 + y2 * y2
                    val x3x3py3y3 = x3 * x3 + y3 * y3

                    val B = x1x1py1y1 * (-y2 + y3) + x2x2py2y2 * (y1 - y3) + x3x3py3y3 * (y2 - y1)
                    val C = x1x1py1y1 * (x2 - x3) + x2x2py2y2 * (x3 - x1) + x3x3py3y3 * (x1 - x2)
                    val D =
                        x1x1py1y1 * (x3 * y2 - x2 * y3) + x2x2py2y2 * (x1 * y3 - x3 * y1) + x3x3py3y3 * (x2 * y1 - x1 * y2)

                    val x = -B / (2 * A)
                    val y = -C / (2 * A)
                    val r = sqrt((B * B + C * C - 4 * A * D) / (4 * A * A))

                    val theta1: Float = atan2(y1 - y, x1 - x)
                    var theta3: Float = atan2(y3 - y, x3 - x)

                    if (((x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2)) > 0) {
                        if (theta1 > theta3) theta3 = (theta3 + 2 * PI).toFloat()
                    } else {
                        if (theta1 < theta3) theta3 = (theta3 - 2 * PI).toFloat()
                    }
                    list = List<Vec2>(CURVE_PRECISION) {
                        val theta = theta3 + (theta1 - theta3) * it / (CURVE_PRECISION - 1)
                        Vec2(x + r * cos(theta), y + r * sin(theta))
                    }
                }
                calcLineMesh(list, pair.second, Color(134, 13, 214, 160), vertexList, indicesList)
            }
            // B样条线
            val N = 5
            val K = 4
            val knots = FloatArray(N + K + 1)
            NURBS6Map.forEach { (_, pair) ->
                val inPoints = arrayOf(
                    Vec3(
                        pair.first.startPos.pos.x.toFloat(),
                        pair.first.startPos.pos.y.toFloat(),
                        pair.first.startPos.pos.z.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos1.x.toFloat(),
                        pair.first.controlPos1.y.toFloat(),
                        pair.first.controlPos1.y.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos2.x.toFloat(),
                        pair.first.controlPos2.y.toFloat(),
                        pair.first.controlPos2.y.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos3.x.toFloat(),
                        pair.first.controlPos3.y.toFloat(),
                        pair.first.controlPos3.y.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos4.x.toFloat(),
                        pair.first.controlPos4.y.toFloat(),
                        pair.first.controlPos4.y.toFloat(),
                    ),
                    Vec3(
                        pair.first.endPos.pos.x.toFloat(),
                        pair.first.endPos.pos.y.toFloat(),
                        pair.first.endPos.pos.z.toFloat(),
                    ),
                )
                // generateKnots
                for (i in 0 until K) {
                    knots[i] = 0.0F
                }
                for (i in N + 1 until N + K + 1) {
                    knots[i] = 1.0F
                }
                var denominator = 0.0F
                var numerator = FloatArray(N + 2 - K)
                for (loop1 in K..N + 1) {
                    var temp = 0.0F
                    for (loop2 in loop1 - K..loop1 - 2) {
                        val diff_x: Float = inPoints[loop2 + 1].x - inPoints[loop2].x
                        val diff_y: Float = inPoints[loop2 + 1].y - inPoints[loop2].y
                        temp += sqrt(diff_x * diff_x + diff_y * diff_y)
                    }
                    denominator += temp
                    numerator[loop1] = temp
                }
                for (loop1 in K..N + 1) {
                    knots[loop1] = knots[loop1 - 1] + numerator[loop1 - K] / denominator
                }
                // generatePath
                val list = mutableListOf<Vec2>()
                val EPSILON = 1E-10F + 0.01F
                var u = 0.0F
                while (u <= 1.0F) {
                    var temp_fenzi_x = 0.0F
                    var temp_fenzi_y = 0.0F
                    var temp_fenmu = 0.0F
                    for (i in 0..N) {
                        val v = inPoints[i].z * bValue(knots, i, K, u)
                        temp_fenzi_x += inPoints[i].x * v
                        temp_fenzi_y += inPoints[i].y * v
                        temp_fenmu += v
                    }
                    if (temp_fenmu != 0.0F) {
                        list.add(Vec2(temp_fenzi_x / temp_fenmu, temp_fenzi_y / temp_fenmu))
                    }
                    u += EPSILON
                }
                list.add(0, Vec2(inPoints.first().x, inPoints.first().y))
                list.add(Vec2(inPoints.last().x, inPoints.last().y))
                calcLineMesh(list, pair.second, Color(134, 13, 214, 160), vertexList, indicesList)
            }
            return LineVertex(vertexList.toFloatArray(), indicesList.toIntArray())
        }

        private fun bValue(knots: FloatArray, i: Int, k: Int, u: Float): Float {
            if (k == 1) {
                return if (knots[i] < u && u <= knots[i + 1]) 1F else 0F
            }
            var front = 0.0F
            var after = 0.0F
            if (u - knots[i] != 0F && knots[i + k - 1] - knots[i] != 0F) {
                front = (u - knots[i]) / (knots[i + k - 1] - knots[i])
            }
            if (knots[i + k] - u != 0.0F && knots[i + k] - knots[i + 1] != 0.0F) {
                after = (knots[i + k] - u) / (knots[i + k] - knots[i + 1])
            }
            return front * bValue(knots, i, k - 1, u) + after * bValue(knots, i + 1, k - 1, u)
        }

        private fun calcLineMesh(
            points: List<Vec2>,
            way: Int,
            color: Color,
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>
        ) {
            return calcLineMesh(points, way, color.toOpenGLColor(), vertexList, indicesList)
        }

        private fun calcLineMesh(
            points: List<Vec2>,
            way: Int,
            color: OpenGLColor,
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>
        ) {
            if (points.size < 2) return
            var p1x: Float
            var p2x: Float
            var p1y: Float
            var p2y: Float
            var offsetX: Float
            var offsetY: Float
            val centerX: Float
            val centerY: Float
            var norm: Float
            var diffX: Float = (points[1].y - points[0].y) * -1
            var diffY: Float = points[1].x - points[0].x
            var indx: Int = vertexList.size / 6
            for (i in 0 until points.size - 1) {
                val tempDiffX = (points[i + 1].y - points[i].y) * -1
                val tempDiffY = points[i + 1].x - points[i].x
                diffX = (diffX + tempDiffX) / 2
                diffY = (diffY + tempDiffY) / 2
                norm = sqrt(diffX * diffX + diffY * diffY)
                offsetX = (diffX / norm * 0.03).toFloat()
                offsetY = (diffY / norm * 0.03).toFloat()
                p1x = points[i].x - offsetX / 2
                p1y = points[i].y - offsetY / 2
                p2x = p1x + offsetX
                p2y = p1y + offsetY
                vertexList.addAll(
                    arrayOf(
                        p1x, p1y, color.r, color.g, color.b, color.a,
                        p2x, p2y, color.r, color.g, color.b, color.a
                    )
                )
                diffX = tempDiffX
                diffY = tempDiffY
            }
            // 最后一个矩形
            norm = sqrt(diffX * diffX + diffY * diffY)
            offsetX = (diffX / norm * 0.03).toFloat()
            offsetY = (diffY / norm * 0.03).toFloat()
            p1x = points.last().x - offsetX / 2
            p1y = points.last().y - offsetY / 2
            p2x = p1x + offsetX
            p2y = p1y + offsetY
            vertexList.addAll(
                arrayOf(
                    p1x, p1y, color.r, color.g, color.b, color.a,
                    p2x, p2y, color.r, color.g, color.b, color.a
                )
            )
            // 索引
            for (i in 0 until vertexList.size / 6 - indx - 2) {
                indicesList.addAll(
                    arrayOf(indx + i, indx + i + 1, indx + i + 2)
                )
            }
            // 箭头
            if (way == 0) return
            indx = vertexList.size / 6
            val i = if (points.size == 2) 0 else points.size / 2
            centerX = (points[i].x + points[i + 1].x) / 2
            centerY = (points[i].y + points[i + 1].y) / 2
            diffX = (points[i].y - points[i + 1].y) * -1
            diffY = points[i].x - points[i + 1].x
            norm = sqrt(diffX * diffX + diffY * diffY)
            offsetX = diffX / norm * 0.06F
            offsetY = diffY / norm * 0.06F
            p1x = centerX + offsetX
            p1y = centerY + offsetY
            p2x = centerX - offsetX
            p2y = centerY - offsetY
            vertexList.addAll(
                arrayOf(
                    p1x, p1y, 1.0F, 0.0F, 0.0F, 0.5F,
                    p2x, p2y, 1.0F, 0.0F, 0.0F, 0.5F
                )
            )
            //
            offsetX = diffY * -1 / norm * 0.08F
            offsetY = diffX / norm * 0.08F
            vertexList.addAll(
                arrayOf(
                    centerX + offsetX, centerY + offsetY, 1.0F, 0.0F, 0.0F, 0.5F
                )
            )
            indicesList.addAll(
                arrayOf(indx, indx + 1, indx + 2)
            )
            if (way == 2) {
                vertexList.addAll(
                    arrayOf(
                        centerX - offsetX, centerY - offsetY, 1.0F, 0.0F, 0.0F, 0.5F
                    )
                )
                indicesList.addAll(
                    arrayOf(indx, indx + 1, indx + 3)
                )
            }
        }

        /*******************************************************************************************/
        /**
         * 读取 map 中其他的数据， 根据 模型 创建 triangles 图元的顶点数据， 如果提供 Bound 则更新地图边界信息。
         *
         * @param model 机器人 模型
         * @param reflectorPosList 包含 reflectorPos 的列表
         * @param advancedAreaList 包含 advancedArea 的列表
         * @param advancedPointList 包含 advancedPoint 的列表
         * @param binLocationsList 包含 binLocations 的列表
         * @param tagPosList 包含 tagPos 的列表
         * @param bound 可选的边界对象，如果提供，函数将更新该对象的xMax、yMax、xMin和yMin属性，以包含所有输入位置的范围。
         * @return 顶点 和 triangles 索引
         */
        fun readMapMesh(
            model: MessageModel.Message_Model,
            reflectorPosList: List<MessageMap.Message_ReflectorPos>,
            advancedAreaList: List<MessageMap.Message_AdvancedArea>,
            advancedPointList: List<MessageMap.Message_AdvancedPoint>,
            binLocationsList: List<MessageMap.Message_BinLocations>,
            tagPosList: List<MessageMap.Message_tagPos>,
            bound: Bound? = null
        ): MeshVertex {
            val vertexList = mutableListOf<Float>()
            val indicesList = mutableListOf<Int>()
            // reflectorPos
            reflectorPosMesh(vertexList, indicesList, reflectorPosList)
            // AdvancedArea
            advancedAreaMesh(vertexList, indicesList, advancedAreaList)
            // AdvancedPoint
            advancedPointMesh(vertexList, indicesList, advancedPointList)
            // BinLocations
            binLocationsMesh(vertexList, indicesList, binLocationsList)
            // TagPos
            tagPosMesh(vertexList, indicesList, tagPosList)

            return MeshVertex(vertexList.toFloatArray(), indicesList.toIntArray())
        }

        private fun reflectorPosMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            reflectorPosList: List<MessageMap.Message_ReflectorPos>
        ) {
            val pen = Color(1, 81, 152, 150).toOpenGLColor()
            val brush = Color(255, 0, 0, 150).toOpenGLColor()
            reflectorPosList.forEach {
                val r = it.width.toFloat() / 2;
                val indx: Int = vertexList.size / 9
                if (it.type == "plane") {
                    val x = it.x.toFloat()
                    val y = it.y.toFloat()

                    val p1x = x - r
                    val p1y = y + r
                    val p2x = x + r
                    val p2y = p1y
                    val p3x = p2x
                    val p3y = y - r
                    val p4x = p1x
                    val p4y = p3y

                    val p5x = p1x - 0.005F
                    val p5y = p1y + 0.005F
                    val p6x = p2x + 0.005F
                    val p6y = p5y
                    val p7x = p6x
                    val p7y = p3y - 0.005F
                    val p8x = p5x
                    val p8y = p7y

                    val p9x = p1x + 0.005F
                    val p9y = p1x - 0.005F
                    val p10x = p2x - 0.005F
                    val p10y = p9y
                    val p11x = p10x
                    val p11y = p3y + 0.005F
                    val p12x = p9x
                    val p12y = p11y

                    vertexList.addAll(
                        arrayOf(
                            p1x, p1y, 0F, 0F, 0F, brush.r, brush.g, brush.b, brush.a,
                            p2x, p2y, 0F, 0F, 0F, brush.r, brush.g, brush.b, brush.a,
                            p3x, p3y, 0F, 0F, 0F, brush.r, brush.g, brush.b, brush.a,
                            p4x, p4y, 0F, 0F, 0F, brush.r, brush.g, brush.b, brush.a,

                            p5x, p5y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p6x, p6y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p7x, p7y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p8x, p8y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,

                            p9x, p9y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p10x, p10y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p11x, p11y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                            p12x, p12y, 0F, 0F, 0F, pen.r, pen.g, pen.b, pen.a,
                        )
                    )
                    indicesList.addAll(
                        arrayOf(
                            indx, indx + 1, indx + 2, indx, indx + 2, indx + 3,
                            indx + 4, indx + 5, indx + 8, indx + 5, indx + 8, indx + 9,
                            indx + 5, indx + 9, indx + 10, indx + 6, indx + 5, indx + 10,
                            indx + 6, indx + 7, indx + 10, indx + 7, indx + 10, indx + 11,
                            indx + 4, indx + 8, indx + 11, indx + 4, indx + 7, indx + 11
                        )
                    )

                } else { // cylinder
                    val d = FloatArray(CIRCLE_PRECISION * 27) // 3 * 9
                    val r2 = r - 0.005F
                    val r3 = r + 0.005F
                    for (i in 0 until CIRCLE_PRECISION) {
                        val v: Float = (i / (CIRCLE_PRECISION - 1) * PI * 2).toFloat()
                        val c: Float = cos(v)
                        val s: Float = sin(v)
                        var j = i * 9
                        d[j] = it.x.toFloat() + r * c
                        d[j + 1] = it.y.toFloat() + r * s
                        d[j + 5] = brush.r
                        d[j + 6] = brush.g
                        d[j + 7] = brush.b
                        d[j + 8] = brush.a
                        j = (i + CIRCLE_PRECISION) * 9
                        d[j] = it.x.toFloat() + r2 * c
                        d[j + 1] = it.y.toFloat() + r2 * s
                        d[j + 5] = pen.r
                        d[j + 6] = pen.g
                        d[j + 7] = pen.b
                        d[j + 8] = pen.a
                        j = (i + CIRCLE_PRECISION + CIRCLE_PRECISION) * 9
                        d[j] = it.x.toFloat() + r3 * c
                        d[j + 1] = it.y.toFloat() + r3 * s
                        d[j + 5] = pen.r
                        d[j + 6] = pen.g
                        d[j + 7] = pen.b
                        d[j + 8] = pen.a
                    }
                    vertexList.addAll(d.toList())
                    for (i in 1 until CIRCLE_PRECISION - 1) {
                        indicesList.addAll(arrayOf(indx, indx + i, indx + i + 1))
                    }
                    for (i in 0 until CIRCLE_PRECISION) {
                        val inner = indx + i + CIRCLE_PRECISION
                        val outer = inner + CIRCLE_PRECISION
                        indicesList.addAll(
                            arrayOf(inner, inner + 1, outer, inner + 1, outer, outer + 1)
                        )
                    }
                }
            }
        }

        private fun advancedAreaMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            advancedAreaList: List<MessageMap.Message_AdvancedArea>
        ) {
            advancedAreaList.forEach {
                val indx: Int = vertexList.size / 9
                val c = cos(it.dir).toFloat()
                val s = sin(it.dir).toFloat()
                val x1 = it.posGroupList[0].x.toFloat()
                val y1 = it.posGroupList[0].y.toFloat()
                val x2 = it.posGroupList[1].x.toFloat()
                val y2 = it.posGroupList[1].y.toFloat()
                val x3 = it.posGroupList[2].x.toFloat()
                val y3 = it.posGroupList[2].y.toFloat()
                val x4 = it.posGroupList[3].x.toFloat()
                val y4 = it.posGroupList[3].y.toFloat()
                val x5 = x1 + 0.005F * c - 0.005F * s
                val y5 = y1 + 0.005F * s + 0.005F * c
                val x6 = x1 - 0.005F * c - (-0.005F) * s
                val y6 = y1 - 0.005F * s + (-0.005F) * c
                val x7 = x2 - 0.005F * c - 0.005F * s
                val y7 = y2 - 0.005F * s + 0.005F * c
                val x8 = x2 + 0.005F * c - (-0.005F) * s
                val y8 = y2 + 0.005F * s + (-0.005F) * c
                val x9 = x3 + 0.005F * c - 0.005F * s
                val y9 = y3 + 0.005F * s + 0.005F * c
                val x10 = x3 - 0.005F * c - (-0.005F) * s
                val y10 = y3 - 0.005F * s + (-0.005F) * c
                val x11 = x4 - 0.005F * c - 0.005F * s
                val y11 = y4 - 0.005F * s + 0.005F * c
                val x12 = x4 + 0.005F * c - (-0.005F) * s
                val y12 = y4 + 0.005F * s + (-0.005F) * c

                val brush = Color(
                    it.attribute.colorBrush ushr 24,
                    it.attribute.colorBrush ushr 16 and 0xFF,
                    it.attribute.colorBrush ushr 8 and 0xFF,
                    25
                ).toOpenGLColor()
                val pen = Color(
                    it.attribute.colorBrush ushr 24,
                    it.attribute.colorBrush ushr 16 and 0xFF,
                    it.attribute.colorBrush ushr 8 and 0xFF,
                    25
                ).toOpenGLColor()

                vertexList.addAll(
                    arrayOf(
                        x1, y1, 0F, 0F, MESH_TYPE_COLOR, brush.r, brush.g, brush.b, brush.a,
                        x2, y2, 0F, 0F, MESH_TYPE_COLOR, brush.r, brush.g, brush.b, brush.a,
                        x3, y3, 0F, 0F, MESH_TYPE_COLOR, brush.r, brush.g, brush.b, brush.a,
                        x4, y4, 0F, 0F, MESH_TYPE_COLOR, brush.r, brush.g, brush.b, brush.a,
                        x5, y5, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x6, y6, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x7, y7, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x8, y8, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x9, y9, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x10, y10, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x11, y11, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x12, y12, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                    )
                )
                indicesList.addAll(
                    arrayOf(
                        indx, indx + 1, indx + 2, indx, indx + 2, indx + 3,
                        indx + 4, indx + 5, indx + 6, indx + 5, indx + 6, indx + 7,
                        indx + 6, indx + 7, indx + 9, indx + 7, indx + 8, indx + 9,
                        indx + 8, indx + 9, indx + 11, indx + 8, indx + 10, indx + 11,
                        indx + 10, indx + 11, indx + 4, indx + 10, indx + 4, indx + 5
                    )
                )
                val name =
                    if (it.className == "AreaDescription") it.desc.toString() else it.className + it.instanceName
                val x = (x1 + x2 + x3 + x4) / 4
                val y = (y1 + y2 + y3 + y4) / 4
                val fv = Global.fontFace!!.getVertexData(
                    name,
                    x,
                    y,
                    FontFace.Align.Center,
                    indx + 12,
                    0.003F
                )
                fv.FontTexCoordList.forEach { v ->
                    vertexList.addAll(
                        arrayOf(
                            v.x,
                            v.y,
                            v.xTexCoord,
                            v.yTexCoord,
                            MESH_TYPE_FONT,
                            pen.r,
                            pen.g,
                            pen.b,
                            pen.a
                        )
                    )
                }
                indicesList.addAll(fv.indices)
            }
        }

        private fun advancedPointMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            advancedPointList: List<MessageMap.Message_AdvancedPoint>
        ) {
// TODO
        }

        private fun binLocationsMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            binLocationsList: List<MessageMap.Message_BinLocations>
        ) {
// TODO
        }

        private fun tagPosMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            tagPosList: List<MessageMap.Message_tagPos>
        ) {
// TODO
        }
    }

}