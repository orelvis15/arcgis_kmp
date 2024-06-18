package com.orelvis.gismap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.view.MapView
import kotlinx.coroutines.launch

class ComposeMapView(
    val onClick: () -> Unit = {}
) {
    lateinit var mapView: MapView

    val layerUrl = "https://ccpublicgis.cctexas.com/server01/rest/services/311_INCAP/311_INCAP/MapServer/29"

    init {
        ArcGISEnvironment.apiKey = ApiKey.create("AAPK92a52386e9dc4166b3756e68932f4328U0FKyQYYyXLBybTfzdEkF134pT6CQEHV4ghSaLYuxqBAIMGmzeQfY79neYAObhTF")
    }

    @Composable
    fun GetMap() {
        val map by remember { mutableStateOf(ArcGISMap(BasemapStyle.ArcGISStreets)) }
        map.operationalLayers.add(FeatureLayer.createWithFeatureTable(ServiceFeatureTable(layerUrl)))

        val viewpoint by remember {
            mutableStateOf(
                Viewpoint(
                    27.796522238,
                    -97.403100544,
                    100000.0
                )
            )
        }

        mapView = MapView(LocalContext.current)
        mapView.map = map

        val lifecycleOwner = LocalLifecycleOwner.current
        val mapView = createMapViewInstance(lifecycleOwner, mapView)

        // wrap the MapView as an AndroidView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            // recomposes the MapView on changes in the MapViewState
            update = { _ ->
                mapView.apply {
                    this.map = mapView.map
                    setViewpoint(viewpoint)
                }
            }
        )

        LaunchedEffect(Unit) {
            launch {
                mapView.onSingleTapConfirmed.collect {
                    onClick()
                }
            }
        }
    }
}

/**
 * Create the MapView instance and add it to the Activity lifecycle
 */
@Composable
fun createMapViewInstance(lifecycleOwner: LifecycleOwner, mapView: MapView): MapView {
    // add the side effects for MapView composition
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(mapView)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(mapView)
        }
    }
    return mapView
}