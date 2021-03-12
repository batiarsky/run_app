package com.friends.runchamp.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Coordinate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double
)