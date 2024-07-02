package com.orelvis15.kmpgismap

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.symbology.PictureMarkerSymbol
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.MapView
import kotlinx.coroutines.launch

class ComposeMapView(
    context: Context,
    private val mapConfig: GisMapConfig,
    private val onMapLoadSuccess: () -> Unit = {},
    private val onMapLoadFailed: (Throwable) -> Unit = {},
    val onClick: (lat: Double, lon: Double) -> Unit
) {
    private var mapView: MapView = MapView(context)

    init {
        ArcGISEnvironment.apiKey = ApiKey.create(mapConfig.apiKey)
        val map = ArcGISMap(BasemapStyle.ArcGISStreets)
        map.operationalLayers.add(FeatureLayer.createWithFeatureTable(ServiceFeatureTable(mapConfig.layerUrl)))
        mapView.map = map
    }

    @Composable
    fun GetMap() {
        val viewpoint by remember {
            mutableStateOf(
                Viewpoint(
                    mapConfig.viewPoint.lat,
                    mapConfig.viewPoint.lon,
                    mapConfig.viewPoint.scale,
                )
            )
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        val mapView = createMapViewInstance(lifecycleOwner, mapView)

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { _ ->
                mapView.apply {
                    this.map = mapView.map
                    setViewpoint(viewpoint)
                }
            }
        )

        LaunchedEffect(Unit) {

            mapView.map?.load()?.fold(
                onSuccess = { onMapLoadSuccess() },
                onFailure = { onMapLoadFailed(Throwable()) }
            )

            mapView.onSingleTapConfirmed.collect {
                mapView.screenToLocation(it.screenCoordinate)
                onClick(it.mapPoint!!.y, it.mapPoint!!.x)
            }
        }
    }

    fun drawPin(point: Point?, pin: ByteArray) {
        var graphicsOverlay: GraphicsOverlay? = mapView.graphicsOverlays.find { it.id == "pin" }

        if (graphicsOverlay == null) {
            graphicsOverlay = GraphicsOverlay()
            graphicsOverlay.scaleSymbols = true
            graphicsOverlay.id = "pin"
            mapView.graphicsOverlays.add(graphicsOverlay)
        }

        graphicsOverlay.scaleSymbols = true

        val pointAttributes: MutableMap<String, Any> = HashMap()

        val image = BitmapFactory.decodeByteArray(pin, 0, pin.size)

        val node = PictureMarkerSymbol.createWithImage(BitmapDrawable(Resources.getSystem(), image))
        val graphic = Graphic(point, pointAttributes, node)

        graphicsOverlay.graphics.clear()
        graphicsOverlay.graphics.add(graphic)
    }

    suspend fun centerMap(lat: Double, lon: Double, scale: Double? = null) {
        val point = Point(x = lon, y = lat)
        val currentScale = scale ?: mapView.mapScale.value
        val viewpoint = Viewpoint(point, currentScale)
        mapView.setViewpointAnimated(viewpoint, durationSeconds = 0.5f)
    }
}

@Composable
fun createMapViewInstance(lifecycleOwner: LifecycleOwner, mapView: MapView): MapView {
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(mapView)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(mapView)
        }
    }
    return mapView
}