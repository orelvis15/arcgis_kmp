package com.orelvis15.kmpgismap

data class GisMapConfig(
    val apiKey: String,
    val layerUrl: String,
    val viewPoint: ViewPointConfig
)

data class ViewPointConfig(
    val lat: Double,
    val lon: Double,
    val scale: Double = 100000.0,
)