package com.score.pulse.core.data.local

import androidx.sqlite.SQLiteException
import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import kotlinx.coroutines.CancellationException

suspend inline fun <T> safeDbCall(
    block: suspend () -> T
): AppResult<T, DataError.Local> {
    return try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AppResult.Error(e.toLocalDataError())
    }
}

fun Exception.toLocalDataError(): DataError.Local {
    return when (this) {
        is IllegalStateException -> DataError.Local.NOT_FOUND
        is SQLiteException -> {
            if (message?.contains("SQLITE_FULL", ignoreCase = true) == true ||
                message?.contains("database or disk is full", ignoreCase = true) == true
            ) {
                DataError.Local.DISK_FULL
            } else {
                DataError.Local.UNKNOWN
            }
        }
        else -> DataError.Local.UNKNOWN
    }
}
