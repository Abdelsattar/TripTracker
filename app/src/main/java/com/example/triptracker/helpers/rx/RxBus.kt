package com.example.triptracker.helpers.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


interface BaseRxBus {
    fun publish(event: Any)
}

object RxBus : BaseRxBus {

    private val publisher = PublishSubject.create<Any>()

    override fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}