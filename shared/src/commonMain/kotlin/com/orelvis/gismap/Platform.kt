package com.orelvis.gismap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform