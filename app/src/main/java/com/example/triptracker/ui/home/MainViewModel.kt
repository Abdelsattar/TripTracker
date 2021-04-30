package com.example.triptracker.ui.home

import com.example.triptracker.helpers.di.BaseSchedulerProvider
import com.example.triptracker.helpers.rx.RxBus
import com.example.triptracker.helpers.rx.RxEvent
import com.example.triptracker.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val schedulerProvider: BaseSchedulerProvider
) : BaseViewModel() {

    private val receivedMessageSubject: PublishSubject<String> = PublishSubject.create()

    override fun onViewAttached() {
        compositeDisposable.add(RxBus.listen(RxEvent.Message::class.java)
            .map { it.message.toString() }
            .subscribe {
                receivedMessageSubject.onNext(it)
            }
        )
    }

    fun getUpdates(): Observable<String> {
        return receivedMessageSubject
            .map { it }
            .share()

    }

}