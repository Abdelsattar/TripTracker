package com.example.triptracker.data.remote.model

data class Data(
    val dropoffLocation: DropoffLocation,
    val intermediateStopLocations: List<IntermediateStopLocation>,
    val pickupLocation: PickupLocation,
    val status: String,
    val vehicleLocation: VehicleLocation
)