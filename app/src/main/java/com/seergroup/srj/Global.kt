package com.seergroup.srj

import android.content.res.AssetManager
import com.seergroup.srj.nativelib.FontFace

object Global {
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var scale: Float = 1f
    var assets: AssetManager? = null
        set(value) {
            if (field == null) {
                field = value
                fontFace = FontFace()
            }
        }
    var fontFace: FontFace? = null
        set(value) {
            field?.destroy()
            field = value
        }


    fun dpToPx(dp: Float): Float {
        return dp * scale
    }

    fun pxToDp(px: Float): Float {
        return px / scale
    }
}
