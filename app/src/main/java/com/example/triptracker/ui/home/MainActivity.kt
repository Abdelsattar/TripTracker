package com.example.triptracker.ui.home

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Observer
import com.example.triptracker.R
import com.example.triptracker.data.Resource
import com.example.triptracker.databinding.ActivityMainBinding
import com.example.triptracker.helpers.Utils.AnimationUtils.carAnimator
import com.example.triptracker.helpers.Utils.AnimationUtils.polyLineAnimator
import com.example.triptracker.helpers.Utils.MapUtils.getCarBitmap
import com.example.triptracker.helpers.Utils.MapUtils.getRotation
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
    private var pickUpLatLng: com.google.maps.model.LatLng? = null
    private var dropLatLng: com.google.maps.model.LatLng? = null

    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var previousLatLngFromServer: LatLng? = null
    private var currentLatLngFromServer: LatLng? = null
    private var movingCarMarker: Marker? = null
    private lateinit var rideUpdates: MainViewModel.RideUpdates

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getDirections()

    }

    override fun layoutId() = R.layout.activity_main

    override fun instantiateViewModel(): MainViewModel = with(viewModelFactory)

    override fun attachView() = viewModel.attachView(lifecycle)

    override fun setupView() {
        setUpMapView()

        binding.btnStartRide.setOnClickListener {
            binding.btnStartRide.text = getString(R.string.your_ride_is_coming)
            binding.btnStartRide.isEnabled = false
            startRide()
        }
    }

    private fun setUpMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun bindViewModel() {
//        binding.view
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
                else -> {

                }
            }

        })
    }

    private fun startRide() {

        rideUpdates = viewModel.getRideUpdatesObservable()

        rideUpdates.bookingOpened.subscribe {
            Log.d(TAG, "Booking Opened $it ")

        }

        rideUpdates.vehicleLocation.subscribe(
            {
                Log.d(TAG, "vehicle Location update $it ")

                runOnUiThread {
                    updateCarLocation(LatLng(it.lat, it.lng))
                }
            }, { e ->
                Log.d(TAG, "vehicle Location update err ${e.localizedMessage} ")

            }
        )

        rideUpdates.statusUpdated.subscribe {
            Log.d(TAG, "status Updated $it ")

        }

        rideUpdates.stopsChanges.subscribe {
            Log.d(TAG, "stops Changes ${it} ")

        }

        rideUpdates.bookingClosed.subscribe {
            Log.d(TAG, "Booking Closed $it ")

        }

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

    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(this))
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    private fun moveCamera(latLng: LatLng?) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun updateCarLocation(latLng: LatLng) {
        Log.d(TAG, "updateCabLocation")

        if (movingCarMarker == null) {
            movingCarMarker = addCarMarkerAndGet(latLng)
        }

        if (previousLatLngFromServer == null) {

            currentLatLngFromServer = latLng
            previousLatLngFromServer = currentLatLngFromServer
            movingCarMarker?.position = currentLatLngFromServer
            movingCarMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLngFromServer)
        } else {

            previousLatLngFromServer = currentLatLngFromServer
            currentLatLngFromServer = latLng
            val valueAnimator = carAnimator()
            valueAnimator.addUpdateListener { va ->

                if (currentLatLngFromServer != null && previousLatLngFromServer != null) {

                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLngFromServer!!.latitude + (1 - multiplier)
                                * previousLatLngFromServer!!.latitude,
                        multiplier * currentLatLngFromServer!!.longitude + (1 - multiplier)
                                * previousLatLngFromServer!!.longitude
                    )
                    movingCarMarker?.position = nextLocation
                    movingCarMarker?.setAnchor(0.5f, 0.5f)
                    val rotation = getRotation(previousLatLngFromServer!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCarMarker?.rotation = rotation
                    }
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }

    //endregion

}