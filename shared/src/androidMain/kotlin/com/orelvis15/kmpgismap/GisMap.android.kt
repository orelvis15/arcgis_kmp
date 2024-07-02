package com.orelvis15.kmpgismap

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arcgismaps.geometry.Point

actual class GisMap actual constructor(
    private var onMapLoadSuccess: () -> Unit,
    private var onMapLoadFailed: (Throwable) -> Unit
) {

    private lateinit var map: ComposeMapView

    @Composable
    actual fun MapView(mapConfig: GisMapConfig, onClick: (lat: Double, lon: Double) -> Unit) {
        map = ComposeMapView(
            LocalContext.current,
            mapConfig,
            onMapLoadSuccess,
            onMapLoadFailed,
            onClick
        )
        map.GetMap()
    }

    actual fun drawPin(lat: Double, lon: Double, pin: ByteArray) {
        map.drawPin(Point(x = lon, y = lat), pin)
    }

    actual suspend fun centerMap(lat: Double, lon: Double, scale: Double?) {
        map.centerMap(lat, lon, scale)
    }
}