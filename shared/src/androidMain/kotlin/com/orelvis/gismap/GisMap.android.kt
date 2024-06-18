package com.orelvis.gismap

import androidx.compose.runtime.Composable

actual class GisMap {

    @Composable
    actual fun getMap(){
        val map = ComposeMapView {
            println("maptest")
        }
        map.GetMap()
    }
}