package com.score.pulse.game.data.mapper

import com.score.pulse.game.data.local.dao.PlayerWithStatsEntity
import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.data.local.entity.PlayerGameStatsEntity
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.model.PlayerEmblem
import com.score.pulse.game.domain.model.PlayerWithStats

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

fun PlayerWithStats.toEntity(gameId: Int): PlayerWithStatsEntity {
    return PlayerWithStatsEntity(
        player = player.toEntity(),
        stats = PlayerGameStatsEntity(
            playerId = player.id,
            gameId = gameId,
            wins = wins,
            losses = losses,
            score = score,
            rank = rank
        )
    )
}

fun PlayerWithStatsEntity.toDomain(): PlayerWithStats {
    return PlayerWithStats(
        player = player.toDomain(),
        wins = stats.wins,
        losses = stats.losses,
        score = stats.score,
        rank = stats.rank
    )
}

fun List<PlayerEntity>.toDomain(): List<Player>  {
    return map { it.toDomain() }
}

fun List<Player>.toEntity(): List<PlayerEntity> {
    return map { it.toEntity() }
}
