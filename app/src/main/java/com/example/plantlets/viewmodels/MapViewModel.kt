package com.example.plantlets.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    lateinit var pinLocation : LatLng
}