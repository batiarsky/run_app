package com.friends.runchamp.view.statistics

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.friends.runchamp.repository.db.ChampDbRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StatisticsViewModel(
    private val champDbRepository: ChampDbRepository
) : ViewModel(), LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposables.dispose()
    }

    fun getRunningData() {
        val insertRoutesDisposable = champDbRepository.getRoutesDao().getAllRoutes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    println(it)
                },
                {
                    Timber.e(it)
                }
            )
        disposables.add(insertRoutesDisposable)
    }
}