package com.example.triptracker.helpers.di

import com.example.triptracker.ui.home.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity?
}