@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.score.pulse.core.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "ScorePulse")
            os.contains("mac") -> File(userHome, "Library/Application Support/ScorePulse")
            else -> File(userHome, ".local/share/ScorePulse")
        }

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, AppDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }
}
