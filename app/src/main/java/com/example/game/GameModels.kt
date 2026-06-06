package com.example.game

import androidx.compose.ui.graphics.Color

data class GameHole(
    val id: String,
    val x: Float, // Relative 0-100 on canvas
    val y: Float  // Relative 0-100 on canvas
)

data class GameScrew(
    val id: String,
    val holeId: String, // Which hole it is in, or empty if in tray/removed
    val colorId: String // e.g. "red", "green", "blue", "yellow", "orange", "silver"
)

data class GamePlate(
    val id: String,
    val attachedHoleIds: List<String>, // Hole IDs that this plate is anchored to
    val colorId: String, // Type of color/texture
    val widthDp: Float = 16f, // Thickness density of the plate
    val isCircular: Boolean = false, // If true, drawn as a circle centered on the first hole
    val circleRadius: Float = 25f
)

data class CargoBox(
    val id: String,
    val colorId: String,
    val capacity: Int,
    val loadedScrewsCount: Int = 0
)

data class LevelData(
    val levelId: Int,
    val name: String,
    val holes: List<GameHole>,
    val screws: List<GameScrew>,
    val plates: List<GamePlate>,
    val targetBoxes: List<CargoBox> = emptyList(), // Only used in SORT mode
    val timeLimitSeconds: Int = 120,
    val defaultDifficulty: String = "Medium"
)

data class GameTheme(
    val id: String,
    val name: String,
    val cost: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val boardBgGradient: List<Color>,
    val plateColors: Map<String, Color>,
    val screwColors: Map<String, Color>,
    val metalTexture: Boolean = false,
    val glowEffect: Boolean = false
)

// Define our preconfigured game theme data
object GameThemePresets {
    val WoodTheme = GameTheme(
        id = "wood",
        name = "Classic Wood",
        cost = 0,
        primaryColor = Color(0xFF8B5A2B),
        secondaryColor = Color(0xFFCD853F),
        boardBgGradient = listOf(Color(0xFF3E2723), Color(0xFF1B0000)),
        plateColors = mapOf(
            "red" to Color(0xFFD32F2F),
            "green" to Color(0xFF388E3C),
            "blue" to Color(0xFF1976D2),
            "yellow" to Color(0xFFFBC02D),
            "orange" to Color(0xFFF57C00),
            "silver" to Color(0xFF9E9E9E),
            "plate_default" to Color(0xFFCD853F),
            "plate_secondary" to Color(0xFFD2B48C)
        ),
        screwColors = mapOf(
            "red" to Color(0xFFFF5252),
            "green" to Color(0xFF69F0AE),
            "blue" to Color(0xFF40C4FF),
            "yellow" to Color(0xFFFFD740),
            "orange" to Color(0xFFFFAB40),
            "silver" to Color(0xFFE0E0E0)
        )
    )

    val NeonTheme = GameTheme(
        id = "neon",
        name = "Cyber Glow",
        cost = 150,
        primaryColor = Color(0xFF00FFCC),
        secondaryColor = Color(0xFFFF007F),
        boardBgGradient = listOf(Color(0xFF0F0C1B), Color(0xFF020208)),
        plateColors = mapOf(
            "red" to Color(0xFFFF0055),
            "green" to Color(0xFF00FF66),
            "blue" to Color(0xFF0066FF),
            "yellow" to Color(0xFFFFFF00),
            "orange" to Color(0xFFFF6600),
            "silver" to Color(0xFFCCCCFF),
            "plate_default" to Color(0xFF00FFD8),
            "plate_secondary" to Color(0xFFFF00E4)
        ),
        screwColors = mapOf(
            "red" to Color(0xFFFF1A75),
            "green" to Color(0xFF33FF99),
            "blue" to Color(0xFF3399FF),
            "yellow" to Color(0xFFFFFF66),
            "orange" to Color(0xFFFF9933),
            "silver" to Color(0xFFE6F0FF)
        ),
        glowEffect = true
    )

    val CandyTheme = GameTheme(
        id = "candy",
        name = "Sugar Pop",
        cost = 300,
        primaryColor = Color(0xFFFF69B4),
        secondaryColor = Color(0xFF87CEFA),
        boardBgGradient = listOf(Color(0xFFFFF0F5), Color(0xFFE6E6FA)),
        plateColors = mapOf(
            "red" to Color(0xFFFF6B6B),
            "green" to Color(0xFF51CF66),
            "blue" to Color(0xFF339AF0),
            "yellow" to Color(0xFFFCC419),
            "orange" to Color(0xFFFF922B),
            "silver" to Color(0xFFADB5BD),
            "plate_default" to Color(0xFFFFB6C1),
            "plate_secondary" to Color(0xFFB0E0E6)
        ),
        screwColors = mapOf(
            "red" to Color(0xFFFF8787),
            "green" to Color(0xFF69DB7C),
            "blue" to Color(0xFF4DABF7),
            "yellow" to Color(0xFFFFDE59),
            "orange" to Color(0xFFFFC078),
            "silver" to Color(0xFFCED4DA)
        )
    )

    val IndustrialTheme = GameTheme(
        id = "metal",
        name = "Heavy Metal",
        cost = 500,
        primaryColor = Color(0xFF607D8B),
        secondaryColor = Color(0xFFCFD8DC),
        boardBgGradient = listOf(Color(0xFF263238), Color(0xFF10171a)),
        plateColors = mapOf(
            "red" to Color(0xFFA52A2A),
            "green" to Color(0xFF2E8B57),
            "blue" to Color(0xFF4682B4),
            "yellow" to Color(0xFFB8860B),
            "orange" to Color(0xFFD2691E),
            "silver" to Color(0xFF708090),
            "plate_default" to Color(0xFFB0C4DE),
            "plate_secondary" to Color(0xFF778899)
        ),
        screwColors = mapOf(
            "red" to Color(0xFFFF6347),
            "green" to Color(0xFF3CB371),
            "blue" to Color(0xFF6495ED),
            "yellow" to Color(0xFFDAA520),
            "orange" to Color(0xFFED4C29),
            "silver" to Color(0xFFCFD8DC)
        ),
        metalTexture = true
    )

    val list = listOf(WoodTheme, NeonTheme, CandyTheme, IndustrialTheme)
    
    fun getById(id: String): GameTheme {
        return list.find { it.id == id } ?: WoodTheme
    }
}
