package com.seergroup.srj

import android.content.res.AssetManager
import android.graphics.Bitmap

object Global {
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var scale: Float = 1f
    var assets: AssetManager? = null
    val bitmapResources = mutableMapOf<String, Bitmap>()


    fun dpToPx(dp: Float): Float {
        return dp * scale
    }

    fun pxToDp(px: Float): Float {
        return px / scale
    }
}
