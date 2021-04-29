package com.example.triptracker.ui.home

import androidx.lifecycle.ViewModel
import com.example.triptracker.helpers.di.BaseSchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val schedulerProvider: BaseSchedulerProvider
) : ViewModel() {


    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}