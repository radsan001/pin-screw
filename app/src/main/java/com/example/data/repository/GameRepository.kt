package com.example.data.repository

import com.example.data.dao.GameDao
import com.example.data.model.GameProgressEntity
import com.example.data.model.LevelProgressEntity
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allLevelProgress: Flow<List<LevelProgressEntity>> = gameDao.getAllLevelProgress()
    val gameProgress: Flow<GameProgressEntity?> = gameDao.getGameProgress()

    suspend fun getGameProgressDirect(): GameProgressEntity {
        return gameDao.getGameProgressDirect() ?: GameProgressEntity().also {
            gameDao.insertGameProgress(it)
        }
    }

    suspend fun updateGameProgress(progress: GameProgressEntity) {
        gameDao.insertGameProgress(progress)
    }

    suspend fun updateLevelProgress(levelId: Int, completed: Boolean, stars: Int, highScore: Int) {
        val current = LevelProgressEntity(levelId = levelId, completed = completed, stars = stars, highScore = highScore)
        gameDao.insertLevelProgress(current)
    }

    suspend fun addCoins(amount: Int) {
        val progress = getGameProgressDirect()
        val updated = progress.copy(coins = progress.coins + amount)
        gameDao.insertGameProgress(updated)
    }

    suspend fun spendCoins(amount: Int): Boolean {
        val progress = getGameProgressDirect()
        if (progress.coins >= amount) {
            val updated = progress.copy(coins = progress.coins - amount)
            gameDao.insertGameProgress(updated)
            return true
        }
        return false
    }

    suspend fun buyBooster(boosterType: String, cost: Int): Boolean {
        val progress = getGameProgressDirect()
        if (progress.coins >= cost) {
            val updated = when (boosterType) {
                "hammer" -> progress.copy(
                    coins = progress.coins - cost,
                    boosterHammerCount = progress.boosterHammerCount + 1
                )
                "extra_hole" -> progress.copy(
                    coins = progress.coins - cost,
                    boosterExtraHoleCount = progress.boosterExtraHoleCount + 1
                )
                "undo" -> progress.copy(
                    coins = progress.coins - cost,
                    boosterUndoCount = progress.boosterUndoCount + 1
                )
                else -> progress
            }
            gameDao.insertGameProgress(updated)
            return true
        }
        return false
    }

    suspend fun useBooster(boosterType: String): Boolean {
        val progress = getGameProgressDirect()
        val updated = when (boosterType) {
            "hammer" -> {
                if (progress.boosterHammerCount > 0) progress.copy(boosterHammerCount = progress.boosterHammerCount - 1) else null
            }
            "extra_hole" -> {
                if (progress.boosterExtraHoleCount > 0) progress.copy(boosterExtraHoleCount = progress.boosterExtraHoleCount - 1) else null
            }
            "undo" -> {
                if (progress.boosterUndoCount > 0) progress.copy(boosterUndoCount = progress.boosterUndoCount - 1) else null
            }
            else -> null
        }
        return if (updated != null) {
            gameDao.insertGameProgress(updated)
            true
        } else {
            false
        }
    }

    suspend fun selectTheme(themeId: String) {
        val progress = getGameProgressDirect()
        val updated = progress.copy(activeThemeId = themeId)
        gameDao.insertGameProgress(updated)
    }

    suspend fun unlockTheme(themeId: String, cost: Int): Boolean {
        val progress = getGameProgressDirect()
        val currentUnlocked = progress.unlockedThemeIds.split(",").toSet()
        if (themeId in currentUnlocked) return true

        if (progress.coins >= cost) {
            val newUnlocked = (currentUnlocked + themeId).joinToString(",")
            val updated = progress.copy(
                coins = progress.coins - cost,
                unlockedThemeIds = newUnlocked,
                activeThemeId = themeId
            )
            gameDao.insertGameProgress(updated)
            return true
        }
        return false
    }

    suspend fun toggleSound() {
        val progress = getGameProgressDirect()
        val updated = progress.copy(soundEnabled = !progress.soundEnabled)
        gameDao.insertGameProgress(updated)
    }

    suspend fun toggleVibration() {
        val progress = getGameProgressDirect()
        val updated = progress.copy(vibrationEnabled = !progress.vibrationEnabled)
        gameDao.insertGameProgress(updated)
    }

    suspend fun selectMode(mode: String) {
        val progress = getGameProgressDirect()
        val updated = progress.copy(selectedMode = mode)
        gameDao.insertGameProgress(updated)
    }
}
