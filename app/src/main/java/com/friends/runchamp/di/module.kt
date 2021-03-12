package com.friends.runchamp.di

import com.friends.runchamp.repository.calculation.CalculationManager
import com.friends.runchamp.repository.calculation.CalculationManagerImpl
import com.friends.runchamp.repository.db.ChampDbRepository
import com.friends.runchamp.repository.db.ChampDbRepositoryImpl
import com.friends.runchamp.repository.map.MapManager
import com.friends.runchamp.repository.map.MapManagerImpl
import com.friends.runchamp.view.settings.SettingsViewModel
import com.friends.runchamp.view.start.StartViewModel
import com.friends.runchamp.view.statistics.StatisticsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module(override = true) {
    single { MapManagerImpl(androidContext(), get()) as MapManager }
    single { ChampDbRepositoryImpl(androidContext()) as ChampDbRepository }
    single { CalculationManagerImpl() as CalculationManager }
}

val viewModelModule = module {
    viewModel {
        StartViewModel(get(), get(), get())
    }
    viewModel {
        SettingsViewModel()
    }
    viewModel {
        StatisticsViewModel(get())
    }
}