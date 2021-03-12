package com.friends.runchamp.repository.calculation

import com.friends.runchamp.utils.Gender

class RunningCalculation {

    companion object {
        private const val MET_6 = 6.0f
        private const val MET_7_P_1 = 7.1f
        private const val MET_8_P_3 = 8.3f
        private const val MET_9 = 9.0f
        private const val MET_9_P_8 = 9.8f
        private const val MET_10_P_1 = 10.1f
        private const val MET_10_P_5 = 10.5f
        private const val MET_10_P_7 = 10.7f
        private const val MET_11 = 11f
        private const val MET_11_P_4 = 11.4f
        private const val MET_11_P_8 = 11.8f
        private const val MET_12 = 12.0f
        private const val MET_12_P_3 = 12.3f
        private const val MET_12_P_55 = 12.55f
        private const val MET_12_P_8 = 12.8f
        private const val MET_13_P_65 = 13.65f
        private const val MET_14_P_5 = 14.5f
        private const val MET_15_P_25 = 15.25f
        private const val MET_16 = 16.0f
        private const val MET_17_P_5 = 17.5f
        private const val MET_19 = 19f
        private const val MET_19_P_4 = 19.4f
        private const val MET_19_P_8 = 19.8f
        private const val MET_21_P_4 = 21.4f
        private const val MET_23_P = 23.0f
        private const val MET_23_P_5 = 23.5f
    }

    /**
     * Gets the MET value for an activity. Based on https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories/running .
     *
     * @param speedInMph The speed in miles per hour
     * @return The met value.
     */
    fun getMetFromMiles(speedInMph: Double): Double {
        val met = when {
            speedInMph <= 4 -> MET_6
            speedInMph > 4.0 && speedInMph < 5.0 -> MET_7_P_1
            speedInMph == 5.0 -> MET_8_P_3
            speedInMph > 5.0 && speedInMph < 6.0 -> MET_9
            speedInMph == 6.0 -> MET_9_P_8
            speedInMph > 6.0 && speedInMph < 6.7 -> MET_10_P_1
            speedInMph == 6.7 -> MET_10_P_5
            speedInMph > 6.7 && speedInMph < 7.0 -> MET_10_P_7
            speedInMph == 7.0 -> MET_11
            speedInMph > 7.0 && speedInMph < 7.5 -> MET_11_P_4
            speedInMph in 7.5..8.0 -> MET_11_P_8
            speedInMph > 8.0 && speedInMph < 8.6 -> MET_12
            speedInMph == 8.6 -> MET_12_P_3
            speedInMph > 8.6 && speedInMph < 9.0 -> MET_12_P_55
            speedInMph == 9.0 -> MET_12_P_8
            speedInMph > 9.0 && speedInMph < 10.0 -> MET_13_P_65
            speedInMph == 10.0 -> MET_14_P_5
            speedInMph > 10.0 && speedInMph < 11.0 -> MET_15_P_25
            speedInMph == 11.0 -> MET_16
            speedInMph > 11.0 && speedInMph < 12.0 -> MET_17_P_5
            speedInMph == 12.0 -> MET_19
            speedInMph > 12.0 && speedInMph < 13.0 -> MET_19_P_4
            speedInMph == 13.0 -> MET_19_P_8
            speedInMph > 13.0 && speedInMph < 14.0 -> MET_21_P_4
            speedInMph == 14.0 -> MET_23_P
            else -> MET_23_P_5
        }

        return met.toDouble()
    }

    /**
     * Gets the MET value for an activity from . Based on https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories/running .
     *
     * @param speedInKm The speed in kilometers per hour
     * @return The met value.
     */
    fun getMetFromKilometers(speedInKm: Double): Double {
        val met = when {
            speedInKm <= 6.44 -> MET_6
            speedInKm > 6.44 && speedInKm < 8.05 -> MET_7_P_1
            speedInKm == 8.05 -> MET_8_P_3
            speedInKm > 8.05 && speedInKm < 9.66 -> MET_9
            speedInKm == 9.66 -> MET_9_P_8
            speedInKm > 9.66 && speedInKm < 10.78 -> MET_10_P_1
            speedInKm == 10.78 -> MET_10_P_5
            speedInKm > 10.78 && speedInKm < 11.27 -> MET_10_P_7
            speedInKm == 11.27 -> MET_11
            speedInKm > 11.27 && speedInKm < 12.07 -> MET_11_P_4
            speedInKm in 12.07..12.87 -> MET_11_P_8
            speedInKm > 12.87 && speedInKm < 13.84 -> MET_12
            speedInKm == 13.84 -> MET_12_P_3
            speedInKm > 13.84 && speedInKm < 14.48 -> MET_12_P_55
            speedInKm == 14.48 -> MET_12_P_8
            speedInKm > 14.48 && speedInKm < 16.09 -> MET_13_P_65
            speedInKm == 16.09 -> MET_14_P_5
            speedInKm > 16.09 && speedInKm < 17.7 -> MET_15_P_25
            speedInKm == 17.7 -> MET_16
            speedInKm > 17.7 && speedInKm < 19.3 -> MET_17_P_5
            speedInKm == 19.3 -> MET_19
            speedInKm > 19.3 && speedInKm < 20.92 -> MET_19_P_4
            speedInKm == 20.92 -> MET_19_P_8
            speedInKm > 20.92 && speedInKm < 22.53 -> MET_21_P_4
            speedInKm == 22.53 -> MET_23_P
            else -> MET_23_P_5
        }

        return met.toDouble()
    }

    /**
     * Calculates the Harris Benedict RMR value for an entity. Based on above calculation for Com
     *
     * @param gender   Users gender.
     * @param weightKg Weight in Kg.
     * @param age      Age in years.
     * @param heightCm Height in CM.
     * @return Harris benedictRMR value.
     */
    fun getHarrisBenedictRmr(
        gender: Int,
        weightKg: Float,
        age: Float,
        heightCm: Float
    ): Float = if (gender == Gender.MALE.value) {
        66.4730f + (5.0033f * heightCm) + (13.7516f * weightKg) - (6.7550f * age)
    } else {
        655.0955f + (1.8496f * heightCm) + (9.5634f * weightKg) - (4.6756f * age)
    }
}