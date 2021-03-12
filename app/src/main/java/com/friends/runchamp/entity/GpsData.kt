package com.friends.runchamp.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class GpsData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "running_type") val runningType: String,
    @ColumnInfo(name = "coordinates") val coordinates: ArrayList<Coordinate>
)