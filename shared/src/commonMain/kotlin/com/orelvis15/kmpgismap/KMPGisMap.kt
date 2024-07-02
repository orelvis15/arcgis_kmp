package com.orelvis15.kmpgismap

import androidx.compose.runtime.Composable

@Composable
fun KMPGisMap(mapConfig: GisMapConfig, onMapClick:(lat: Double, lon: Double) -> Unit) {
    val map = GisMap(onMapLoadSuccess = {}, onMapLoadFailed = {})
    map.MapView(mapConfig, onMapClick)
}