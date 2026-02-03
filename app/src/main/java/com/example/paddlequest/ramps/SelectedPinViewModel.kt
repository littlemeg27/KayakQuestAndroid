package com.example.paddlequest.ramps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SelectedPinViewModel : ViewModel()
{
    private val _selectedPin = MutableLiveData<LatLng?>()
    val selectedPin: LiveData<LatLng?> = _selectedPin

    fun setSelectedPin(latLng: LatLng?)
    {
        _selectedPin.value = latLng
    }

    fun clearSelectedPin()
    {
        _selectedPin.value = null
    }
}