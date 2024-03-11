package com.seergroup.srj.gl.shaderProgram.seermap

import com.seergroup.srj.gl.matrix.Color
import com.seergroup.srj.gl.matrix.Vec2
import com.seergroup.srj.gl.matrix.Vec3
import com.seergroup.srj.gl.matrix.Vec4
import rbk.protocol.MessageMap
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

    fun read(rbkMap: MessageMap.Message_Map) {
        bound = Bound()
        posVertex = readNormalPosAndRssiPos(rbkMap.normalPosListList, rbkMap.rssiPosListList, bound)
        lineVertex = readAdvancedCurveAndAdvancedLine(
            rbkMap.advancedCurveListList,
            rbkMap.advancedLineListList
        )
    }

    companion object {
        const val CURVE_PRECISION = 100
        /******************************************************************************************/
        /**
         * 读取 NormalPos 和 RSSIPos，并将它们组合到一个浮点数组中， 此函数还可以根据提供的边界对象更新边界信息。
         *
         * @param normalPosList 包含 NormalPos 的列表
         * @param rssiPosList 包含 RSSIPos 的列表
         * @param bound 可选的边界对象，如果提供，函数将更新该对象的xMax、yMax、xMin和yMin属性，以包含所有输入位置的范围。
         * @return PosVertex
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
         * 读取 AdvancedCurve 和 AdvancedLine，并将它们组合到一个浮点数组中。
         *
         * @param advancedCurveList 包含 AdvancedCurve 的列表
         * @param advancedLineList 包含 AdvancedLine 的列表
         * @return LineVertex
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
            color: Vec4,
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
                        p1x, p1y, color.x, color.y, color.z, color.w,
                        p2x, p2y, color.x, color.y, color.z, color.w
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
                    p1x, p1y, color.x, color.y, color.z, color.w,
                    p2x, p2y, color.x, color.y, color.z, color.w
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

    }

}