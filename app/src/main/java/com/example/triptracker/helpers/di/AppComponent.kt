package com.example.triptracker.helpers.di

import android.app.Application
import com.example.triptracker.ApplicationClass
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        SchedulerModule::class,
        NetworkModule::class,
        AppModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: ApplicationClass)
}
