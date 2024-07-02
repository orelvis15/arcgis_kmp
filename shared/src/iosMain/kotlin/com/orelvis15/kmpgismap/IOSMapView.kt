package com.orelvis15.kmpgismap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.ArcGIS.AGSArcGISRuntimeEnvironment
import cocoapods.ArcGIS.AGSBasemapStyleArcGISNavigation
import cocoapods.ArcGIS.AGSFeatureLayer
import cocoapods.ArcGIS.AGSGeoView
import cocoapods.ArcGIS.AGSGeoViewTouchDelegateProtocol
import cocoapods.ArcGIS.AGSGraphic
import cocoapods.ArcGIS.AGSGraphicsOverlay
import cocoapods.ArcGIS.AGSLoadStatus
import cocoapods.ArcGIS.AGSLoadStatusFailedToLoad
import cocoapods.ArcGIS.AGSLoadStatusLoaded
import cocoapods.ArcGIS.AGSLoadStatusLoading
import cocoapods.ArcGIS.AGSLoadStatusNotLoaded
import cocoapods.ArcGIS.AGSLoadStatusUnknown
import cocoapods.ArcGIS.AGSLoadableBase
import cocoapods.ArcGIS.AGSMap
import cocoapods.ArcGIS.AGSMapView
import cocoapods.ArcGIS.AGSPictureMarkerSymbol
import cocoapods.ArcGIS.AGSPoint
import cocoapods.ArcGIS.AGSServiceFeatureTable
import cocoapods.ArcGIS.AGSViewpoint
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGPoint
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
class IOSMapView(
    private val mapConfig: GisMapConfig,
    private val onMapLoadSuccess: () -> Unit = {},
    private val onMapLoadFailed: (Throwable) -> Unit = {},
    val onClick: (lat: Double, lon: Double) -> Unit,
    nibName: String? = null,
    bundle: NSBundle? = null
) : UIViewController(nibName, bundle), AGSGeoViewTouchDelegateProtocol {

    private val mapView = AGSMapView()

    @Composable
    fun GetMap() {
        val coroutine = rememberCoroutineScope()
        AGSArcGISRuntimeEnvironment.APIKey = mapConfig.apiKey
        val viewpoint by remember {
            mutableStateOf(
                AGSViewpoint(
                    latitude = mapConfig.viewPoint.lat,
                    longitude = mapConfig.viewPoint.lon,
                    scale = mapConfig.viewPoint.scale
                )
            )
        }

        mapView.touchDelegate = this
        view.addSubview(mapView)

        val map = AGSMap(basemapStyle = AGSBasemapStyleArcGISNavigation)
        val url = NSURL(string = mapConfig.layerUrl)
        val featureTable = AGSServiceFeatureTable(url)
        val featureLayer = AGSFeatureLayer(featureTable)
        map.operationalLayers.add(featureLayer)

        LaunchedEffect(Unit) {
            coroutine.launch(Dispatchers.IO) {
                while (mapView.map?.loadStatus() == AGSLoadStatusLoading) {
                    when (mapView.map?.loadStatus()) {
                        AGSLoadStatusLoaded -> {
                            onMapLoadSuccess()
                        }

                        AGSLoadStatusNotLoaded ->{
                            onMapLoadFailed(Throwable(mapView.map?.loadStatus().toString()))
                        }

                        AGSLoadStatusFailedToLoad->{
                            onMapLoadFailed(Throwable(mapView.map?.loadStatus().toString()))
                        }
                    }
                }
            }
        }

        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.setViewpoint(viewpoint)
                mapView.map = map
                mapView
            },
            update = {
                mapView.setViewpoint(viewpoint)
                mapView.map = map
            }
        )
    }

    fun drawPin(point: AGSPoint, pin: ByteArray) {
        var graphicsOverlay: AGSGraphicsOverlay? =
            mapView.graphicsOverlays.find { (it as AGSGraphicsOverlay).overlayID == "pin" } as AGSGraphicsOverlay?

        val image = pin.usePinned {
            val data = NSData.dataWithBytes(it.addressOf(0), pin.size.toULong())
            UIImage(data = data)
        }

        if (graphicsOverlay == null) {
            graphicsOverlay = AGSGraphicsOverlay()
            graphicsOverlay.scaleSymbols = true
            graphicsOverlay.overlayID = "pin"
            mapView.graphicsOverlays.add(graphicsOverlay)
        }

        val pointAttributes: Map<Any?, Any> = hashMapOf()
        val node = AGSPictureMarkerSymbol.pictureMarkerSymbolWithImage(image)
        val graphic = AGSGraphic(point, node, pointAttributes)
        graphicsOverlay.graphics.clear()
        graphicsOverlay.graphics.add(graphic)

    }

    suspend fun centerMap(lat: Double, lon: Double, scale: Double? = null) {
        val point = AGSPoint.pointWithX(x = lon, y = lat, null)
        val currentScale = scale ?: mapView.mapScale
        val viewpoint = AGSViewpoint(center = point, scale = currentScale)
        mapView.setViewpointCenter(center = point, scale = currentScale) {}
        mapView.setViewpoint(viewpoint, duration = 0.5) { }
    }

    override fun geoView(
        geoView: AGSGeoView,
        didTapAtScreenPoint: CValue<CGPoint>,
        mapPoint: AGSPoint
    ) {
        mapView.screenToLocation(didTapAtScreenPoint)
        onClick(mapPoint.y, mapPoint.x)
    }
}