package com.example.paddlequest.ramps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SelectedPinViewModel : ViewModel()
{
    private val _selectedPin = MutableLiveData<LatLng?>()

    fun getSelectedPin(): LiveData<LatLng?> = _selectedPin

    fun setSelectedPin(pin: LatLng?)
    {
        _selectedPin.value = pin
    }
}