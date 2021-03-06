package com.example.triptracker.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.triptracker.data.Resource
import com.example.triptracker.data.remote.WebSocketRideCallBack
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
    private val webSocketRequest: Request
) {

    val TAG = "TripRepo"


    /**
     * return a MutableLiveData of list of latlng that have the path between pickup and dropOff with
     * number of stops between them
     */
    fun getDirections(
        pickup: com.google.maps.model.LatLng,
        dropOff: com.google.maps.model.LatLng,
        stops: List<com.google.maps.model.LatLng>? = null
    ): MutableLiveData<Resource<List<LatLng>>> {

        val tripPath = arrayListOf<com.google.maps.model.LatLng>()
        val directionLiveData = MutableLiveData<Resource<List<LatLng>>>()
        directionLiveData.value = Resource.Loading()

        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(pickup)
        directionsApiRequest.destination(dropOff)
        directionsApiRequest.optimizeWaypoints(true)

        //add stops as a way points
        var wayPointsStr: String
        stops?.let {

            if (stops.size == 1)
                directionsApiRequest.waypoints(stops[0])
            else {
                wayPointsStr = "${it[0]}"

                for (i in it.indices) {
                    wayPointsStr = if (i != 0) {
                        "$wayPointsStr|${it[i]}"
                    } else {
                        "${it[i]}"
                    }
                }
                directionsApiRequest.waypoints(wayPointsStr)
            }
        }

        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d(TAG, "onResult : ${Gson().toJson(result)}")

                val routeList = result.routes

                if (routeList.isEmpty()) {
                    directionLiveData.postValue(Resource.Error("Route not available"))
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
                directionLiveData.postValue(Resource.Error(e?.localizedMessage))
            }
        }
        )
        return directionLiveData
    }


    /**
     * connect to Ride webSocket and open a connection
     */
    fun connectToRideWebSockets() {
        okHttpClient.newWebSocket(webSocketRequest, WebSocketRideCallBack())
    }
}