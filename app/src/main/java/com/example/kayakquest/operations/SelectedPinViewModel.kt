package com.example.kayakquest.operations

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectedPinViewModel : ViewModel()
{
    private val _selectedPin = MutableStateFlow<LatLng?>(null)
    val selectedPin: StateFlow<LatLng?> = _selectedPin.asStateFlow()
    fun setSelectedPin(latLng: LatLng) { _selectedPin.value = latLng }
}