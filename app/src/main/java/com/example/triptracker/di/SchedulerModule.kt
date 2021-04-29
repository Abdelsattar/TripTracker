package com.example.triptracker.di

import dagger.Module
import dagger.Provides

@Module
class SchedulerModule {

    @Provides
    fun provideScheduler(): BaseSchedulerProvider =
        SchedulerProvider()

}