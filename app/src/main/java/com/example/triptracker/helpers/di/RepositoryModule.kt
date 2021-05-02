package com.example.triptracker.helpers.di

import com.example.triptracker.data.repository.TripRepo
import com.google.maps.GeoApiContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun getTripRepository(geoApiContext: GeoApiContext): TripRepo {
        return TripRepo(geoApiContext)
    }

}