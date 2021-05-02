package com.example.triptracker.ui.home

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Observer
import com.example.triptracker.R
import com.example.triptracker.data.Resource
import com.example.triptracker.databinding.ActivityMainBinding
import com.example.triptracker.helpers.Utils.AnimationUtils.polyLineAnimator
import com.example.triptracker.helpers.Utils.MapUtils.getStopBitmap
import com.example.triptracker.helpers.extentions.with
import com.example.triptracker.ui.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "MainActivity"

    private var currentLatLng: LatLng? = null
    private var pickUpLatLng: com.google.maps.model.LatLng? = null
    private var dropLatLng: com.google.maps.model.LatLng? = null
    private val nearbyCabMarkerList = arrayListOf<Marker>()

    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getDirections()

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
            Log.d(TAG, "Booking Opened $it ")

        }

        updates.vehicleLocation.subscribe {
            Log.d(TAG, "vehicle Location update $it ")

        }

        updates.statusUpdated.subscribe {
            Log.d(TAG, "status Updated $it ")

        }

        updates.stopsChanges.subscribe {
            Log.d(TAG, "stops Changes ${it} ")

        }

        updates.bookingClosed.subscribe {
            Log.d(TAG, "Booking Closed $it ")

        }

    }

    //region map functions

    private fun getDirections() {

        pickUpLatLng = com.google.maps.model.LatLng(52.52663, 13.411632)
        dropLatLng = com.google.maps.model.LatLng(52.51376919675238, 13.393197655677795)

        viewModel.getDirections(pickUpLatLng!!, dropLatLng!!).observe(this, Observer { status ->

            when (status) {
                is Resource.Success -> status.data?.let {
                    runOnUiThread {
                        showPath(it)
                    }

                }
                is Resource.Error -> {
                    //todo show some error
                }
            }

        })

    }

    private fun showPath(latLngList: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        greyPolyLine = mMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(5f)
        blackPolylineOptions.color(Color.BLACK)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)

        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)

        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

        val polylineAnimator = polyLineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (greyPolyLine?.points!!.size * (percentValue / 100.0f)).toInt()
            blackPolyline?.points = greyPolyLine?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getStopBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    //endregion

}