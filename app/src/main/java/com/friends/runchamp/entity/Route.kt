package com.friends.runchamp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.friends.runchamp.repository.db.DataConverter

@Entity(tableName = "route")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @TypeConverters(DataConverter::class) @ColumnInfo(name = "running_data_list") val gpsDataList: ArrayList<GpsData>
)