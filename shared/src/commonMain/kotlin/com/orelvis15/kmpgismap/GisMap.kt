package com.orelvis15.kmpgismap

import androidx.compose.runtime.Composable

expect class GisMap(
    onMapLoadSuccess: () -> Unit,
    onMapLoadFailed: (Throwable) -> Unit
) {
    @Composable
    fun MapView(
        mapConfig: GisMapConfig,
        onClick: (lat: Double, lon: Double) -> Unit
    )

    fun drawPin(lat: Double, lon: Double, pin: ByteArray)
    suspend fun centerMap(lat: Double, lon: Double, scale: Double? = null)
}