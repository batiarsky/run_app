package com.friends.runchamp.repository.db

import androidx.room.*
import com.friends.runchamp.entity.Route
import io.reactivex.Single

@Dao
interface RoutesDao {
    @Query("SELECT * FROM route")
    fun getAllRoutes(): Single<List<Route>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoute(route: Route): Single<Long>

    @Delete
    fun delete(route: Route): Single<Int>
}