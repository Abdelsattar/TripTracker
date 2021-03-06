package com.example.triptracker.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity<DB: ViewDataBinding, VM : BaseViewModel>: DaggerAppCompatActivity() {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun instantiateViewModel(): VM

    abstract fun attachView()

    abstract fun setupView()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: VM
        internal set

    lateinit var binding: DB
        internal  set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId())

        viewModel = instantiateViewModel()

        attachView()

        lifecycle.addObserver(viewModel)

        setupView()

    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected fun onViewDestroyed() {
        clearDisposeBag()
    }

    private fun clearDisposeBag() {
        compositeDisposable.clear()
    }
}