package com.example.triptracker.ui.home

import android.graphics.Color
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.triptracker.R
import com.example.triptracker.data.Resource
import com.example.triptracker.data.remote.model.Data
import com.example.triptracker.data.remote.model.VehicleLocation
import com.example.triptracker.databinding.ActivityMainBinding
import com.example.triptracker.helpers.Constants
import com.example.triptracker.helpers.Utils.AnimationUtils.carAnimator
import com.example.triptracker.helpers.Utils.MapUtils.getCarBitmap
import com.example.triptracker.helpers.Utils.MapUtils.getRotation
import com.example.triptracker.helpers.Utils.MapUtils.getStopBitmap
import com.example.triptracker.helpers.extentions.showToast
import com.example.triptracker.helpers.extentions.with
import com.example.triptracker.ui.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), OnMapReadyCallback {


    //region declare fields
    private lateinit var mMap: GoogleMap
    private val TAG = "MainActivity"
    private var pickUpLatLng: com.google.maps.model.LatLng? = null
    private var dropLatLng: com.google.maps.model.LatLng? = null
    private var vehicleLocation: com.google.maps.model.LatLng? = null
    private var previousLatLngFromServer: LatLng? = null
    private var currentLatLngFromServer: LatLng? = null

    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var stopMarker: Marker? = null
    private var movingCarMarker: Marker? = null

    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null

    private lateinit var rideUpdates: MainViewModel.RideUpdates
    private var rideStops = ArrayList<com.google.maps.model.LatLng>()
    private var showStops: Boolean = false
    private val BerlinLatLng = LatLng(52.5200, 13.4050)
    //endregion

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        animateCamera(BerlinLatLng)
    }

    override fun layoutId() = R.layout.activity_main

    override fun instantiateViewModel(): MainViewModel = with(viewModelFactory)

    override fun attachView() = viewModel.attachView(lifecycle)

    override fun setupView() {
        setUpMapView()
        binding.btnStartRide.setOnClickListener {
            binding.btnStartRide.text = getString(R.string.your_ride_is_coming)
            binding.btnStartRide.isEnabled = false
            startRideAndObserveChanges()
        }
    }

    /**
     * setup map view
     */
    private fun setUpMapView() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * start ride and observing changes to update the UI
     */
    private fun startRideAndObserveChanges() {

        rideUpdates = viewModel.startRide()

        rideUpdates.bookingOpened.observe(this, { data ->
            handelBookingOpened(data)
        })

        rideUpdates.vehicleLocation.observe(this, {
            handelVehicleLocationChanges(it)
        })

        rideUpdates.statusUpdated.observe(this, {
            handelStatusChanges(it)
        })

        rideUpdates.stopsChanges.observe(this, {
            handelStopsChanges(it)
        })

        rideUpdates.bookingClosed.observe(this, {
            handelBookingClosed(it)
        })

    }

    /**
     * Handel when we get booking opened update
     */
    private fun handelBookingOpened(data: Data) {
        Log.d(TAG, "Booking Opened $data ")
        pickUpLatLng =
            com.google.maps.model.LatLng(data.pickupLocation.lat, data.pickupLocation.lng)
        dropLatLng =
            com.google.maps.model.LatLng(data.dropoffLocation.lat, data.dropoffLocation.lng)
        rideStops.addAll(
            data.intermediateStopLocations.map {
                com.google.maps.model.LatLng(
                    it.lat,
                    it.lng
                )
            } as ArrayList<com.google.maps.model.LatLng>
        )

        vehicleLocation =
            com.google.maps.model.LatLng(data.vehicleLocation.lat, data.vehicleLocation.lng)

        runOnUiThread {

            binding.tvPickUp.text = data.pickupLocation.address
            binding.tvDropOff.text = data.dropoffLocation.address
            getDirections(vehicleLocation!!, pickUpLatLng!!, null)
        }

    }

    /**
     * Handel when we get vehicle Location update
     */
    private fun handelVehicleLocationChanges(newVehicleLocation: VehicleLocation) {

        Log.d(TAG, "vehicle Location update $newVehicleLocation ")

        runOnUiThread {
            vehicleLocation =
                com.google.maps.model.LatLng(newVehicleLocation.lat, newVehicleLocation.lng)
            updateCarLocation(LatLng(newVehicleLocation.lat, newVehicleLocation.lng))
        }
    }


    /**
     * Handel when we get ride status update
     */
    private fun handelStatusChanges(status: String) {
        Log.d(TAG, "status Updated $status ")

        when (status) {
            Constants.EVENT_STATUS_IN_VEHICLE -> {
                runOnUiThread {
                    binding.btnStartRide.text = getString(R.string.you_are_on_a_trip)
                    greyPolyLine?.remove()
                    blackPolyline?.remove()
                    blackPolyline?.points?.clear()

                    originMarker?.remove()
                    destinationMarker?.remove()
                    showStops = true
                    getDirections(pickUpLatLng!!, dropLatLng!!, rideStops)
                }
            }
            Constants.EVENT_STATUS_DROPP_OFF -> {
                binding.btnStartRide.text = getString(R.string.your_trip_ended)
            }

        }
    }

    /**
     * Handel when we get stops changes update
     */
    private fun handelStopsChanges(stops: List<VehicleLocation>) {
        Log.d(TAG, "Stops Changes $stops ")
        //TODO handle when stops change
    }

    /**
     * Handel when we get booking closed  update
     */
    private fun handelBookingClosed(it: Boolean?) {
        Log.d(TAG, "Booking Closed $it ")
        runOnUiThread {

            Toast.makeText(this, getString(R.string.your_trip_ended), LENGTH_SHORT).show()
        }
    }

    //region map functions

    /**
     * geting the direction between startAddress, endAddress with stops inside the route
     */
    private fun getDirections(
        startAddress: com.google.maps.model.LatLng,
        endAddress: com.google.maps.model.LatLng,
        stops: List<com.google.maps.model.LatLng>?
    ) {

        viewModel.getDirections(startAddress, endAddress, stops)
            .observe(this, { status ->
                Log.d(TAG, "getting direction $status ")
                when (status) {
                    is Resource.Success -> status.data?.let {
                        runOnUiThread {
                            showPath(it)
                        }
                    }
                    is Resource.Error -> {
                        status.errorMessage?.let { showToast(it) }
                    }
                    else -> {
                        showToast(getString(R.string.error_getting_directions))
                    }
                }

            })
    }

    /**
     * draw the path of list of LatLng on map
     */
    private fun showPath(latLngList: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(5f)
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.addAll(latLngList)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)

        originMarker = createMarkerAndAddToMap(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)

        destinationMarker = createMarkerAndAddToMap(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

        if (showStops) {
            rideStops.forEach {
                stopMarker = createMarkerAndAddToMap(LatLng(it.lat, it.lng))
                stopMarker?.setAnchor(0.5f, 0.5f)
            }
        }
    }

    /**
     * return location marker after creating and adding it to google maps
     */
    private fun createMarkerAndAddToMap(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getStopBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    /**
     * return car marker after creating and adding it to google maps
     */
    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(this))
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    /**
     * animate camera to specific LatLng
     */
    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    /**
     * update car marker location on the map based on the new LatLng
     */
    private fun updateCarLocation(latLng: LatLng) {
        Log.d(TAG, "updateCarLocation")

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