package com.example.triptracker.ui.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

interface ViewModelLifecycle: LifecycleObserver {
    fun attachView(lifecycle: Lifecycle)
}