package com.seergroup.srj.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.seergroup.srj.gl.GLView

@Composable
fun MainView(glView: GLView) {
    AndroidView(factory = { glView })
}