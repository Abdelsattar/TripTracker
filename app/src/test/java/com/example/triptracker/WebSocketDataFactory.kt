package com.example.triptracker

import com.example.triptracker.WebSocketMessageFactory.getBookingOpenedEvent
import com.example.triptracker.WebSocketMessageFactory.getLocationChangedEvent
import com.example.triptracker.WebSocketMessageFactory.getRideStatusEvent
import com.example.triptracker.WebSocketMessageFactory.getStopsChangedEvent
import com.example.triptracker.data.remote.model.Data
import com.example.triptracker.data.remote.model.VehicleLocation
import com.example.triptracker.helpers.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

object WebSocketDataFactory {
    private val gson = Gson()

    fun getBookingOpenedData(): Data {
        val jsonObject = JSONObject(getBookingOpenedEvent())
        val data = jsonObject.get(Constants.KEY_DATA).toString()

        return gson.fromJson(data, Data::class.java)
    }

    fun getLocationChangedData(): VehicleLocation {
        val jsonObject = JSONObject(getLocationChangedEvent())
        val data = jsonObject.get(Constants.KEY_DATA).toString()

        return gson.fromJson(data, VehicleLocation::class.java)
    }

    fun getStopsChangedData(): List<VehicleLocation>? {
        val jsonObject = JSONObject(getStopsChangedEvent())
        val data = jsonObject.get(Constants.KEY_DATA).toString()

        val listType = object : TypeToken<List<VehicleLocation?>?>() {}.type
        return gson.fromJson<List<VehicleLocation>>(data, listType)
    }

    fun getRideStatusData(): String {
        val jsonObject = JSONObject(getRideStatusEvent())
        return jsonObject.get(Constants.KEY_DATA).toString()

    }

    fun getBookingClosedData() = true


}