package com.score.pulse.game.data.mapper

import com.score.pulse.game.data.local.entity.ActiveGameEntity
import com.score.pulse.game.data.local.entity.MatchHistoryEntity
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.model.MatchRecord

fun ActiveGameEntity.toDomain(): Game {
    return Game(
        id = id,
        name = name,
        currentRound = currentRound,
        maxRounds = maxRounds,
        createdAt = createdAt
    )
}

fun Game.toActiveEntity(): ActiveGameEntity {
    return ActiveGameEntity(
        id = 1,
        name = name,
        currentRound = currentRound,
        maxRounds = maxRounds,
        createdAt = createdAt
    )
}

fun MatchHistoryEntity.toDomain(): MatchRecord {
    return MatchRecord(
        id = id.toString(),
        title = title,
        durationMinutes = durationMinutes,
        createdAt = createdAt,
        participants = participantsSnapshot.toPlayerList()
    )
}

fun MatchRecord.toHistoryEntity(): MatchHistoryEntity {
    return MatchHistoryEntity(
        id = id.toIntOrNull() ?: 0,
        title = title,
        durationMinutes = durationMinutes,
        createdAt = createdAt,
        participantsSnapshot = participants.toSnapshotString()
    )
}
