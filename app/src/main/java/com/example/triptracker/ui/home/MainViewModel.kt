package com.example.triptracker.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.triptracker.data.remote.model.ResultResource
import com.example.triptracker.data.remote.model.Trip
import com.example.triptracker.di.BaseSchedulerProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
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