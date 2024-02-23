package com.example.plantlets.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.plantlets.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.plantlets.databinding.ActivityPinLocationMapBinding
import com.example.plantlets.utils.Constants.LATITUDE
import com.example.plantlets.utils.Constants.LONGITUDE
import com.example.plantlets.viewmodels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator

class PinLocationMapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapViewModel: MapViewModel
    private lateinit var binding: ActivityPinLocationMapBinding
    private var mMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinLocationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val resultIntent = Intent()
                mapViewModel.pinLocation.apply {
                    resultIntent.apply {
                        putExtra(LATITUDE, latitude)
                        putExtra(LONGITUDE, longitude)
                    }
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setupMap()
        setupListeners()

    }

    private fun setupListeners() {
        binding.btnUsePinLocation.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }




    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val pakistan = LatLng( 31.5204, 74.3587)
        mMap?.apply{
            moveCamera(CameraUpdateFactory.newLatLng(pakistan))
        }
        updateMap()
    }


    private fun updateMap() {
        val customIcon = getCustomMapIcon()
        mMap?.apply {
            getCurrentLocation { locationLatLng ->
                mapViewModel.pinLocation = locationLatLng
                mapViewModel.pinLocation.apply {
                    addMarker(MarkerOptions().position(this).icon(customIcon))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(this, 15f))
                }
            }

            setOnMapClickListener { point ->
                clear()
                mapViewModel.pinLocation = point
                addMarker(MarkerOptions().position(point).icon(customIcon))
            }
        }
    }

    private fun getCurrentLocation(callback: (locationLatLng: LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        callback(
                            LatLng(location.latitude, location.longitude)
                        )

                    }
                }
        }
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestLocationPermissionLauncher.launch(permissions)
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isGranted = permissions.all { it.value }
            if (isGranted) {
                updateMap()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    private fun getCustomMapIcon(): BitmapDescriptor {
        val iconGenerator = IconGenerator(this)
        iconGenerator.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_marker_green))
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }


}