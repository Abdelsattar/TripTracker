package com.example.triptracker.di

import com.example.triptracker.ui.home.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

 @Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity?
}