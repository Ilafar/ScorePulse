package com.score.pulse.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.score.pulse.core.data.local.AppDatabase
import com.score.pulse.core.data.local.DatabaseFactory
import com.score.pulse.game.presentation.match.viewmodel.MatchViewModel
import com.score.pulse.game.data.repository.PlayerRepositoryImpl
import com.score.pulse.game.domain.repository.PlayerRepository
import com.score.pulse.game.domain.usecase.AddPlayerUseCase
import com.score.pulse.game.domain.usecase.ObservePlayersUseCase
import com.score.pulse.game.presentation.addplayer.viewmodel.AddPlayerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::MatchViewModel)
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
