package com.example.triptracker.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.triptracker.data.Resource
import com.example.triptracker.data.remote.WebSocketCallBack
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class TripRepo @Inject constructor(
    private val geoApiContext: GeoApiContext,
    private val okHttpClient: OkHttpClient,
    private val webSocket: Request
) {

    val TAG = "TripRepo"
    private var tripPath = arrayListOf<com.google.maps.model.LatLng>()

    fun getDirections(
        pickup: com.google.maps.model.LatLng,
        dropOff: com.google.maps.model.LatLng
    ): MutableLiveData<Resource<List<LatLng>>> {

        val directionLiveData = MutableLiveData<Resource<List<LatLng>>>()

        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(pickup)
        directionsApiRequest.destination(dropOff)
        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d(TAG, "onResult : ${Gson().toJson(result)}")

                val routeList = result.routes

                if (routeList.isEmpty()) {
                    directionLiveData.postValue(Resource.Error("routesNotAvailable"))
                } else {
                    for (route in routeList) {
                        val path = route.overviewPolyline.decodePath()

                        tripPath.addAll(path)
                        val pathPoints = arrayListOf<LatLng>()

                        for (point in tripPath) {
                            val latLng = LatLng(point.lat, point.lng)
                            pathPoints.add(latLng)
                        }
                        directionLiveData.postValue(Resource.Success(pathPoints))
                    }
                }
            }

            override fun onFailure(e: Throwable?) {
                Log.d(TAG, "onFailure : ${e?.message}")
                directionLiveData.postValue(Resource.Error(e?.localizedMessage))
            }
        }
        )
        return directionLiveData
    }


    fun connectToRideWebSockets() {
        Log.d(TAG, "connectToRideWebSockets")
        okHttpClient.newWebSocket(webSocket, WebSocketCallBack())

    }
}