package com.friends.runchamp.utils

class UnitConverter {
    companion object {
        private const val KILOMETERS_IN_MILE = 1.60934
        private const val MILES_IN_KILOMETER = 0.621371

        fun convertKilometersToMiles(kilometers: Float) = kilometers / KILOMETERS_IN_MILE

        fun convertMilesToKilometers(miles: Float) = miles / MILES_IN_KILOMETER
    }
}