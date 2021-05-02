package com.example.triptracker

import android.app.Application
import com.example.triptracker.data.remote.WebSocketCallBack
import com.example.triptracker.helpers.di.DaggerAppComponent
import com.example.triptracker.ui.home.MainActivity
import com.google.maps.GeoApiContext
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApplicationClass : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private var socket: WebSocket? = null

    override fun onCreate() {
        super.onCreate()

        initDagger()
        initSocket()
    }

    private fun initDagger() {
        DaggerAppComponent.builder()
            .application(this)
            .build().inject(this)
    }

    private fun initSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(BuildConfig.WebsocketEndpoint)
            .build()

        socket = client.newWebSocket(request, WebSocketCallBack())
    }

    fun sendMessage(message: String) {
        socket?.send(message)
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

} 