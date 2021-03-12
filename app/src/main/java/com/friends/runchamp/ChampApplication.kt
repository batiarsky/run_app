package com.friends.runchamp

import android.app.Application
import com.friends.runchamp.di.applicationModule
import com.friends.runchamp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class ChampApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@ChampApplication)
            modules(listOf(applicationModule, viewModelModule))
        }
    }
}