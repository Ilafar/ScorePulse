package com.score.pulse.game.domain.usecase.add

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.error.AddPlayerError
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.repository.PlayerRepository

class AddPlayerUseCase(
    private val playerRepository: PlayerRepository
) : BaseUseCase<Player, Unit, AddPlayerError>() {

    override suspend operator fun invoke(params: Player): AppResult<Unit, AddPlayerError> {
        val normalizedName = params.name.trim()
            .replaceFirstChar(Char::titlecase)

        if (normalizedName.isBlank()) {
            return AppResult.Error(AddPlayerError.BlankName)
        }

        if (normalizedName.length > 20) {
            return AppResult.Error(AddPlayerError.NameTooLong)
        }

        val normalizedPlayer = params.copy(name = normalizedName)
        return when (val result = playerRepository.addPlayer(normalizedPlayer)) {
            is AppResult.Error -> AppResult.Error(AddPlayerError.Data(result.error))
            is AppResult.Success -> AppResult.Success(Unit)
        }
    }
}