package com.example.triptracker.data.remote.model

data class Data(
    val dropoffLocation: DropoffLocation,
    val intermediateStopLocations: ArrayList<IntermediateStopLocation>,
    val pickupLocation: PickupLocation,
    val status: String,
    val vehicleLocation: VehicleLocation
)