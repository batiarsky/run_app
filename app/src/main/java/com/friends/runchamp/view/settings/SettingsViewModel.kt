package com.friends.runchamp.view.settings

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

class SettingsViewModel : ViewModel(), LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposables.dispose()
    }
}