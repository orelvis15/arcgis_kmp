package com.orelvis.gismap

class GisMapConfig(
    val apiKey: String,
    val layerUrl: String,
    val viewPoint: ViewPointConfig
)

class ViewPointConfig(
    val lat: Double,
    val lon: Double,
    val scale: Double = 100000.0,
)