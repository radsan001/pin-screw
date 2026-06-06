package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GameProgressEntity
import com.example.data.model.LevelProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // Level progress queries
    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllLevelProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    fun getLevelProgress(levelId: Int): Flow<LevelProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(progress: LevelProgressEntity)

    // Game progress queries
    @Query("SELECT * FROM game_progress WHERE id = 0 LIMIT 1")
    fun getGameProgress(): Flow<GameProgressEntity?>

    @Query("SELECT * FROM game_progress WHERE id = 0 LIMIT 1")
    suspend fun getGameProgressDirect(): GameProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameProgress(progress: GameProgressEntity)
}
