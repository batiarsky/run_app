package com.friends.runchamp.repository.map

import com.friends.runchamp.activityService.MapManagerListener
import com.friends.runchamp.entity.Route
import com.google.android.gms.maps.GoogleMap
import io.reactivex.Completable

interface MapManager {
    fun getDeviceLocation(): Completable
    fun onShowMyLocationClicked()
    fun locationRequest()
    fun onStartClicked()
    fun locationCallback()
    fun setGoogleMap(googleMap: GoogleMap)
    fun onStopClicked()
    fun onPauseClicked()
    fun onContinueClicked()
    fun getRoute(): Route
    fun clearRoutes()
    fun setMapManagerListener(mapManagerListener: MapManagerListener)
    fun removeMapManagerListener()
    fun isActivityStarted(): Boolean
}