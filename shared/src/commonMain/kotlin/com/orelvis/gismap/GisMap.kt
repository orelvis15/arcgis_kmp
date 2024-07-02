package com.orelvis.gismap

import androidx.compose.runtime.Composable

expect class GisMap(){
    @Composable
    fun KMPMapView(mapConfig: GisMapConfig, onClick: (lat: Double, lon: Double) -> Unit)
    fun drawPin(lat: Double, lon: Double, pin: ByteArray)
    suspend fun centerMap(lat: Double, lon: Double, scale: Double? = null)
}