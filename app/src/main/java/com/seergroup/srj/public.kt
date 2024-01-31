package com.seergroup.srj

import android.graphics.Bitmap

object public {
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var scale: Float = 1f
    val bitmapResources = mutableMapOf<String, Bitmap>()

    fun dpToPx(dp: Float): Float {
        return dp * scale
    }

    fun pxToDp(px: Float): Float {
        return px / scale
    }
}
