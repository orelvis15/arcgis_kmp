import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orelvis15.kmpgismap.GisMap
import com.orelvis15.kmpgismap.GisMapConfig
import com.orelvis15.kmpgismap.ViewPointConfig
import gislib.sample.composeapp.generated.resources.Res
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutine = rememberCoroutineScope()
        var loading by remember { mutableStateOf("loading") }
        var text by remember { mutableStateOf("") }
        Column {
            Text(modifier = Modifier.fillMaxWidth().height(50.dp), text = loading)
            Text(modifier = Modifier.fillMaxWidth().height(50.dp), text = text)

            val config = GisMapConfig(
                apiKey = "AAPK92a52386e9dc4166b3756e68932f4328U0FKyQYYyXLBybTfzdEkF134pT6CQEHV4ghSaLYuxqBAIMGmzeQfY79neYAObhTF",
                layerUrl = "https://ccpublicgis.cctexas.com/server01/rest/services/311_INCAP/311_INCAP/MapServer/29",
                viewPoint = ViewPointConfig(
                    lat = 27.796522238,
                    lon = -97.403100544,
                    scale = 100000.0
                )
            )

            val map = GisMap({
                loading = "success"
            }, {
                loading = "failed"
            })
            map.MapView(config) { lat, lon ->
                text = "$lat  -  $lon"
                coroutine.launch {
                    val image = Res.readBytes("files/pin.png")
                    map.drawPin(lat, lon, image)
                    map.centerMap(lat, lon)
                }
            }
        }
    }
}