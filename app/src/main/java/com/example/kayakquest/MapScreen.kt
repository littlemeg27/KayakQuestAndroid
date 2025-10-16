import androidx.compose.runtime.Composable
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker

@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val selectedPin by viewModel.getSelectedPin().observeAsState()
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { viewModel.setSelectedPin(it) }
    )
    {
        selectedPin?.let { Marker(position = it, title = "Selected Pin") }
    }
}