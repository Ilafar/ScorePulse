@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.score.pulse.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbPath = appContext.getDatabasePath(AppDatabase.DB_NAME)

        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            dbPath.absolutePath
        )
    }
}
