package com.example.paddlequest.ramps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SelectedPinViewModel : ViewModel()
{
    private val _selectedPin = MutableLiveData<LatLng?>()
    val selectedPin: LiveData<LatLng?> = _selectedPin

    private val _selectedState = MutableLiveData<String?>()
    val selectedState: LiveData<String?> = _selectedState

    fun setSelectedPin(latLng: LatLng?)
    {
        _selectedPin.value = latLng
    }

    fun setSelectedState(state: String?)
    {
        _selectedState.value = state
    }

    fun clearSelectedPin()
    {
        _selectedPin.value = null
    }

    fun clearAll()
    {
        _selectedPin.value = null
        _selectedState.value = null
    }
}