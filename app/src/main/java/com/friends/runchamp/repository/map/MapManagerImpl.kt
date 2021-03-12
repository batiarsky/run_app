package com.friends.runchamp.repository.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Looper
import com.friends.runchamp.activityService.MapManagerListener
import com.friends.runchamp.entity.Coordinate
import com.friends.runchamp.entity.GpsData
import com.friends.runchamp.entity.Route
import com.friends.runchamp.entity.RunningData
import com.friends.runchamp.repository.calculation.CalculationManager
import com.friends.runchamp.utils.extantions.paceToString
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.Completable
import timber.log.Timber

class MapManagerImpl(
    context: Context,
    private val calculationManager: CalculationManager
) :
    MapManager {

    companion object {
        private const val DEFAULT_ZOOM = 15F
        private const val PAUSED_POINTS = "paused_"
        private const val RUNNING_POINTS = "running_"
    }

    private val mRoutePoints = ArrayList<Coordinate>()
    private val mRoute = Route(gpsDataList = arrayListOf())
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private var mIsStarted = false
    private var mIsActivityTrackingStarted = false
    private var mLastKnownLocation: Location? = null
    private var mMapManagerListener: MapManagerListener? = null

    private var mFusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun getDeviceLocation(): Completable {
        return Completable.create {
            mFusedLocationProviderClient.lastLocation?.addOnSuccessListener { location: Location? ->
                Timber.d("Current location = $location")
                if (location != null) {
                    mLastKnownLocation = location
                    animateCamera(location)
                    it.onComplete()
                } else {
                    it.onError(Throwable())
                }
            }
        }
    }

    override fun onShowMyLocationClicked() {
        mLastKnownLocation?.let {
            animateCamera(it)
        }
    }

    override fun locationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 500
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onStartClicked() {
        mIsStarted = true
        mIsActivityTrackingStarted = true
        mMap.clear()
        mRoute.gpsDataList.clear()
        calculationManager.onActivityStarted()
        startLocationUpdates()
    }

    override fun onPauseClicked() {
        val lastPreviousPoint = mRoutePoints.last()
        val coordinates = ArrayList(mRoutePoints)

        mIsStarted = false
        mRoute.gpsDataList.add(
            GpsData(
                runningType = RUNNING_POINTS + mRoute.gpsDataList.size,
                coordinates = coordinates
            )
        )
        mRoutePoints.clear()
        mRoutePoints.add(lastPreviousPoint)
    }

    override fun onContinueClicked() {
        val lastPreviousPoint = mRoutePoints.last()
        val coordinates = ArrayList(mRoutePoints)

        mIsStarted = true
        mRoute.gpsDataList.add(
            GpsData(
                runningType = PAUSED_POINTS + mRoute.gpsDataList.size,
                coordinates = coordinates
            )
        )
        mRoutePoints.clear()
        mRoutePoints.add(lastPreviousPoint)
    }

    override fun onStopClicked() {
        if (mIsStarted) {
            onPauseClicked()
        } else {
            onContinueClicked()
        }
        mIsActivityTrackingStarted = false
        calculationManager.onActivityEnded()
        stopLocationUpdates()
        mMap.clear()
    }

    override fun locationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Timber.d("On new location: ${locationResult?.locations}")
                locationResult ?: return
                val lastLocation = locationResult.lastLocation
                calculationManager.onNewLocation(lastLocation)
                val distance = calculationManager.getDistance()
                val activityTime = calculationManager.getActivityTimeInSec()
                val paceString = calculationManager.getPacePerDistanceUnitInSec().paceToString()
                val burnedCalories = calculationManager.getBurnedCalories()

                mMapManagerListener?.onActivityData(
                    RunningData(
                        distance,
                        activityTime,
                        paceString,
                        burnedCalories
                    )
                )

                mLastKnownLocation = lastLocation
                mRoutePoints.add(
                    Coordinate(lat = lastLocation.latitude, lon = lastLocation.longitude)
                )
                drawLineOnStart()
            }
        }
    }

    override fun setGoogleMap(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun getRoute(): Route = mRoute

    override fun clearRoutes() = mRoute.gpsDataList.clear()

    override fun setMapManagerListener(mapManagerListener: MapManagerListener) {
        mMapManagerListener = mapManagerListener
    }

    override fun removeMapManagerListener() {
        mMapManagerListener = null
    }

    override fun isActivityStarted(): Boolean = mIsActivityTrackingStarted

    private fun drawLineOnStart() {
        if (mIsStarted) {
            val options = PolylineOptions().width(15f).color(Color.BLUE).geodesic(true)
            mRoutePoints.forEach {
                options.add(LatLng(it.lat, it.lon))
            }
            mMap.addPolyline(options)
        } else {
            val options = PolylineOptions().width(15f).color(Color.RED).geodesic(true)
            mRoutePoints.forEach {
                options.add(LatLng(it.lat, it.lon))
            }
            mMap.addPolyline(options)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationRequest()
        locationCallback()
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        mRoutePoints.clear()
    }

    private fun animateCamera(location: Location) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),
                DEFAULT_ZOOM
            )
        )
    }
}