package com.example.triptracker.helpers.di

import com.example.triptracker.data.remote.service.TripService
import com.example.triptracker.data.repository.TripRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun getTripRepository(tripService: TripService): TripRepo {
        return TripRepo(tripService)
    }

}