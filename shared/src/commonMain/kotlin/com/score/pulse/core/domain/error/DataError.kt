package com.score.pulse.core.domain.error

sealed interface DataError : AppError {

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN,
        NOT_FOUND,
    }

    enum class Network : DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }
}
