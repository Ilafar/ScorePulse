package com.score.pulse.game.data.mapper

import com.score.pulse.game.data.local.entity.GameEntity
import com.score.pulse.game.domain.model.Game

fun Game.toEntity(): GameEntity {
    return GameEntity(
        id = id,
        name = name,
        maxRounds = maxRounds,
        status = status,
        createdAt = createdAt
    )
}

fun GameEntity.toDomain(): Game {
    return Game(
        id = id,
        name = name,
        maxRounds = maxRounds,
        status = status,
        createdAt = createdAt
    )
}

fun List<GameEntity>.toDomain(): List<Game> {
    return map { it.toDomain() }
}

fun List<Game>.toEntity(): List<GameEntity> {
    return map { it.toEntity() }
}