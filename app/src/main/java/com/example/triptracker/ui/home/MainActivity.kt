package com.example.triptracker.ui.home

import android.util.Log
import com.example.triptracker.R
import com.example.triptracker.databinding.ActivityMainBinding
import com.example.triptracker.helpers.extentions.with
import com.example.triptracker.ui.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "MainActivity"

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    override fun layoutId() = R.layout.activity_main

    override fun instantiateViewModel(): MainViewModel = with(viewModelFactory)

    override fun attachView() = viewModel.attachView(lifecycle)

    override fun setupView() {
        setUpMapView()
    }

    private fun setUpMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun bindViewModel() {

        val updates = viewModel.getUpdatesObservable()

        updates.bookingOpened.subscribe {
            Log.d(TAG,"Booking Opened $it ")

        }
        updates.vehicleLocation.subscribe {
            Log.d(TAG,"vehicle Location update $it ")

        }
        updates.statusUpdated.subscribe {
            Log.d(TAG,"status Updated $it ")

        }
        updates.stopsChanges.subscribe {
            Log.d(TAG,"stops Changes ${it} ")

        }
        updates.bookingClosed.subscribe {
            Log.d(TAG,"Booking Closed $it ")

        }
    }


}