package com.score.pulse.core.presentation.util

import com.score.pulse.core.domain.error.DataError

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Local.DISK_FULL -> UiText.DynamicString("Data is full")
        DataError.Local.UNKNOWN -> UiText.DynamicString("An unexpected error occurred")
        DataError.Network.NO_INTERNET -> UiText.DynamicString("No internet connection")
        DataError.Network.REQUEST_TIMEOUT -> UiText.DynamicString("Request timed out")
        DataError.Network.UNAUTHORIZED -> UiText.DynamicString("Unauthorized session")
        DataError.Network.FORBIDDEN -> UiText.DynamicString("Access forbidden")
        DataError.Network.NOT_FOUND -> UiText.DynamicString("Resource not found")
        DataError.Network.TOO_MANY_REQUESTS -> UiText.DynamicString("Too many requests")
        DataError.Network.SERVER_ERROR -> UiText.DynamicString("Server error")
        DataError.Network.SERIALIZATION -> UiText.DynamicString("Data parsing error")
        DataError.Network.UNKNOWN -> UiText.DynamicString("Network error occurred")
    }
}