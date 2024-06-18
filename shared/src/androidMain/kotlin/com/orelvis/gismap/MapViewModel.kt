package com.orelvis.gismap

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.LatitudeLongitudeFormat
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.symbology.PictureMarkerSymbol
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.mapping.view.SingleTapConfirmedEvent

class MapViewModel(
    application: Application
) : AndroidViewModel(application) {

    var mapView: MapView? by mutableStateOf(null)

    @Composable
    fun initMap(loadSuccess: () -> Unit, loadFaild: () -> Unit): MapView {
        ArcGISEnvironment.apiKey =
            ApiKey.create("AAPK92a52386e9dc4166b3756e68932f4328U0FKyQYYyXLBybTfzdEkF134pT6CQEHV4ghSaLYuxqBAIMGmzeQfY79neYAObhTF")

        val layerUrl = "https://ccpublicgis.cctexas.com/server01/rest/services/311_INCAP/311_INCAP/MapServer/29"
        val layer = ServiceFeatureTable(layerUrl)

        val map by remember { mutableStateOf(ArcGISMap(BasemapStyle.ArcGISStreets)) }
        map.operationalLayers.add(FeatureLayer.createWithFeatureTable(layer))

        mapView = MapView(LocalContext.current)
        mapView?.map = map

        LaunchedEffect(Unit) {
            map.load().onSuccess {
                loadSuccess()
            }
            map.load().onFailure {
                loadFaild()
            }
        }

        return mapView!!
    }

    fun clearGraphics() {
        mapView?.graphicsOverlays?.clear()
    }

    suspend fun drawPin(point: Point?, id : String, onlyShowOne: Boolean = false) {
        var graphicsOverlay: GraphicsOverlay? = mapView?.graphicsOverlays?.find { it.id == "pin" }

        if ( graphicsOverlay == null){
            graphicsOverlay = GraphicsOverlay()
            graphicsOverlay.scaleSymbols = true
            graphicsOverlay.id = "pin"
            mapView?.graphicsOverlays?.add(graphicsOverlay)
        }

        val pointAttributes: MutableMap<String, Any> = HashMap()
        pointAttributes["id"] = id

        val imageBytes = null// Res.readBytes("files/pin.png")
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, 0 /*imageBytes.size*/)
        val node = PictureMarkerSymbol.createWithImage(BitmapDrawable(image))
        val graphic = Graphic(point, pointAttributes, node)
        if (onlyShowOne){
            graphicsOverlay.graphics.clear()
        }
        graphicsOverlay.graphics.add(graphic)
    }

    suspend fun centerMap(tap: SingleTapConfirmedEvent){
        val point = getClickPoint(tap)
        if (point != null) {
            mapView?.setViewpointCenter(point, 1000.0)
            mapView?.setViewpointCenter(point)
        }
    }

    suspend fun centerMap(point: Point?){
        if (point != null) {
            mapView?.setViewpointCenter(point, 1000.0)
            mapView?.setViewpointCenter(point)
        }
    }

    fun getClickPoint(tap: SingleTapConfirmedEvent): Point? {
        val mapPoint = mapView?.screenToLocation(tap.screenCoordinate)
        val toLatitudeLongitude = CoordinateFormatter.toLatitudeLongitudeOrNull(mapPoint!!, LatitudeLongitudeFormat.DecimalDegrees, 16)
        return CoordinateFormatter.fromLatitudeLongitudeOrNull(toLatitudeLongitude!!, null)
    }

    suspend fun getPinFromPoint(tabEvent: SingleTapConfirmedEvent): String? {
        val a = mapView?.identifyGraphicsOverlays(tabEvent.screenCoordinate, 25.0, false)
        a?.fold(
            onSuccess = {
                if (it.isNotEmpty()){
                    return it[0].graphics[0].attributes["id"].toString()
                }else {
                    return null
                }
            },
            onFailure = { _ ->
                return null
            }
        )
        return null
    }
}