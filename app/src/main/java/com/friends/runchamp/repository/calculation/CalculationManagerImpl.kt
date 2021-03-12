package com.friends.runchamp.repository.calculation

import android.location.Location
import com.friends.runchamp.utils.DistanceType
import com.friends.runchamp.utils.Gender
import org.joda.time.DateTime
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.floor

class CalculationManagerImpl : CalculationManager {
    companion object {
        private const val KILOMETER_IN_METERS = 1000
        private const val MILE_IN_METERS = 1609
    }

    private val mPacePerKilometerCollection = mutableListOf<Float>()
    private val mPacePerMileCollection = mutableListOf<Float>()
    private var mPreviousLocation: Location? = null
    private var mDistanceInMeters: Float = 0f
    private var mDistancePerUnit: Float = 0f
    private var mTimePerDistanceType: Long = 0L
    private var mPreviousTimePerDistanceType: Long = 0L
    private var mStartTime: Long = 0L
    private var mEndTime: Long = 0L
    private var mDistanceType: DistanceType = DistanceType.KILOMETERS
    private var mUnitDistance: Int = 0
    private var mMetCalculation =
        RunningCalculation()

    init {
        mUnitDistance = if (mDistanceType == DistanceType.KILOMETERS) {
            KILOMETER_IN_METERS
        } else {
            MILE_IN_METERS
        }
    }

    override fun getDistance(): Float {
        val currentDistance = if (mDistanceType == DistanceType.KILOMETERS) {
            mDistanceInMeters / KILOMETER_IN_METERS
        } else {
            mDistanceInMeters / MILE_IN_METERS
        }

        return roundFloat(currentDistance)
    }

    override fun onNewLocation(location: Location) {
        val distanceFromPreviousPoint = mPreviousLocation?.distanceTo(location) ?: 0f
        mDistanceInMeters += distanceFromPreviousPoint
        updateDistancePerKilometer(distanceFromPreviousPoint)
        updateTimePerKilometer()
        mPreviousLocation = location
    }

    override fun onActivityStarted() {
        mStartTime = DateTime().millis
        mPreviousTimePerDistanceType = DateTime().millis
    }

    override fun onActivityEnded() {
        mEndTime = DateTime().millis
    }

    override fun getPacePerDistanceUnitInSec(): Double {
        val pacePerDistanceUnitInSec = mTimePerDistanceType / (mDistancePerUnit / mUnitDistance)

        return DecimalFormat("#.##").run {
            if (pacePerDistanceUnitInSec != Float.POSITIVE_INFINITY) {
                format(pacePerDistanceUnitInSec).toDouble()
            } else {
                0.0
            }
        }
    }

    override fun getActivityTimeInMin(): Long =
        TimeUnit.MILLISECONDS.toMinutes(DateTime().millis - mStartTime)

    override fun getBurnedCalories(): Double =
        calculateEnergyExpenditure(
            1.76f,
            DateTime().millis,
            72f,
            Gender.MALE.value,
            getActivityTimeInSec()
        )


    override fun getActivityTimeInSec(): Long =
        TimeUnit.MILLISECONDS.toSeconds(DateTime().millis - mStartTime)

    //    todo: need to implement
    override fun getComparisonDifference(): Long = 0L

    override fun setDistanceType(distanceType: DistanceType) {
        mDistanceType = distanceType
    }

    private fun roundFloat(value: Float) = floor(value * 100) / 100

    /**
     * Calculated the energy expenditure for an activity. Adapted from the following website https://sites.google.com/site/compendiumofphysicalactivities/corrected-mets
     *
     * @param height               The height in metres.
     * @param age                  The date of birth.
     * @param weight               The weight of the user.
     * @param gender               The gender of the user.
     * @param durationInSeconds    The duration of the activity in seconds.
     * @return The number of calories burnt (kCal)
     */
    private fun calculateEnergyExpenditure(
        height: Float,
        age: Long,
        weight: Float,
        gender: Int,
        durationInSeconds: Long
    ): Double {
        val constant = 3.5f
        val ageCalculated = 28f
        val harrisBenedictRmR = convertKilocaloriesToMlKmin(
            mMetCalculation.getHarrisBenedictRmr(
                gender,
                weight,
                ageCalculated,
                convertMetresToCentimetre(height)
            ), weight
        )
        val hours: Double = getActivityTimeInSec().toDouble() / 3600.toDouble()
        val speed = getDistance() / hours
        val metValue = getMetForActivity(speed)
        val correctedMets = metValue * (constant / harrisBenedictRmR)

        return correctedMets * hours * weight
    }

    private fun getMetForActivity(speed: Double): Double {
        return if (mDistanceType == DistanceType.KILOMETERS) {
            mMetCalculation.getMetFromKilometers(speed)
        } else {
            mMetCalculation.getMetFromMiles(speed)
        }
    }

    private fun convertKilocaloriesToMlKmin(kilocalories: Float, weightKgs: Float): Float {
        return ((((kilocalories / 1440) / 5) / (weightKgs)) * 1000)
    }

    private fun convertMetresToCentimetre(metres: Float): Float = metres * 100

    private fun updateDistancePerKilometer(distanceFromPreviousPoint: Float) {
        mDistancePerUnit += distanceFromPreviousPoint

        if (mDistancePerUnit >= mUnitDistance) {
            resetIndicators()
        }
    }

    private fun resetIndicators() {
        val localDistancePerKilometer = mDistancePerUnit
        val localTimePerKilometer = mTimePerDistanceType
        mDistancePerUnit -= floor(mDistancePerUnit)
        resetTimePerKilometer(mDistancePerUnit / localDistancePerKilometer)
        addPaceToCollection(localTimePerKilometer, localDistancePerKilometer)
    }

    private fun addPaceToCollection(
        localTimePerKilometer: Long,
        localDistancePerKilometer: Float
    ) {
        val previousPacePerKilometer =
            (localTimePerKilometer - mTimePerDistanceType) / (localDistancePerKilometer - mDistancePerUnit)
        mPacePerKilometerCollection.add(previousPacePerKilometer)
    }

    private fun resetTimePerKilometer(overrunDistanceCoefficient: Float) {
        mTimePerDistanceType =
            (mTimePerDistanceType.toFloat() * overrunDistanceCoefficient).toLong()
    }

    private fun updateTimePerKilometer() {
        mTimePerDistanceType += TimeUnit.MILLISECONDS.toSeconds(DateTime().millis - mPreviousTimePerDistanceType)
        mPreviousTimePerDistanceType = DateTime().millis
    }
}