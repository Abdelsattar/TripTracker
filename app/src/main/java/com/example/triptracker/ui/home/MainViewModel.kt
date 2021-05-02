package com.example.triptracker.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.triptracker.data.Resource
import com.example.triptracker.data.remote.model.Data
import com.example.triptracker.data.remote.model.VehicleLocation
import com.example.triptracker.data.repository.TripRepo
import com.example.triptracker.helpers.Constants.DATA
import com.example.triptracker.helpers.Constants.EVENT_BOOKING_CLOSED
import com.example.triptracker.helpers.Constants.EVENT_BOOKING_EVENT
import com.example.triptracker.helpers.Constants.EVENT_LOCATION_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_STATUS_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_STOPS_UPDATE
import com.example.triptracker.helpers.Constants.EVENT_TYPE
import com.example.triptracker.helpers.rx.RxBus
import com.example.triptracker.helpers.rx.RxEvent
import com.example.triptracker.ui.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.model.LatLng
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val tripRepo: TripRepo
) : BaseViewModel() {

    val TAG = "MainViewModel"
    private val receivedMessageSubject: PublishSubject<String> = PublishSubject.create()

    private val bookingOpenedSubject: PublishSubject<Data> = PublishSubject.create()
    private val vehicleLocationSubject: PublishSubject<VehicleLocation> = PublishSubject.create()
    private val statusUpdatedSubject: PublishSubject<String> = PublishSubject.create()
    private val stopsChangesSubject: PublishSubject<List<VehicleLocation>> = PublishSubject.create()
    private val bookingClosedSubject: PublishSubject<Boolean> = PublishSubject.create()

    override fun onViewAttached() {
        compositeDisposable.add(RxBus.listen(RxEvent.Message::class.java)
            .map { it.message.toString() }
            .subscribe {
                receivedMessageSubject.onNext(it)
                parsingMessage(it)
            }
        )
    }

    private fun parsingMessage(data: String) {
        val jsonObject = JSONObject(data)
        val eventType = jsonObject.getString(EVENT_TYPE)
        Log.d(TAG, "parsingMessage ${eventType}")

        val data: String? = jsonObject.getString(DATA)
        Log.e(TAG, data.toString())
        val gson = Gson()
        when (eventType) {
            EVENT_BOOKING_EVENT -> {
                Log.d(TAG, "EVENT_BOOKING_EVENT")

                bookingOpenedSubject.onNext(gson.fromJson(data, Data::class.java))
            }
            EVENT_LOCATION_UPDATE -> {
                Log.d(TAG, "EVENT_LOCATION_UPDATE  ${data.toString()}")

                vehicleLocationSubject.onNext(gson.fromJson(data, VehicleLocation::class.java))
            }
            EVENT_STATUS_UPDATE -> {
                Log.d(TAG, "EVENT_STOPS_UPDATE ")

                statusUpdatedSubject.onNext(data)
            }
            EVENT_STOPS_UPDATE -> {
                val listType = object : TypeToken<List<VehicleLocation?>?>() {}.type
                val vehicleLocationList = gson.fromJson<List<VehicleLocation>>(data, listType)

                Log.d(TAG, "EVENT_STOPS_UPDATE ${vehicleLocationList}")

                stopsChangesSubject.onNext(vehicleLocationList)
            }
            EVENT_BOOKING_CLOSED -> {
                Log.d(TAG, "EVENT_STOPS_UPDATE ${EVENT_BOOKING_CLOSED}")

                bookingClosedSubject.onNext(true)
            }
            else -> {
                Log.e(TAG, "Else ->  ${eventType}")
            }

        }
    }

    class Updates(
        val bookingOpened: Observable<Data>,
        val vehicleLocation: Observable<VehicleLocation>,
        val statusUpdated: Observable<String>,
        val stopsChanges: Observable<List<VehicleLocation>>,
        val bookingClosed: Observable<Boolean>
    )

    fun getUpdatesObservable(): Updates {

        val bookingOpenedObservable = bookingOpenedSubject.share()
        val vehicleLocationObservable = vehicleLocationSubject.share()
        val statusUpdatedObservable = statusUpdatedSubject.share()
        val stopsChangesObservable = stopsChangesSubject.share()
        val bookingClosedObservable = bookingClosedSubject.share()

        return Updates(
            bookingOpenedObservable,
            vehicleLocationObservable,
            statusUpdatedObservable,
            stopsChangesObservable,
            bookingClosedObservable
        )

    }

    fun getDirections(
        pickup: LatLng,
        dropOff: LatLng
    ): MutableLiveData<Resource<List<com.google.android.gms.maps.model.LatLng>>> {
        return tripRepo.getDirections(pickup,dropOff)
    }
}