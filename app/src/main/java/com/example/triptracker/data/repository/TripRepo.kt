package com.example.triptracker.data.repository

import com.example.triptracker.data.remote.service.TripService
import javax.inject.Inject

class TripRepo @Inject constructor(private val tripService: TripService) {

}