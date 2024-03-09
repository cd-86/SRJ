package com.seergroup.srj.nativelib

import android.content.res.AssetManager
import android.util.Log
import com.seergroup.srj.Global

class FontFace {
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


    companion object {
        init {
            System.loadLibrary("lib")
        }
    }

    private val instance: Long

    init {
        instance = newFontFace(Global.assets, "SmileySans-Oblique.ttf")
        if (instance == 0L) {
            Log.d("TAG::FontFace", "Freetype init: Failed")
        }
        val str = getErrorString(instance)
        if (str.isNotEmpty()) {
            Log.d("TAG::FontFace", str)
        } else {
            Log.d("TAG::FontFace", "Freetype init: Success")
        }
        val info = getCharacter(instance, 'a')
        Log.d("TAG::FontFace", info.toString())
        val info2 = getCharacter(instance, '我')
        Log.d("TAG::FontFace", info2.toString())
    }

    fun destroy() {
        deleteFontFace(instance)
        Log.d("TAG::FontFace", "destroy: Success")
    }
}