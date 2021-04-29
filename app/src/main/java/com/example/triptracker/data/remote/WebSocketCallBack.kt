package com.example.triptracker.data.remote

import android.util.Log
import com.example.triptracker.helpers.rx.RxBus
import com.example.triptracker.helpers.rx.RxEvent
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketCallBack : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        RxBus.publish(RxEvent.Message(text))
        Log.d("Server", "$text")
    }
}