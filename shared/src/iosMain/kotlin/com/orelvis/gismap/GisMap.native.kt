package com.orelvis.gismap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi

actual class GisMap {

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun getMap() {
        val map = IOSMapView({ println("maptest") })

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            map.GetMap()
        }
    }
}