package com.friends.runchamp.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.friends.runchamp.entity.Route


@Database(entities = arrayOf(Route::class), version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class ChampDatabase : RoomDatabase() {
    abstract fun getRoutesDao(): RoutesDao
}