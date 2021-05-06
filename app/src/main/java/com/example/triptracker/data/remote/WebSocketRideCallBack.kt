package com.example.triptracker.data.remote

import com.example.triptracker.helpers.rx.RxBus
import com.example.triptracker.helpers.rx.RxEvent
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketRideCallBack : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {
        RxBus.publish(RxEvent.Message(text))
    }
}