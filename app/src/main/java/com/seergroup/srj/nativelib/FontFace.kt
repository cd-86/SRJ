package com.seergroup.srj.nativelib

import android.content.res.AssetManager
import android.util.Log
import com.seergroup.srj.Global
import java.nio.ByteBuffer


class FontFace {
    enum class Align { Left, Right, HCenter, Top, Bottom, VCenter, Center }
    data class FontTexCoord(val x: Float, val y: Float, val xTexCoord: Float, val yTexCoord: Float)
    data class FontVertex(
        val fontTexCoordList: List<FontTexCoord> = emptyList(),
        val indices: List<Int> = emptyList()
    )

    data class CharTextureInfo(
        val width: Int,
        val height: Int,
        val bearingX: Int,
        val bearingY: Int,
        val advance: Int,
        val xOffset: Int
    )

    private external fun newFontFace(am: AssetManager?, fileName: String): Long
    private external fun deleteFontFace(instance: Long)
    private external fun getErrorString(instance: Long): String
    private external fun getCharacter(instance: Long, c: Char): CharTextureInfo
    private external fun getTextureWidth(instance: Long): Int
    private external fun getTextureHeight(instance: Long): Int
    private external fun getTextureData(instance: Long): ByteBuffer


    companion object {
        init {
            System.loadLibrary("lib")
        }

        const val CHAR_Y_OFFSET = 2F
    }

    private val instance: Long = newFontFace(Global.assets, "msyh.ttc")
    private val fontMap = mutableMapOf<Char, CharTextureInfo>()

    init {
        if (instance == 0L) {
            Log.d("SRJ-${javaClass.simpleName}", "Freetype init: Failed")
        }
        val str = getErrorString(instance)
        if (str.isNotEmpty()) {
            Log.d("SRJ-${javaClass.simpleName}", str)
        } else {
            Log.d("SRJ-${javaClass.simpleName}", "Freetype init: Success")
        }
    }

    fun textureWidth(): Int {
        return getTextureWidth(instance)
    }

    fun textureHeight(): Int {
        return getTextureHeight(instance)
    }

    fun textureData(): ByteBuffer {
        return getTextureData(instance)
    }

    fun getVertexData(
        text: String,
        x: Float,
        y: Float,
        align: Align,
        indicesOffset: Int = 0,
        scale: Float
    ): FontVertex {
        if (text.isEmpty()) {
            return FontVertex()
        }
        val fontTexCoordList = mutableListOf<FontTexCoord>()
        val indices = mutableListOf<Int>()
        val charWidthList = mutableListOf<Float>()
        charWidthList.add(0F)
        for (c in text) {
            if (c == '\n') {
                charWidthList.add(0F)
                continue
            }
            if (c !in fontMap) {
                fontMap[c] = getCharacter(instance, c)
            }
            charWidthList[charWidthList.size - 1] += fontMap[c]!!.advance * scale
        }

        if (align == Align.HCenter || align == Align.Center) {
            for (i in 0 until charWidthList.size) {
                charWidthList[i] = x - charWidthList[i] / 2
            }
        } else if (align == Align.Right) {
            for (i in 0 until charWidthList.size) {
                charWidthList[i] = x - charWidthList[i]
            }
        } else {
            for (i in 0 until charWidthList.size) {
                charWidthList[i] = x
            }
        }
        var tY = y
        if (align == Align.VCenter || align == Align.Center) {
            tY += (charWidthList.size - 1) * 48 * scale - 24 * scale
        } else if (align == Align.Bottom) {
            tY += charWidthList.size * 48 * scale
        }
        var tX = charWidthList.first()
        var i = 0
        var line = 0
        for (c in text) {
            if (c == '\n') {
                line++
                tX = charWidthList[line]
                tY -= 48 * scale
                continue
            }
            val ft = fontMap[c]!!
            var x1: Float = tX + ft.bearingX * scale
            var x2: Float = x1 + ft.width * scale
            var y1: Float = tY + ft.bearingY * scale
            var y2: Float = y1 - ft.height * scale
            fontTexCoordList.add(FontTexCoord(x1, y1, ft.xOffset.toFloat(), CHAR_Y_OFFSET))
            fontTexCoordList.add(
                FontTexCoord(
                    x2,
                    y1,
                    ft.xOffset.toFloat() + ft.width,
                    CHAR_Y_OFFSET
                )
            )
            fontTexCoordList.add(
                FontTexCoord(
                    x2,
                    y2,
                    ft.xOffset.toFloat() + ft.width,
                    ft.height + CHAR_Y_OFFSET
                )
            )
            fontTexCoordList.add(
                FontTexCoord(
                    x1,
                    y2,
                    ft.xOffset.toFloat(),
                    ft.height + CHAR_Y_OFFSET
                )
            )
            val j = indicesOffset + i * 4
            indices.addAll(
                arrayOf(
                    j, j + 1, j + 2, j, j + 2, j + 3
                )
            )
            tX += ft.advance * scale
            i++
        }
        return FontVertex(fontTexCoordList, indices)
    }


    fun destroy() {
        deleteFontFace(instance)
        Log.d("SRJ-${javaClass.simpleName}", "destroy: Success")
    }
}