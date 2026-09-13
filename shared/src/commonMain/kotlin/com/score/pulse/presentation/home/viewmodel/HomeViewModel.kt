package com.score.pulse.presentation.home.viewmodel

import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.domain.model.AccentColor
import com.score.pulse.domain.model.Game
import com.score.pulse.domain.model.Player
import com.score.pulse.domain.model.PlayerEmblem
import com.score.pulse.presentation.home.contract.HomeEffect
import com.score.pulse.presentation.home.contract.HomeEvent
import com.score.pulse.presentation.home.contract.HomeState

class HomeViewModel : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {

    // TODO: Remove this when implemented repos
    init {
        setState {
            copy(
                game = Game(
                    name = "UNO Dark",
                    maxRounds = 4
                ),
                players = listOf(
                    Player(
                        "p1",
                        "Alex",
                        PlayerEmblem.Gamepad,
                        AccentColor.Emerald,
                        score = 142
                    ),
                    Player(
                        "p2",
                        "Sarah",
                        PlayerEmblem.Thunder,
                        AccentColor.Cyan,
                        score = 118
                    ),
                    Player(
                        "p3",
                        "Marcus",
                        PlayerEmblem.Phoenix,
                        AccentColor.Magenta,
                        score = 95
                    ),
                    Player(
                        "p4",
                        "Elena",
                        PlayerEmblem.Shield,
                        AccentColor.Violet,
                        score = 86
                    ),
                ),
                currentRound = 1
            )
        }
    }

    override fun createInitialState(): HomeState = HomeState()

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.AdjustScoreClicked -> {
                setState {
                    copy(
                        players = adjustPlayerPt(
                            players = players,
                            editingPlayerId = event.playerId,
                            delta = event.delta
                        )
                    )
                }
            }

            is HomeEvent.EditScoreClicked -> {
                setState {
                    copy(editingPlayerId = event.playerId)
                }
            }

            HomeEvent.DismissEdit -> {
                setState {
                    copy(editingPlayerId = null)
                }
            }

            HomeEvent.FinishMatchClicked -> {
                setState {
                    copy(isGameResultVisible = true)
                }
            }

            HomeEvent.LockRoundClicked -> {
                setState {
                    copy(isLockRoundConfirmationVisible = true)
                }
            }

            HomeEvent.ConfirmLockRoundClicked -> {
                setState {
                    copy(isLockRoundConfirmationVisible = false)
                }
            }

            HomeEvent.DismissLockRound -> {
                setState {
                    copy(isLockRoundConfirmationVisible = false)
                }
            }

            HomeEvent.NewGameClicked -> {
                setState {
                    copy(
                        currentRound = 1,
                        players = players.map { it.copy(score = 0) },
                        isGameResultVisible = false
                    )
                }
            }

            is HomeEvent.GameNameValueChange -> {
                setState {
                    copy(
                        game = game.copy(
                            name = event.gameName
                        )
                    )
                }
            }

            is HomeEvent.TotalRoundsValueChange -> {
                setState {
                    copy(
                        game = game.copy(
                            maxRounds = event.totalRounds.toInt()
                        )
                    )
                }
            }
        }
    }

    fun adjustPlayerPt(
        players: List<Player>,
        editingPlayerId: String,
        delta: Int
    ): List<Player> {
        return players.map {
            if (it.id == editingPlayerId)
                it.copy(score = (it.score + delta).coerceAtLeast(0))
            else it
        }
    }
}