package com.orelvis.gismap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cocoapods.ArcGIS.AGSPoint
import kotlinx.cinterop.ExperimentalForeignApi

actual class GisMap {

    lateinit var map: IOSMapView

    @Composable
    actual fun KMPMapView(mapConfig: GisMapConfig, onClick: (lat: Double, lon: Double) -> Unit) {
        map = IOSMapView(mapConfig, onClick)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            map.GetMap()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun drawPin(lat: Double, lon: Double, pin: ByteArray) {
        map.drawPin(AGSPoint.pointWithX(x = lon, y = lat, null), pin)
    }

    actual suspend fun centerMap(lat: Double, lon: Double, scale: Double?) {
        map.centerMap(lat, lon, scale)
    }
}