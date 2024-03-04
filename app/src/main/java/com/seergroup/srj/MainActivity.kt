package com.seergroup.srj

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.seergroup.srj.composable.MainView
import com.seergroup.srj.gl.GLView
import com.seergroup.srj.ui.theme.SRJTheme

class MainActivity : ComponentActivity() {
    private val glView by lazy { GLView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initPublic()

        setContent {
            SRJTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(glView)
                }
            }
        }
    }

    private fun initPublic() {
        public.screenWidth = resources.displayMetrics.widthPixels
        public.screenHeight = resources.displayMetrics.heightPixels
        public.scale = resources.displayMetrics.density
        public.assets = assets
        public.bitmapResources["touch_button"] = BitmapFactory.decodeResource(resources, R.drawable.touch_button)
    }
}