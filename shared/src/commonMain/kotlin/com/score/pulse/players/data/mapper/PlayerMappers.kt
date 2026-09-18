package com.score.pulse.players.data.mapper

import com.score.pulse.players.data.local.PlayerEntity
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.model.PlayerEmblem

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

fun List<PlayerEntity>.toDomain(): List<Player> {
    return map { it.toDomain() }
}

fun List<Player>.toEntity(): List<PlayerEntity> {
    return map { it.toEntity() }
}
