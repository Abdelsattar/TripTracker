package com.example.triptracker.helpers.di

import com.example.triptracker.BuildConfig
import com.google.maps.GeoApiContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Sattar on 02.05.21.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun getGeoApiContext(): GeoApiContext = GeoApiContext.Builder()
        .apiKey(BuildConfig.GoogleMapsAPIKey)
        .build()


}