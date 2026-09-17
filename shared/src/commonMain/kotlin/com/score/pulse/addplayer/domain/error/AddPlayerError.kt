package com.score.pulse.addplayer.domain.error

import com.score.pulse.core.domain.error.AppError
import com.score.pulse.core.domain.error.DataError

sealed interface AddPlayerError : AppError {
    data object BlankName : AddPlayerError
    data object NameTooLong : AddPlayerError
    data class Data(val error: DataError) : AddPlayerError
}
