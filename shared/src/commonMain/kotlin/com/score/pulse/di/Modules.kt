package com.score.pulse.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.score.pulse.addplayer.domain.usecase.AddPlayerUseCase
import com.score.pulse.addplayer.domain.usecase.ObservePlayersUseCase
import com.score.pulse.addplayer.presentation.viewmodel.AddPlayerViewModel
import com.score.pulse.core.data.local.AppDatabase
import com.score.pulse.core.data.local.DatabaseFactory
import com.score.pulse.core.data.repository.PlayerRepositoryImpl
import com.score.pulse.core.domain.repository.PlayerRepository
import com.score.pulse.home.presentation.viewmodel.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddPlayerViewModel)

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    factoryOf(::AddPlayerUseCase)
    factoryOf(::ObservePlayersUseCase)

    single { get<AppDatabase>().playerDao() }

    singleOf(::PlayerRepositoryImpl).bind<PlayerRepository>()
}
