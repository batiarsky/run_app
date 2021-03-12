package com.friends.runchamp.repository.db

import android.content.Context
import androidx.room.Room

class ChampDbRepositoryImpl(context: Context) : ChampDbRepository {

    companion object {
        private const val CHAMP_DATABASE = "CHAMP_DATABASE"
    }

    private var champDatabase: ChampDatabase

    init {
        champDatabase =
            Room.databaseBuilder(context, ChampDatabase::class.java, CHAMP_DATABASE).build()
    }

    override fun getRoutesDao(): RoutesDao = champDatabase.getRoutesDao()
}