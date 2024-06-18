import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.orelvis.gismap.GisMap
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        GisMap().getMap()
    }
}