package com.example.triptracker.helpers.di

import com.example.triptracker.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    fun provideWebSocketRequest(): Request {
       return  Request.Builder()
            .url(BuildConfig.WebsocketEndpoint)
            .build()
    }

}