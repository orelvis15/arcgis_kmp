package com.orelvis.gismap

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arcgismaps.geometry.Point

actual class GisMap {

    private lateinit var map: ComposeMapView

    @Composable
    actual fun KMPMapView(mapConfig: GisMapConfig, onClick: (lat: Double, lon: Double) -> Unit){
        map = ComposeMapView(LocalContext.current, mapConfig, onClick)
        map.GetMap()
    }

    actual fun drawPin(lat: Double, lon: Double, pin: ByteArray) {
        map.drawPin(Point(x = lon, y = lat), pin)
    }

    actual suspend fun centerMap(lat: Double, lon: Double, scale: Double?) {
        map.centerMap(lat, lon, scale)
    }
}