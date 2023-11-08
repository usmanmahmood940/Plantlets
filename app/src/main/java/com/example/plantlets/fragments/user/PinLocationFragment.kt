package com.example.plantlets.fragments.user

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.plantlets.R
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.databinding.FragmentPinLocationBinding
import com.example.plantlets.utils.Constants.LATITUDE
import com.example.plantlets.utils.Constants.LOCATION_DATA
import com.example.plantlets.utils.Constants.LONGITUDE
import com.example.plantlets.viewmodels.user.PinLocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator


class PinLocationFragment : Fragment() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: FragmentPinLocationBinding
    private lateinit var pinLocationViewModel: PinLocationViewModel
    private var myGoogleMap: GoogleMap? = null

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { it.value }
            if (isGranted) {
                updateMap()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPinLocationBinding.inflate(inflater, container, false)
        pinLocationViewModel = ViewModelProvider(this).get(PinLocationViewModel::class.java)
        (requireActivity() as UserHomeActivity).hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupBackButton()
        setupUseLocationButton()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            myGoogleMap = googleMap
            updateMap()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun setupBackButton() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val previousFragment = findNavController().previousBackStackEntry
                if (previousFragment != null) {
                    val data = Bundle().apply {
                        pinLocationViewModel.pinLocation.apply {
                            putDouble(LATITUDE, latitude)
                            putDouble(LONGITUDE, longitude)
                        }
                    }
                    previousFragment.savedStateHandle.set(LOCATION_DATA, data)
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun setupUseLocationButton() {
        binding.btnUseLocation.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun updateMap() {
        val customIcon = getCustomMapIcon()
        myGoogleMap?.apply {
            getCurrentLocation { locationLatLng ->
                pinLocationViewModel.pinLocation = locationLatLng
                pinLocationViewModel.pinLocation.apply {
                    addMarker(MarkerOptions().position(this).icon(customIcon))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(this, 15f))
                }
            }

            setOnMapClickListener { point ->
                clear()
                pinLocationViewModel.pinLocation = point
                addMarker(MarkerOptions().position(point).icon(customIcon))
            }
        }
    }

    private fun getCurrentLocation(callback: (locationLatLng: LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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

    private fun getCustomMapIcon(): BitmapDescriptor {
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_location_pin
            )
        )
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

}