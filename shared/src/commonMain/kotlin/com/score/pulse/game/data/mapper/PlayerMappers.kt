package com.score.pulse.game.data.mapper

import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.model.PlayerEmblem

fun PlayerEntity.toDomain(): Player {
    return Player(
        id = id,
        name = name,
        emblem = runCatching { PlayerEmblem.valueOf(emblem) }.getOrDefault(PlayerEmblem.Gamepad),
        accent = runCatching { AccentColor.valueOf(accent) }.getOrDefault(AccentColor.Emerald),
        score = score,
        wins = wins,
        losses = losses,
        rank = rank,
    )
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        emblem = emblem.name,
        accent = accent.name,
        score = score,
        wins = wins,
        losses = losses,
        rank = rank,
    )
}


fun List<Player>.toSnapshotString(): String {
    return joinToString(";;") { player ->
        listOf(
            player.id,
            player.name,
            player.emblem.name,
            player.accent.name,
            player.score,
            player.wins,
            player.losses,
            player.rank
        ).joinToString("||")
    }
}

fun String.toPlayerList(): List<Player> {
    if (isBlank()) return emptyList()
    return split(";;").mapNotNull { entry ->
        val parts = entry.split("||")
        if (parts.size >= 8) {
            Player(
                id = parts[0].toIntOrNull() ?: 0,
                name = parts[1],
                emblem = runCatching { PlayerEmblem.valueOf(parts[2]) }.getOrDefault(PlayerEmblem.Gamepad),
                accent = runCatching { AccentColor.valueOf(parts[3]) }.getOrDefault(AccentColor.Emerald),
                score = parts[4].toIntOrNull() ?: 0,
                wins = parts[5].toIntOrNull() ?: 0,
                losses = parts[6].toIntOrNull() ?: 0,
                rank = parts[7].toIntOrNull() ?: 0
            )
        } else null
    }
}

fun List<PlayerEntity>.toDomain(): List<Player> = map { it.toDomain() }

fun List<Player>.toEntity(): List<PlayerEntity> = map { it.toEntity() }
