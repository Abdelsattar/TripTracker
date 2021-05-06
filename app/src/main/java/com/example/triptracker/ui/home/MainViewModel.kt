package com.example.triptracker.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.triptracker.data.Resource
import com.example.triptracker.data.remote.model.Data
import com.example.triptracker.data.remote.model.VehicleLocation
import com.example.triptracker.data.repository.TripRepo
import com.example.triptracker.helpers.Constants.EVENT_BOOKING_CLOSED
import com.example.triptracker.helpers.Constants.EVENT_BOOKING_OPENED
import com.example.triptracker.helpers.Constants.EVENT_LOCATION_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_STATUS_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_STOPS_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_TYPE
import com.example.triptracker.helpers.Constants.KEY_DATA
import com.example.triptracker.helpers.rx.RxBus
import com.example.triptracker.helpers.rx.RxEvent
import com.example.triptracker.ui.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.model.LatLng
import org.json.JSONObject
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val tripRepo: TripRepo
) : BaseViewModel() {

    private val bookingOpenedLiveData = MutableLiveData<Data>()
    private val vehicleLocationLiveData = MutableLiveData<VehicleLocation>()
    private val statusUpdatedLiveData = MutableLiveData<String>()
    private val stopsChangesLiveData = MutableLiveData<List<VehicleLocation>>()
    private val bookingClosedLiveData = MutableLiveData<Boolean>()

    init {
        val publisher = RxBus.listen(RxEvent.Message::class.java)
            .map { it.message.toString() }
            .subscribe({ data ->
                parsingMessageAndPublish(data)
            }, { e ->
                println("publisher err ${e.localizedMessage}")
            })
        compositeDisposable.add(publisher)
    }


    fun parsingMessageAndPublish(event: String?) {
        val jsonObject = JSONObject(event)
        val eventType = jsonObject.getString(EVENT_TYPE)

        val data = jsonObject.get(KEY_DATA).toString()
        val gson = Gson()
        when (eventType) {
            EVENT_BOOKING_OPENED -> {
                bookingOpenedLiveData.postValue(gson.fromJson(data, Data::class.java))
            }
            EVENT_LOCATION_UPDATE -> {
                val location = gson.fromJson(data, VehicleLocation::class.java)
                vehicleLocationLiveData.postValue(location)
            }
            EVENT_STATUS_UPDATE -> {
                statusUpdatedLiveData.postValue(data)
            }
            EVENT_STOPS_UPDATE -> {
                val listType = object : TypeToken<List<VehicleLocation?>?>() {}.type
                val vehicleLocationList = gson.fromJson<List<VehicleLocation>>(data, listType)

                stopsChangesLiveData.postValue(vehicleLocationList)
            }
            EVENT_BOOKING_CLOSED -> {
                bookingClosedLiveData.postValue(true)
            }
        }
    }

    fun getRideUpdatesObservable(): RideUpdates {
        tripRepo.connectToRideWebSockets()
        return RideUpdates(
            bookingOpenedLiveData,
            vehicleLocationLiveData,
            statusUpdatedLiveData,
            stopsChangesLiveData,
            bookingClosedLiveData
        )
    }

    fun getDirections(
        pickup: LatLng,
        dropOff: LatLng,
        rideStops: List<LatLng>?
    ): MutableLiveData<Resource<List<com.google.android.gms.maps.model.LatLng>>> {
        return tripRepo.getDirections(pickup, dropOff, rideStops)
    }

    class RideUpdates(
        val bookingOpened: MutableLiveData<Data>,
        val vehicleLocation: MutableLiveData<VehicleLocation>,
        val statusUpdated: MutableLiveData<String>,
        val stopsChanges: MutableLiveData<List<VehicleLocation>>,
        val bookingClosed: MutableLiveData<Boolean>
    )
}