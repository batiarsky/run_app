package com.friends.runchamp.repository.calculation

import android.location.Location
import com.friends.runchamp.utils.DistanceType

interface CalculationManager {
    fun getDistance(): Float
    fun onNewLocation(location: Location)
    fun onActivityStarted()
    fun onActivityEnded()
    fun getActivityTimeInMin(): Long
    fun getActivityTimeInSec(): Long
    fun getPacePerDistanceUnitInSec(): Double
    fun getBurnedCalories(): Double
    fun getComparisonDifference(): Long
    fun setDistanceType(distanceType: DistanceType)
}