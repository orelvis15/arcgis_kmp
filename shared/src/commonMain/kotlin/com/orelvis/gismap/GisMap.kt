package com.orelvis.gismap

import androidx.compose.runtime.Composable

expect class GisMap(){
    @Composable
    fun getMap()
}