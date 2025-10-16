class SelectedPinViewModel : ViewModel()
{
    private val _selectedPin = MutableStateFlow<LatLng?>(null)
    val selectedPin: StateFlow<LatLng?> = _selectedPin.asStateFlow()
    fun setSelectedPin(latLng: LatLng) { _selectedPin.value = latLng }
}