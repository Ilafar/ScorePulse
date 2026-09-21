package com.score.pulse.core.domain.usecase

import com.score.pulse.core.domain.error.AppError
import com.score.pulse.core.domain.error.AppResult

abstract class BaseUseCase<in Params, out OutData, out OutError : AppError> {
    abstract suspend operator fun invoke(params: Params): AppResult<OutData, OutError>
}

object NoParams
