package com.score.pulse.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.score.pulse.game.data.local.dao.GameDao
import com.score.pulse.game.data.local.dao.PlayerDao
import com.score.pulse.game.data.local.dao.PlayerGameDao
import com.score.pulse.game.data.local.entity.GameEntity
import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.data.local.entity.PlayerGameStatsEntity

@Database(
    entities =
        [
            PlayerEntity::class,
            GameEntity::class,
            PlayerGameStatsEntity::class
        ],
    version = 1,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun playerGameDao(): PlayerGameDao


    companion object {
        const val DB_NAME = "app_database.db"
    }
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
