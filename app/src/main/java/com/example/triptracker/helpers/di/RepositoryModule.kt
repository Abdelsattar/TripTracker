package com.example.triptracker.helpers.di

import com.example.triptracker.data.repository.TripRepo
import com.google.maps.GeoApiContext
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun getTripRepository(
        geoApiContext: GeoApiContext,
        okHttpClient: OkHttpClient,
        request: Request
    ): TripRepo {
        return TripRepo(geoApiContext, okHttpClient, request)
    }

}