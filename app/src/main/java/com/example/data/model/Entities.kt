package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val completed: Boolean = false,
    val stars: Int = 0,
    val highScore: Int = 0
)

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val id: Int = 0, // Single-row configuration
    val coins: Int = 200, // Starts with 200 coins
    val boosterHammerCount: Int = 3,
    val boosterExtraHoleCount: Int = 3,
    val boosterUndoCount: Int = 3,
    val activeThemeId: String = "wood", // Default theme is "wood"
    val unlockedThemeIds: String = "wood", // Comma-separated like "wood,neon,candy"
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val selectedMode: String = "SORT" // Default game mode selection: SORT or SOLITAIRE
)
