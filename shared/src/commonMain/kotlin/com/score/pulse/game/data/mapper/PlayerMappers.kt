package com.score.pulse.game.data.mapper

import com.score.pulse.game.data.local.dao.PlayerWithStatsEntity
import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.data.local.entity.PlayerGameStatsEntity
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.model.PlayerEmblem

fun PlayerEntity.toDomain(): Player {
    return Player(
        id = id,
        name = name,
        emblem = runCatching { PlayerEmblem.valueOf(emblem) }.getOrDefault(PlayerEmblem.Gamepad),
        accent = runCatching { AccentColor.valueOf(accent) }.getOrDefault(AccentColor.Emerald),
    )
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        emblem = emblem.name,
        accent = accent.name,
    )
}

fun Player.toStatsEntity(gameId: Int): PlayerGameStatsEntity {
    return PlayerGameStatsEntity(
        playerId = id,
        gameId = gameId,
        wins = wins,
        losses = losses,
        score = score,
        rank = rank
    )
}

fun PlayerWithStatsEntity.toDomain(): Player {
    return Player(
        id = player.id,
        name = player.name,
        emblem = runCatching { PlayerEmblem.valueOf(player.emblem) }.getOrDefault(PlayerEmblem.Gamepad),
        accent = runCatching { AccentColor.valueOf(player.accent) }.getOrDefault(AccentColor.Emerald),
        score = stats.score,
        wins = stats.wins,
        losses = stats.losses,
        rank = stats.rank
    )
}

fun List<PlayerEntity>.toDomain(): List<Player>  {
    return map { it.toDomain() }
}

fun List<Player>.toEntity(): List<PlayerEntity> {
    return map { it.toEntity() }
}
