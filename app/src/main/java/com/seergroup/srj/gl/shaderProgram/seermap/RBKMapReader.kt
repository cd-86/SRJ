package com.seergroup.srj.gl.shaderProgram.seermap

import android.util.Log
import com.google.gson.Gson
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
import kotlin.math.acos
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

    internal data class JSPoint(val x: Float = 0F, val y: Float = 0F)

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
        val STATION_COLOR_MAP = mapOf(
            "LocationMark" to OpenGLColor(1F, 0.785F, 0.588F, 0.47F),
            "ActionPoint" to OpenGLColor(0.75F, 0.985F, 0.286F, 0.47F),
            "ChargePoint" to OpenGLColor(0.52F, 0.99F, 0.29F, 0.47F),
            "ParkPoint" to OpenGLColor(0.6F, 0.71F, 0.96F, 0.47F),
            "SwitchMap" to OpenGLColor(0.8F, 0.8F, 0.99F, 0.47F),
            "HomeRegion" to OpenGLColor(0.4F, 0.55F, 0.73F, 0.47F),
            "TransFerLocation" to OpenGLColor(0.99F, 0.44F, 0.99F, 0.47F),
            "WorkingLocation" to OpenGLColor(0.8F, 0.62F, 0.44F, 0.47F)
        )

        // TopLeft.x, TopLeft.y, TopRight.x, TopRight.y, BottomRight.x, BottomRight.y, BottomLeft.x, BottomLeft.y
        val STATION_TEXCOORD_MAP = mapOf(
            "LocationMark" to floatArrayOf(0F / 9F, 0F, 1F / 9F, 0F, 1F / 9F, 1F, 0F / 9F, 1F),
            "ActionPoint" to floatArrayOf(1F / 9F, 0F, 2F / 9F, 0F, 2F / 9F, 1F, 1F / 9F, 1F),
            "ChargePoint" to floatArrayOf(2F / 9F, 0F, 3F / 9F, 0F, 3F / 9F, 1F, 2F / 9F, 1F),
            "ParkPoint" to floatArrayOf(3F / 9F, 0F, 4F / 9F, 0F, 4F / 9F, 1F, 3F / 9F, 1F),
            "SwitchMap" to floatArrayOf(4F / 9F, 0F, 5F / 9F, 0F, 5F / 9F, 1F, 4F / 9F, 1F),
            "HomeRegion" to floatArrayOf(5F / 9F, 0F, 6F / 9F, 0F, 6F / 9F, 1F, 5F / 9F, 1F),
            "TransFerLocation" to floatArrayOf(6F / 9F, 0F, 7F / 9F, 0F, 7F / 9F, 1F, 6F / 9F, 1F),
            "WorkingLocation" to floatArrayOf(7F / 9F, 0F, 8F / 9F, 0F, 8F / 9F, 1F, 7F / 9F, 1F),
        )
        val AR_TEXCOORD = floatArrayOf(8F / 9F, 0F, 9F / 9F, 0F, 9F / 9F, 1F, 8F / 9F, 1F)

        val TAG_TEXCOORD_3D = floatArrayOf(0F, 0F, 0.4F, 0F, 0.4F, 1F, 0F, 1F)
        val TAG_TEXCOORD_2D = floatArrayOf(0.4F, 0F, 1F, 0F, 1F, 1F, 0.4F, 1F)
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
                    arr[i++] = 1F
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
                    arr[i++] = 1F
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
                val color = if (it.className == "FeatureLine")
                    OpenGLColor(1F, 0F, 1F, 0.5F) else OpenGLColor(1F, 0F, 0F, 0.5F)
                calcLineMesh(list, 0, color, vertexList, indicesList)
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
                            it.controlPos1.x == NURBS6Map[key]!!.first.controlPos4.x &&
                            it.controlPos1.y == NURBS6Map[key]!!.first.controlPos4.y &&
                            it.controlPos2.x == NURBS6Map[key]!!.first.controlPos3.x &&
                            it.controlPos2.y == NURBS6Map[key]!!.first.controlPos3.y &&
                            it.controlPos3.x == NURBS6Map[key]!!.first.controlPos2.x &&
                            it.controlPos3.y == NURBS6Map[key]!!.first.controlPos2.y &&
                            it.controlPos4.x == NURBS6Map[key]!!.first.controlPos1.x &&
                            it.controlPos4.y == NURBS6Map[key]!!.first.controlPos1.y
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
                        pair.first.controlPos1.z.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos2.x.toFloat(),
                        pair.first.controlPos2.y.toFloat(),
                        pair.first.controlPos2.z.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos3.x.toFloat(),
                        pair.first.controlPos3.y.toFloat(),
                        pair.first.controlPos3.z.toFloat(),
                    ),
                    Vec3(
                        pair.first.controlPos4.x.toFloat(),
                        pair.first.controlPos4.y.toFloat(),
                        pair.first.controlPos4.z.toFloat(),
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
                    numerator[loop1 - K] = temp
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
                calcLineMesh(list, pair.second, Color(0, 255, 246, 160), vertexList, indicesList)
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
            reflectorPosMesh(vertexList, indicesList, reflectorPosList, bound)
            // AdvancedArea
            advancedAreaMesh(vertexList, indicesList, advancedAreaList, bound)
            val robotShape = RBKRobotShape(model)
            // AdvancedPoint
            advancedPointMesh(vertexList, indicesList, advancedPointList, robotShape, bound)
            // BinLocations
            binLocationsMesh(vertexList, indicesList, binLocationsList, bound)
            // TagPos
            tagPosMesh(vertexList, indicesList, tagPosList, bound)

            return MeshVertex(vertexList.toFloatArray(), indicesList.toIntArray())
        }

        private fun reflectorPosMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            reflectorPosList: List<MessageMap.Message_ReflectorPos>,
            bound: Bound? = null
        ) {
            val pen = Color(1, 81, 152, 150).toOpenGLColor()
            val brush = Color(255, 0, 0, 150).toOpenGLColor()
            reflectorPosList.forEach {
                val r = it.width.toFloat() / 2
                val x = it.x.toFloat()
                val y = it.y.toFloat()
                val indx: Int = vertexList.size / 9
                if (it.type == "plane") {
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
                    val p9y = p1y - 0.005F
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
                    if (bound != null) {
                        bound.xMax = max(bound.xMax, p6x)
                        bound.yMax = max(bound.yMax, p5y)
                        bound.xMin = min(bound.xMin, p5x)
                        bound.yMin = min(bound.yMin, p7y)
                    }

                } else { // cylinder
                    val d = FloatArray(CIRCLE_PRECISION * 27) // 3 * 9
                    val r2 = r - 0.005F
                    val r3 = r + 0.005F
                    for (i in 0 until CIRCLE_PRECISION) {
                        val v: Float =
                            (i.toFloat() / (CIRCLE_PRECISION.toFloat() - 1F) * PI * 2F).toFloat()
                        val c: Float = cos(v)
                        val s: Float = sin(v)
                        var j = i * 9
                        d[j] = x + r * c
                        d[j + 1] = y + r * s
                        d[j + 5] = brush.r
                        d[j + 6] = brush.g
                        d[j + 7] = brush.b
                        d[j + 8] = brush.a
                        j = (i + CIRCLE_PRECISION) * 9
                        d[j] = x + r2 * c
                        d[j + 1] = y + r2 * s
                        d[j + 5] = pen.r
                        d[j + 6] = pen.g
                        d[j + 7] = pen.b
                        d[j + 8] = pen.a
                        j = (i + CIRCLE_PRECISION + CIRCLE_PRECISION) * 9
                        d[j] = x + r3 * c
                        d[j + 1] = y + r3 * s
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
                    if (bound != null) {
                        bound.xMax = max(bound.xMax, x + r3)
                        bound.yMax = max(bound.yMax, y + r3)
                        bound.xMin = min(bound.xMin, x - r3)
                        bound.yMin = min(bound.yMin, y - r3)
                    }
                }
            }
        }

        private fun advancedAreaMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            advancedAreaList: List<MessageMap.Message_AdvancedArea>,
            bound: Bound? = null
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
                    it.attribute.colorBrush and 0xFF,
                    it.attribute.colorBrush ushr 8 and 0xFF,
                    it.attribute.colorBrush ushr 16 and 0xFF,
                    25,
//                    it.attribute.colorBrush ushr 24 and 0xFF,
                ).toOpenGLColor()
                val pen = Color(
                    it.attribute.colorPen and 0xFF,
                    it.attribute.colorPen ushr 8 and 0xFF,
                    it.attribute.colorPen ushr 16 and 0xFF,
                    100,
//                    it.attribute.colorPen ushr 24 and 0xFF,
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
                if (bound != null) {
                    bound.xMax = maxOf(bound.xMax, x1, x2, x3, x4)
                    bound.yMax = maxOf(bound.yMax, y1, y2, y3, y4)
                    bound.xMin = minOf(bound.xMin, x1, x2, x3, x4)
                    bound.yMin = minOf(bound.yMin, y1, y2, y3, y4)
                }

                val name =
                    if (it.className == "AreaDescription") it.desc.toStringUtf8() else it.className + it.instanceName
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
                fv.fontTexCoordList.forEach { v ->
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
            advancedPointList: List<MessageMap.Message_AdvancedPoint>,
            robotShape: RBKRobotShape,
            bound: Bound? = null
        ) {
            if (robotShape.shapeType == RBKRobotShape.ShapeType.RECTANGLE) {
                val w = robotShape.width / 2F
                val arrowW = robotShape.head / 5F
                advancedPointList.forEach {
                    val x = it.pos.x.toFloat()
                    val y = it.pos.y.toFloat()
                    val c = cos(it.dir.toFloat())
                    val s = sin(it.dir.toFloat())
                    var p1x: Float = robotShape.head * c + w * s + x
                    var p1y: Float = robotShape.head * s - w * c + y
                    var p2x: Float = robotShape.head * c - w * s + x
                    var p2y: Float = robotShape.head * s + w * c + y
                    var p3x: Float = -robotShape.tail * c - w * s + x
                    var p3y: Float = -robotShape.tail * s + w * c + y
                    var p4x: Float = -robotShape.tail * c + w * s + x
                    var p4y: Float = -robotShape.tail * s - w * c + y
                    var indx: Int = vertexList.size / 9
                    val color = STATION_COLOR_MAP.getOrDefault(
                        it.className,
                        STATION_COLOR_MAP["LocationMark"]
                    )!!
                    vertexList.addAll(
                        arrayOf(
                            p1x, p1y, 0F, 0F, MESH_TYPE_COLOR, color.r, color.g, color.b, color.a,
                            p2x, p2y, 0F, 0F, MESH_TYPE_COLOR, color.r, color.g, color.b, color.a,
                            p3x, p3y, 0F, 0F, MESH_TYPE_COLOR, color.r, color.g, color.b, color.a,
                            p4x, p4y, 0F, 0F, MESH_TYPE_COLOR, color.r, color.g, color.b, color.a,
                        )
                    )
                    indicesList.addAll(
                        arrayOf(
                            indx, indx + 1, indx + 2, indx, indx + 2, indx + 3
                        )
                    )
                    indx += 4
                    if (bound != null) {
                        bound.xMax = maxOf(bound.xMax, p1x, p2x, p3x, p4x)
                        bound.yMax = maxOf(bound.yMax, p1y, p2y, p3y, p4y)
                        bound.xMin = minOf(bound.xMin, p1x, p2x, p3x, p4x)
                        bound.yMin = minOf(bound.yMin, p1y, p2y, p3y, p4y)
                    }
                    for (p in it.propertyList) {
                        if (p.key == "spin" && p.boolValue) {
                            p1x = x - 0.115F
                            p1y = y + 0.115F
                            p2x = x + 0.115F
                            p2y = p1y
                            p3x = p2x
                            p3y = y - 0.115F
                            p4x = p1x
                            p4y = p3y
                            vertexList.addAll(
                                arrayOf(
                                    p1x, p1y, 0F, 0F, MESH_TYPE_SPIN, 1F, 0F, 0F, 0.5F,
                                    p2x, p2y, 1F, 0F, MESH_TYPE_SPIN, 1F, 0F, 0F, 0.5F,
                                    p3x, p3y, 1F, 1F, MESH_TYPE_SPIN, 1F, 0F, 0F, 0.5F,
                                    p4x, p4y, 0F, 1F, MESH_TYPE_SPIN, 1F, 0F, 0F, 0.5F,
                                )
                            )
                            indicesList.addAll(
                                arrayOf(
                                    indx, indx + 1, indx + 2, indx, indx + 2, indx + 3
                                )
                            )
                            indx += 4
                            break
                        }
                    }
                    var p5x: Float = -0.115F * c - 0.015F * s + x
                    var p5y: Float = -0.115F * s + 0.015F * c + y
                    var p6x: Float = 0.115F * c - 0.015F * s + x
                    var p6y: Float = 0.115F * s + 0.015F * c + y
                    var p7x: Float = 0.115F * c + 0.015F * s + x
                    var p7y: Float = 0.115F * s - 0.015F * c + y
                    var p8x: Float = -0.115F * c + 0.015F * s + x
                    var p8y: Float = -0.115F * s - 0.015F * c + y

                    var p9x: Float = -0.015F * c - 0.115F * s + x
                    var p9y: Float = -0.015F * s + 0.115F * c + y
                    var p10x: Float = 0.015F * c - 0.115F * s + x
                    var p10y: Float = 0.015F * s + 0.115F * c + y
                    var p11x: Float = 0.015F * c + 0.115F * s + x
                    var p11y: Float = 0.015F * s - 0.115F * c + y
                    var p12x: Float = -0.015F * c + 0.115F * s + x
                    var p12y: Float = -0.015F * s - 0.115F * c + y

                    vertexList.addAll(
                        arrayOf(
                            p5x, p5y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p6x, p6y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p7x, p7y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p8x, p8y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p9x, p9y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p10x, p10y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p11x, p11y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                            p12x, p12y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.5F,
                        )
                    )
                    indicesList.addAll(
                        arrayOf(
                            indx, indx + 1, indx + 2, indx, indx + 2, indx + 3,
                            indx + 4, indx + 5, indx + 6, indx + 4, indx + 6, indx + 7
                        )
                    )
                    indx += 8

                    p1x = (robotShape.head + 0.005F) * c - (w + 0.005F) * s + x
                    p1y = (robotShape.head + 0.005F) * s + (w + 0.005F) * c + y
                    p2x = (robotShape.head - 0.005F) * c - (w + 0.005F) * s + x
                    p2y = (robotShape.head - 0.005F) * s + (w + 0.005F) * c + y
                    p3x = (-robotShape.tail + 0.005F) * c - (w + 0.005F) * s + x
                    p3y = (-robotShape.tail + 0.005F) * s + (w + 0.005F) * c + y
                    p4x = (-robotShape.tail - 0.005F) * c - (w + 0.005F) * s + x
                    p4y = (-robotShape.tail - 0.005F) * s + (w + 0.005F) * c + y
                    p5x = (robotShape.head - 0.005F) * c - (w - 0.005F) * s + x
                    p5y = (robotShape.head - 0.005F) * s + (w - 0.005F) * c + y
                    p6x = (-robotShape.tail + 0.005F) * c - (w - 0.005F) * s + x
                    p6y = (-robotShape.tail + 0.005F) * s + (w - 0.005F) * c + y
                    p7x = (robotShape.head - 0.005F) * c - (-w + 0.005F) * s + x
                    p7y = (robotShape.head - 0.005F) * s + (-w + 0.005F) * c + y
                    p8x = (-robotShape.tail + 0.005F) * c - (-w + 0.005F) * s + x
                    p8y = (-robotShape.tail + 0.005F) * s + (-w + 0.005F) * c + y
                    p9x = (robotShape.head + 0.005F) * c - (-w - 0.005F) * s + x
                    p9y = (robotShape.head + 0.005F) * s + (-w - 0.005F) * c + y
                    p10x = (robotShape.head - 0.005F) * c - (-w - 0.005F) * s + x
                    p10y = (robotShape.head - 0.005F) * s + (-w - 0.005F) * c + y
                    p11x = (-robotShape.tail + 0.005F) * c - (-w - 0.005F) * s + x
                    p11y = (-robotShape.tail + 0.005F) * s + (-w - 0.005F) * c + y
                    p12x = (-robotShape.tail - 0.005F) * c - (-w - 0.005F) * s + x
                    p12y = (-robotShape.tail - 0.005F) * s + (-w - 0.005F) * c + y
                    val wtcp = robotShape.width / 0.03F
                    val htcp = (robotShape.head + robotShape.tail) / 0.03F
                    vertexList.addAll(
                        arrayOf(
                            p1x, p1y, wtcp, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p2x, p2y, wtcp, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p10x, p10y, 0F, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p9x, p9y, 0F, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,

                            p3x, p3y, wtcp, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p4x, p4y, wtcp, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p12x, p12y, 0F, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p11x, p11y, 0F, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,

                            p2x, p2y, 0F, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p3x, p3y, htcp, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p6x, p6y, htcp, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p5x, p5y, 0F, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,

                            p7x, p7y, 0F, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p8x, p8y, htcp, 0F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p11x, p11y, htcp, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                            p10x, p10y, 0F, 1F, MESH_TYPE_LINE, 0F, 0.45F, 0F, 0.7F,
                        )
                    )
                    indicesList.addAll(
                        arrayOf(
                            indx, indx + 1, indx + 2, indx, indx + 2, indx + 3,
                            indx + 4, indx + 5, indx + 6, indx + 4, indx + 6, indx + 7,
                            indx + 8, indx + 9, indx + 10, indx + 8, indx + 10, indx + 11,
                            indx + 12, indx + 13, indx + 14, indx + 12, indx + 14, indx + 15,
                        )
                    )
                    indx += 16
                    if (!it.ignoreDir) {
                        p1x = arrowW * 4 * c - 0 * s + x
                        p1y = arrowW * 4 * s + 0 * c + y
                        p2x = arrowW * 3 * c - arrowW * s + x
                        p2y = arrowW * 3 * s + arrowW * c + y
                        p3x = arrowW * 3 * c + arrowW * s + x
                        p3y = arrowW * 3 * s - arrowW * c + y
                        vertexList.addAll(
                            arrayOf(
                                p1x, p1y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.7F,
                                p2x, p2y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.7F,
                                p3x, p3y, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 0.7F,
                            )
                        )
                        indicesList.addAll(arrayOf(indx, indx + 1, indx + 2))
                        indx += 3
                    }
                    val name = it.instanceName
                    val fv = Global.fontFace!!.getVertexData(
                        name,
                        x,
                        y - 0.2F,
                        FontFace.Align.Center,
                        indx,
                        0.003F
                    )
                    fv.fontTexCoordList.forEach { v ->
                        vertexList.addAll(
                            arrayOf(
                                v.x,
                                v.y,
                                v.xTexCoord,
                                v.yTexCoord,
                                MESH_TYPE_FONT,
                                0F,
                                0F,
                                0F,
                                0.6F
                            )
                        )
                    }
                    indicesList.addAll(fv.indices)
                }
            } else {
                val w = robotShape.radius + robotShape.radius * 0.2F
                advancedPointList.forEach {
                    val x = it.pos.x.toFloat()
                    val y = it.pos.y.toFloat()
                    val c = cos(it.dir.toFloat())
                    val s = sin(it.dir.toFloat())
                    val texCoord = STATION_TEXCOORD_MAP.getOrDefault(
                        it.instanceName,
                        STATION_TEXCOORD_MAP["LocationMark"]
                    )!!
                    val p1x: Float = -w * c - w * s + x
                    val p1y: Float = -w * s + w * c + y
                    val p2x: Float = w * c - w * s + x
                    val p2y: Float = w * s + w * c + y
                    val p3x: Float = w * c - -w * s + x
                    val p3y: Float = w * s + -w * c + y
                    val p4x: Float = -w * c - -w * s + x
                    val p4y: Float = -w * s + -w * c + y
                    val indx = vertexList.size / 9
                    val f = if (it.ignoreDir) 0F else 1F
                    vertexList.addAll(
                        arrayOf(
                            p1x,
                            p1y,
                            texCoord[0],
                            texCoord[1],
                            MESH_TYPE_SCENE_POINTS,
                            AR_TEXCOORD[0],
                            AR_TEXCOORD[1],
                            f,
                            0F,
                            p2x,
                            p2y,
                            texCoord[2],
                            texCoord[3],
                            MESH_TYPE_SCENE_POINTS,
                            AR_TEXCOORD[2],
                            AR_TEXCOORD[3],
                            f,
                            0F,
                            p3x,
                            p3y,
                            texCoord[4],
                            texCoord[5],
                            MESH_TYPE_SCENE_POINTS,
                            AR_TEXCOORD[4],
                            AR_TEXCOORD[5],
                            f,
                            0F,
                            p4x,
                            p4y,
                            texCoord[6],
                            texCoord[7],
                            MESH_TYPE_SCENE_POINTS,
                            AR_TEXCOORD[6],
                            AR_TEXCOORD[7],
                            f,
                            0F,
                        )
                    )
                    indicesList.addAll(arrayOf(indx, indx + 1, indx + 2, indx, indx + 2, indx + 3))

                    if (bound != null) {
                        bound.xMax = maxOf(bound.xMax, p1x, p2x, p3x, p4x)
                        bound.yMax = maxOf(bound.yMax, p1y, p2y, p3y, p4y)
                        bound.xMin = minOf(bound.xMin, p1x, p2x, p3x, p4x)
                        bound.yMin = minOf(bound.yMin, p1y, p2y, p3y, p4y)
                    }

                    val name = it.instanceName
                    val fv = Global.fontFace!!.getVertexData(
                        name,
                        x,
                        y - 0.2F,
                        FontFace.Align.Center,
                        indx + 3,
                        0.003F
                    )
                    fv.fontTexCoordList.forEach { v ->
                        vertexList.addAll(
                            arrayOf(
                                v.x,
                                v.y,
                                v.xTexCoord,
                                v.yTexCoord,
                                MESH_TYPE_FONT,
                                0F,
                                0F,
                                0F,
                                0.6F
                            )
                        )
                    }
                    indicesList.addAll(fv.indices)
                }
            }
        }

        private fun binLocationsMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            binLocationsList: List<MessageMap.Message_BinLocations>,
            bound: Bound? = null
        ) {
            val pen = Color(18, 150, 219, 100).toOpenGLColor()
            val brush = Color(18, 150, 219, 25).toOpenGLColor()
            val gson = Gson()
            for (it in binLocationsList) {
                var x1 = 0F
                var x2 = 0F
                var x3 = 0F
                var x4 = 0F
                var x5 = 0F
                var x6 = 0F
                var x7 = 0F
                var x8 = 0F
                var x9 = 0F
                var x10 = 0F
                var x11 = 0F
                var x12 = 0F
                var y1 = 0F
                var y2 = 0F
                var y3 = 0F
                var y4 = 0F
                var y5 = 0F
                var y6 = 0F
                var y7 = 0F
                var y8 = 0F
                var y9 = 0F
                var y10 = 0F
                var y11 = 0F
                var y12 = 0F
                var c = 0F
                var s = 0F
                var theta = 0F
                val bl = it.binLocationListList.first()
                val x = bl.pos.x.toFloat()
                val y = bl.pos.y.toFloat()
                var width = 0F
                var height = 0F
                var noPoints = true
                for (mp in bl.propertyList) {
                    when (mp.key) {
                        "points" -> {
                            try {
                                val ps = gson.fromJson(mp.stringValue, Array<JSPoint>::class.java)
                                if (ps.size != 4) break
                                x1 = ps[0].x
                                x2 = ps[1].x
                                x3 = ps[2].x
                                x4 = ps[3].x
                                y1 = ps[0].y
                                y2 = ps[1].y
                                y3 = ps[2].y
                                y4 = ps[3].y
                                c = (x1 - x4) / sqrt((x1 - x4) * (x1 - x4) + (y1 - y4) * (y1 - y4))
                                theta = (PI / 2 - acos(c)).toFloat()
                                s = sin(theta)
                                c = cos(theta)
                            } catch (e: Exception) {
                                Log.d("SRJ-${Companion::class.java.simpleName}", "${e.message}")
                            }
                            noPoints = false
                            break
                        }

                        "width" -> width = mp.doubleValue.toFloat()
                        "height" -> height = mp.doubleValue.toFloat()
                    }
                }
                if (noPoints) {
                    if (width == 0F || height == 0F) continue
                    x1 = x - width / 2
                    y1 = y - height / 2
                    x2 = x + width / 2
                    y2 = y - height / 2
                    x3 = x + width / 2
                    y3 = y + height / 2
                    x4 = x - width / 2
                    y4 = y + height / 2
                    s = 0F
                    c = 1F
                }
                if (bound != null) {
                    bound.xMax = maxOf(bound.xMax, x1, x2, x3, x4)
                    bound.yMax = maxOf(bound.yMax, y1, y2, y3, y4)
                    bound.xMin = minOf(bound.xMin, x1, x2, x3, x4)
                    bound.yMin = minOf(bound.yMin, y1, y2, y3, y4)
                }
                val pointName = bl.pointName
                val size = it.binLocationListCount
                val instanceName =
                    if (size != 1) bl.instanceName + "~" + it.binLocationListList.last().instanceName else bl.instanceName
                x5 = x1 + 0.005F * c - 0.005F * s
                y5 = y1 + 0.005F * s + 0.005F * c
                x6 = x1 - 0.005F * c - (-0.005F) * s
                y6 = y1 - 0.005F * s + (-0.005F) * c
                x7 = x2 - 0.005F * c - 0.005F * s
                y7 = y2 - 0.005F * s + 0.005F * c
                x8 = x2 + 0.005F * c - (-0.005F) * s
                y8 = y2 + 0.005F * s + (-0.005F) * c

                x9 = x3 + 0.005F * c - 0.005F * s
                y9 = y3 + 0.005F * s + 0.005F * c
                x10 = x3 - 0.005F * c - (-0.005F) * s
                y10 = y3 - 0.005F * s + (-0.005F) * c
                x11 = x4 - 0.005F * c - 0.005F * s
                y11 = y4 - 0.005F * s + 0.005F * c
                x12 = x4 + 0.005F * c - (-0.005F) * s
                y12 = y4 + 0.005F * s + (-0.005F) * c
                var indx: Int = vertexList.size / 9
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

                        x - 0.005F, y - 0.005F, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x + 0.005F, y - 0.005F, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x + 0.005F, y + 0.005F, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        x - 0.005F, y + 0.005F, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                    )
                )
                indicesList.addAll(
                    arrayOf(
                        indx, indx + 1, indx + 2, indx, indx + 2, indx + 3,
                        indx + 4, indx + 5, indx + 6, indx + 5, indx + 6, indx + 7,
                        indx + 6, indx + 7, indx + 9, indx + 7, indx + 8, indx + 9,
                        indx + 8, indx + 9, indx + 11, indx + 8, indx + 10, indx + 11,
                        indx + 10, indx + 11, indx + 4, indx + 10, indx + 4, indx + 5,
                        indx + 12, indx + 13, indx + 14, indx + 12, indx + 14, indx + 15,
                    )
                )
                indx += 16
                for (i in 1 until size) {
                    var px = i.toFloat() / size.toFloat() * (x1 - x4) + x4
                    var py = i.toFloat() / size.toFloat() * (y1 - y4) + y4
                    x5 = px + 0.005F * c - 0.005F * s
                    y5 = py + 0.005F * s + 0.005F * c
                    x6 = px + 0.005F * c - -0.005F * s
                    y6 = py + 0.005F * s + -0.005F * c

                    px = i.toFloat() / size * (x2 - x3) + x3
                    py = i.toFloat() / size * (y2 - y3) + y3
                    x7 = px - 0.005F * c - 0.005F * s
                    y7 = py - 0.005F * s + 0.005F * c
                    x8 = px - 0.005F * c - -0.005F * s
                    y8 = py - 0.005F * s + -0.005F * c
                    vertexList.addAll(
                        arrayOf(
                            x5, y5, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                            x6, y6, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                            x7, y7, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                            x8, y8, 0F, 0F, MESH_TYPE_COLOR, pen.r, pen.g, pen.b, pen.a,
                        )
                    )
                    indicesList.addAll(
                        arrayOf(
                            indx,
                            indx + 1,
                            indx + 2,
                            indx + 1,
                            indx + 2,
                            indx + 3
                        )
                    )
                    indx += 4
                }
                val centerX = (x2 + x3) / 2
                val centerY = (y2 + y3) / 2
                x5 = centerX + 0.02F * c - 0.05F * s
                y5 = centerY + 0.02F * s + 0.05F * c

                x6 = centerX + 0.02F * c - -0.05F * s
                y6 = centerY + 0.02F * s + -0.05F * c

                x7 = centerX + 0.07F * c
                y7 = centerY + 0.07F * s

                vertexList.addAll(
                    arrayOf(
                        x5, y5, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 1F,
                        x6, y6, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 1F,
                        x7, y7, 0F, 0F, MESH_TYPE_COLOR, 1F, 0F, 0F, 1F,
                    )
                )
                indicesList.addAll(arrayOf(indx, indx + 1, indx + 2))
                indx += 3
                var fv = Global.fontFace!!.getVertexData(
                    instanceName,
                    x,
                    y + 0.06F,
                    FontFace.Align.Center,
                    indx,
                    0.001F
                )
                fv.fontTexCoordList.forEach { v ->
                    vertexList.addAll(
                        arrayOf(
                            v.x,
                            v.y,
                            v.xTexCoord,
                            v.yTexCoord,
                            MESH_TYPE_FONT,
                            0F,
                            0F,
                            0F,
                            0.6F
                        )
                    )
                }
                indicesList.addAll(fv.indices)
                indx += fv.fontTexCoordList.size
                fv = Global.fontFace!!.getVertexData(
                    pointName,
                    x,
                    y - 0.06F,
                    FontFace.Align.Center,
                    indx,
                    0.001F
                )
                fv.fontTexCoordList.forEach { v ->
                    vertexList.addAll(
                        arrayOf(
                            v.x,
                            v.y,
                            v.xTexCoord,
                            v.yTexCoord,
                            MESH_TYPE_FONT,
                            0F,
                            0F,
                            0F,
                            0.6F
                        )
                    )
                }
                indicesList.addAll(fv.indices)
            }
        }

        private fun tagPosMesh(
            vertexList: MutableList<Float>,
            indicesList: MutableList<Int>,
            tagPosList: List<MessageMap.Message_tagPos>,
            bound: Bound? = null
        ) {
            var tc: FloatArray
            var l1: Float
            var l2: Float
            tagPosList.forEach {
                if (it.className == "3DTAG") {
                    tc = TAG_TEXCOORD_3D
                    l1 = 0.06F
                    l2 = 0.06F
                } else {
                    tc = TAG_TEXCOORD_2D
                    l1 = 0.03F
                    l2 = 0.06F
                }
                val x = it.x.toFloat()
                val y = it.y.toFloat()
                val s = sin(it.angle).toFloat()
                val c = cos(it.angle).toFloat()

                val x1 = -l1 * c - l1 * s + x
                val y1 = -l1 * s + l1 * c + y
                val x2 = l2 * c - l1 * s + x
                val y2 = l2 * s + l1 * c + y
                val x3 = l2 * c - (-l1) * s + x
                val y3 = l2 * s + (-l1) * c + y
                val x4 = -l1 * c - (-l1) * s + x
                val y4 = -l1 * s + (-l1) * c + y

                val indx = vertexList.size / 9
                vertexList.addAll(
                    arrayOf(
                        x1, y1, tc[0], tc[1], MESH_TYPE_TAG, 0F, 0F, 0F, 1F,
                        x2, y2, tc[2], tc[3], MESH_TYPE_TAG, 0F, 0F, 0F, 1F,
                        x3, y3, tc[4], tc[5], MESH_TYPE_TAG, 0F, 0F, 0F, 1F,
                        x4, y4, tc[6], tc[7], MESH_TYPE_TAG, 0F, 0F, 0F, 1F,
                    )
                )
                indicesList.addAll(
                    arrayOf(
                        indx,
                        indx + 1,
                        indx + 2,
                        indx,
                        indx + 2,
                        indx + 3
                    )
                )
                if (bound != null) {
                    bound.xMax = maxOf(bound.xMax, x1, x2, x3, x4)
                    bound.yMax = maxOf(bound.yMax, y1, y2, y3, y4)
                    bound.xMin = minOf(bound.xMin, x1, x2, x3, x4)
                    bound.yMin = minOf(bound.yMin, y1, y2, y3, y4)
                }
                val fv = Global.fontFace!!.getVertexData(
                    it.tagValue.toString(),
                    x,
                    y - 0.015F,
                    FontFace.Align.Center,
                    indx + 4,
                    0.001F
                )
                fv.fontTexCoordList.forEach { v ->
                    vertexList.addAll(
                        arrayOf(
                            v.x,
                            v.y,
                            v.xTexCoord,
                            v.yTexCoord,
                            MESH_TYPE_FONT,
                            0F,
                            0F,
                            0F,
                            0.6F
                        )
                    )
                }
                indicesList.addAll(fv.indices)
            }
        }
    }
}