package com.orelvis.gismap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.ArcGIS.AGSArcGISRuntimeEnvironment
import cocoapods.ArcGIS.AGSBasemapStyleArcGISNavigation
import cocoapods.ArcGIS.AGSFeatureLayer
import cocoapods.ArcGIS.AGSGeoViewTouchDelegateProtocol
import cocoapods.ArcGIS.AGSMap
import cocoapods.ArcGIS.AGSMapView
import cocoapods.ArcGIS.AGSServiceFeatureTable
import cocoapods.ArcGIS.AGSViewpoint
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
class IOSMapView(
    val onClick: () -> Unit = {}
)  {

    val layerUrl =
        "https://ccpublicgis.cctexas.com/server01/rest/services/311_INCAP/311_INCAP/MapServer/29"
//
//    init {
//        IOSMapView()
//    }

    init {


        //mapView.touchDelegate = this
        //view.addSubview(mapView)
    }

    @Composable
    fun GetMap() {
        AGSArcGISRuntimeEnvironment.APIKey =
            "AAPK92a52386e9dc4166b3756e68932f4328U0FKyQYYyXLBybTfzdEkF134pT6CQEHV4ghSaLYuxqBAIMGmzeQfY79neYAObhTF"

        val viewpoint by remember {
            mutableStateOf(
                AGSViewpoint(
                    27.796522238,
                    -97.403100544,
                    100000.0
                )
            )
        }


        val mapView = AGSMapView()

        val map = AGSMap(basemapStyle = AGSBasemapStyleArcGISNavigation)
        val url = NSURL(string = layerUrl)
        val featureTable = AGSServiceFeatureTable(url)
        val featureLayer = AGSFeatureLayer(featureTable)
        map.operationalLayers.add(featureLayer)

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


    /*override fun geoView(
        geoView: AGSGeoView,
        didTapAtScreenPoint: CValue<CGPoint>,
        mapPoint: AGSPoint
    ) {
        super.geoView(geoView, didTapAtScreenPoint, mapPoint)
        onClick()
    }*/
}