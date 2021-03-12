package com.friends.runchamp.view.start

import androidx.lifecycle.*
import com.friends.runchamp.repository.calculation.CalculationManager
import com.friends.runchamp.repository.db.ChampDbRepository
import com.friends.runchamp.repository.map.MapManager
import com.friends.runchamp.utils.DistanceType
import com.google.android.gms.maps.GoogleMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StartViewModel(
    private val mapManager: MapManager,
    private val champDbRepository: ChampDbRepository,
    private val calculationManager: CalculationManager
) : ViewModel(), LifecycleObserver {

    private val disposables = CompositeDisposable()
    val mIsActivityStartedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun prepareToWork() {
        getDistanceType()
    }

    fun getDeviceLocation() {
        val mapManagerDisposable = mapManager.getDeviceLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.d("Current location request completed")
                },
                {
                    Timber.e(it)
                }
            )
        disposables.add(mapManagerDisposable)
    }

    fun setGoogleMap(googleMap: GoogleMap) {
        mapManager.setGoogleMap(googleMap)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposables.dispose()
    }

    fun onStartClicked() {
        mapManager.onStartClicked()
    }

    fun onPauseClicked() {
        mapManager.onPauseClicked()
    }

    fun onContinueClicked() {
        mapManager.onContinueClicked()
    }

    fun onStopClicked() {
        mapManager.onStopClicked()
        val route = mapManager.getRoute()
        val insertRoutesDisposable = champDbRepository.getRoutesDao().insertRoute(route)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mapManager.clearRoutes()
                },
                {
                    Timber.e(it)
                }
            )
        disposables.add(insertRoutesDisposable)
    }

    fun onShowMyLocationClicked() {
        mapManager.onShowMyLocationClicked()
    }

    private fun getDistanceType() {
//      todo: Here we should to get distance type from local db
        calculationManager.setDistanceType(DistanceType.KILOMETERS)
    }

    fun isActivityTrackingStarted() {
        mIsActivityStartedLiveData.postValue(mapManager.isActivityStarted())
    }
}