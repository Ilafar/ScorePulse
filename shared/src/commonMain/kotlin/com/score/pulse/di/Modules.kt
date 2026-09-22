package com.score.pulse.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.score.pulse.core.data.local.AppDatabase
import com.score.pulse.core.data.local.DatabaseFactory
import com.score.pulse.game.data.repository.GameRepositoryImpl
import com.score.pulse.game.data.repository.HistoryRepositoryImpl
import com.score.pulse.game.data.repository.PlayerGameRepositoryImpl
import com.score.pulse.game.data.repository.PlayerRepositoryImpl
import com.score.pulse.game.domain.repository.GameRepository
import com.score.pulse.game.domain.repository.HistoryRepository
import com.score.pulse.game.domain.repository.PlayerGameRepository
import com.score.pulse.game.domain.repository.PlayerRepository
import com.score.pulse.game.domain.usecase.add.AddNewGameUseCase
import com.score.pulse.game.domain.usecase.add.AddPlayerUseCase
import com.score.pulse.game.domain.usecase.add.AdjustScoreUseCase
import com.score.pulse.game.domain.usecase.add.LockRoundUseCase
import com.score.pulse.game.domain.usecase.clear.ClearPlayersUseCase
import com.score.pulse.game.domain.usecase.complete.CompleteActiveGameUseCase
import com.score.pulse.game.domain.usecase.delete.DeletePlayerUseCase
import com.score.pulse.game.domain.usecase.observe.ObserveActiveGameUseCase
import com.score.pulse.game.domain.usecase.observe.ObserveLeaderboardUseCase
import com.score.pulse.game.domain.usecase.observe.ObserveMatchHistoryUseCase
import com.score.pulse.game.domain.usecase.observe.ObservePlayersUseCase
import com.score.pulse.game.domain.usecase.observe.ObservePlayersWithStatsUseCase
import com.score.pulse.game.presentation.addplayer.viewmodel.AddPlayerViewModel
import com.score.pulse.game.presentation.history.viewmodel.HistoryViewModel
import com.score.pulse.game.presentation.leaderboard.viewmodel.LeaderboardViewModel
import com.score.pulse.game.presentation.match.viewmodel.MatchViewModel
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
    viewModelOf(::HistoryViewModel)
    viewModelOf(::LeaderboardViewModel)

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    factoryOf(::AddPlayerUseCase)
    factoryOf(::AddNewGameUseCase)
    factoryOf(::AdjustScoreUseCase)
    factoryOf(::ObservePlayersWithStatsUseCase)
    factoryOf(::ObservePlayersUseCase)
    factoryOf(::ObserveActiveGameUseCase)
    factoryOf(::DeletePlayerUseCase)
    factoryOf(::ClearPlayersUseCase)
    factoryOf(::LockRoundUseCase)
    factoryOf(::CompleteActiveGameUseCase)
    factoryOf(::ObserveMatchHistoryUseCase)
    factoryOf(::ObserveLeaderboardUseCase)

    single { get<AppDatabase>().playerDao() }
    single { get<AppDatabase>().gameDao() }
    single { get<AppDatabase>().historyDao() }

    singleOf(::PlayerRepositoryImpl).bind<PlayerRepository>()
    singleOf(::GameRepositoryImpl).bind<GameRepository>()
    singleOf(::PlayerGameRepositoryImpl).bind<PlayerGameRepository>()
    singleOf(::HistoryRepositoryImpl).bind<HistoryRepository>()
}
