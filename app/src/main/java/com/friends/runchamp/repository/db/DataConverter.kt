package com.friends.runchamp.repository.db

import androidx.room.TypeConverter
import com.friends.runchamp.entity.GpsData
import com.friends.runchamp.utils.DistanceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class DataConverter {
    @TypeConverter
    fun fromRunningData(countryLang: ArrayList<GpsData>): String {
        val type: Type =
            object : TypeToken<ArrayList<GpsData>>() {}.type
        return Gson().toJson(countryLang, type)
    }

    @TypeConverter
    fun toRunningData(countryLangString: String): ArrayList<GpsData> {
        val type: Type =
            object : TypeToken<ArrayList<GpsData>>() {}.type
        return Gson().fromJson(countryLangString, type)
    }

    @TypeConverter
    fun fromDistanceType(distanceType: DistanceType): String {
        val type: Type = object : TypeToken<DistanceType>() {}.type
        return Gson().toJson(distanceType, type)
    }

    @TypeConverter
    fun toDistanceType(value: Int): DistanceType = enumValues<DistanceType>()[value]
}