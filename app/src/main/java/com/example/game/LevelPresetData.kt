package com.example.game

object LevelPresetData {

    // Helper to generate a unique ID
    private var idCounter = 0
    private fun nextId(prefix: String): String = "${prefix}_${++idCounter}"

    fun getLevel(levelId: Int): LevelData {
        idCounter = 0 // Reset for consistent generation per level ID
        return when (levelId) {
            1 -> createLevel1()
            2 -> createLevel2()
            3 -> createLevel3()
            4 -> createLevel4()
            5 -> createLevel5()
            6 -> createLevel6()
            7 -> createLevel7()
            8 -> createLevel8()
            9 -> createLevel9()
            10 -> createLevel10()
            11 -> createLevel11()
            12 -> createLevel12()
            13 -> createLevel13()
            14 -> createLevel14()
            15 -> createLevel15()
            else -> generateProceduralLevel(levelId)
        }
    }

    // LEVEL 1: Simple introductory lesson (Warmup)
    private fun createLevel1(): LevelData {
        val holes = listOf(
            GameHole("h1", 30f, 40f),
            GameHole("h2", 70f, 40f),
            GameHole("h3", 50f, 75f) // Empty hole
        )
        val screws = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "red")
        )
        val plates = listOf(
            GamePlate("p1", listOf("h1", "h2"), "red", widthDp = 20f)
        )
        val boxes = listOf(
            CargoBox("box1", "red", 2)
        )
        return LevelData(1, "Warmup", holes, screws, plates, boxes, 90, "Easy")
    }

    // LEVEL 2: Double Trouble (Two bars, different colors)
    private fun createLevel2(): LevelData {
        val holes = listOf(
            GameHole("h1", 30f, 30f),
            GameHole("h2", 70f, 30f),
            GameHole("h3", 30f, 60f),
            GameHole("h4", 70f, 60f),
            GameHole("h5", 50f, 80f) // Empty hole
        )
        val screws = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "red"),
            GameScrew("s3", "h3", "blue"),
            GameScrew("s4", "h4", "blue")
        )
        val plates = listOf(
            GamePlate("p1", listOf("h1", "h2"), "red", widthDp = 18f),
            GamePlate("p2", listOf("h3", "h4"), "blue", widthDp = 18f)
        )
        val boxes = listOf(
            CargoBox("box1", "red", 2),
            CargoBox("box2", "blue", 2)
        )
        return LevelData(2, "Double Trouble", holes, screws, plates, boxes, 120, "Easy")
    }

    // LEVEL 3: T-Bone Overlap (Plates overlapping at a joint)
    private fun createLevel3(): LevelData {
        val holes = listOf(
            GameHole("h1", 50f, 25f),
            GameHole("h2", 50f, 50f), // Shared overlap anchor!
            GameHole("h3", 20f, 50f),
            GameHole("h4", 80f, 50f),
            GameHole("h5", 35f, 75f), // Empty
            GameHole("h6", 65f, 75f)  // Empty
        )
        val screws = listOf(
            GameScrew("s1", "h1", "green"),
            GameScrew("s2", "h2", "green"), // Green screw pinning BOTH
            GameScrew("s3", "h3", "orange"),
            GameScrew("s4", "h4", "orange")
        )
        val plates = listOf(
            // Overlapping plates sharing h2
            GamePlate("p1", listOf("h1", "h2"), "green", widthDp = 22f),
            GamePlate("p2", listOf("h3", "h2", "h4"), "orange", widthDp = 16f)
        )
        val boxes = listOf(
            CargoBox("box1", "green", 2),
            CargoBox("box2", "orange", 2)
        )
        return LevelData(3, "T-Bone Overlap", holes, screws, plates, boxes, 120, "Medium")
    }

    // LEVEL 4: The Ring (Circular disk and rectangular bars)
    private fun createLevel4(): LevelData {
        val holes = listOf(
            GameHole("h1", 50f, 40f), // Center of circular plate
            GameHole("h2", 25f, 40f),
            GameHole("h3", 75f, 40f),
            GameHole("h4", 50f, 15f),
            GameHole("h5", 50f, 65f),
            GameHole("h6", 25f, 75f), // Empty
            GameHole("h7", 75f, 75f)  // Empty
        )
        val screws = listOf(
            GameScrew("s1", "h1", "yellow"),
            GameScrew("s2", "h2", "red"),
            GameScrew("s3", "h3", "red"),
            GameScrew("s4", "h4", "blue"),
            GameScrew("s5", "h5", "blue")
        )
        val plates = listOf(
            GamePlate("p_circ", listOf("h1"), "yellow", isCircular = true, circleRadius = 40f),
            GamePlate("p_horiz", listOf("h2", "h3"), "red", widthDp = 16f),
            GamePlate("p_vert", listOf("h4", "h5"), "blue", widthDp = 16f)
        )
        val boxes = listOf(
            CargoBox("box1", "red", 2),
            CargoBox("box2", "blue", 2),
            CargoBox("box3", "yellow", 1)
        )
        return LevelData(4, "The Ring", holes, screws, plates, boxes, 150, "Medium")
    }

    // LEVEL 5: Swing Pivot (Showcasing 1-screw rotation physical trick!)
    private fun createLevel5(): LevelData {
        val holes = listOf(
            GameHole("h1", 30f, 25f), // Top left pivot
            GameHole("h2", 70f, 25f), // Top right pivot
            GameHole("h3", 30f, 65f), // Bottom left
            GameHole("h4", 70f, 65f), // Bottom right
            GameHole("h5", 50f, 15f), // Empty target
            GameHole("h6", 50f, 80f)  // Empty target
        )
        val screws = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "blue"),
            GameScrew("s3", "h3", "red"),
            GameScrew("s4", "h4", "blue")
        )
        val plates = listOf(
            GamePlate("p1", listOf("h1", "h3"), "red", widthDp = 20f),
            GamePlate("p2", listOf("h2", "h4"), "blue", widthDp = 20f)
        )
        val boxes = listOf(
            CargoBox("box1", "red", 2),
            CargoBox("box2", "blue", 2)
        )
        return LevelData(5, "Pendulum Swing", holes, screws, plates, boxes, 155, "Medium")
    }

    // LEVEL 6: Triangle Trap (Locked structure of three interlocking bars)
    private fun createLevel6(): LevelData {
        val holes = listOf(
            GameHole("h1", 50f, 25f), // Peak
            GameHole("h2", 25f, 65f), // Bottom-left
            GameHole("h3", 75f, 65f), // Bottom-right
            GameHole("h4", 50f, 75f), // Empty
            GameHole("h5", 50f, 85f)  // Empty
        )
        val screws = listOf(
            GameScrew("s1", "h1", "yellow"),
            GameScrew("s2", "h2", "yellow"),
            GameScrew("s3", "h3", "yellow")
        )
        val plates = listOf(
            GamePlate("p1", listOf("h1", "h2"), "yellow", widthDp = 18f),
            GamePlate("p2", listOf("h2", "h3"), "yellow", widthDp = 18f),
            GamePlate("p3", listOf("h3", "h1"), "yellow", widthDp = 18f)
        )
        val boxes = listOf(
            CargoBox("box1", "yellow", 3)
        )
        return LevelData(6, "The Golden Triangle", holes, screws, plates, boxes, 140, "Hard")
    }

    // LEVEL 7: Cross Over (A beautiful cross grid with central overlap)
    private fun createLevel7(): LevelData {
        val h = listOf(
            GameHole("h1", 50f, 20f),
            GameHole("h2", 50f, 40f),
            GameHole("h3", 50f, 60f),
            GameHole("h4", 50f, 80f),
            GameHole("h5", 20f, 50f),
            GameHole("h6", 40f, 50f),
            GameHole("h7", 60f, 50f),
            GameHole("h8", 80f, 50f),
            GameHole("he1", 15f, 15f), // Empty
            GameHole("he2", 85f, 15f)  // Empty
        )
        val s = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "orange"),
            GameScrew("s3", "h3", "red"),
            GameScrew("s4", "h4", "orange"),
            GameScrew("s5", "h5", "blue"),
            GameScrew("s6", "h6", "green"),
            GameScrew("s7", "h7", "blue"),
            GameScrew("s8", "h8", "green")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1", "h2", "h3", "h4"), "red", widthDp = 14f),
            GamePlate("p2", listOf("h5", "h6", "h7", "h8"), "blue", widthDp = 14f)
        )
        val b = listOf(
            CargoBox("b1", "red", 2),
            CargoBox("b2", "orange", 2),
            CargoBox("b3", "blue", 2),
            CargoBox("b4", "green", 2)
        )
        return LevelData(7, "Cross Examination", h, s, p, b, 180, "Hard")
    }

    // LEVEL 8: Starburst (Rotational star motif)
    private fun createLevel8(): LevelData {
        val h = listOf(
            GameHole("hc", 50f, 50f), // Centre
            GameHole("h1", 30f, 30f),
            GameHole("h2", 70f, 30f),
            GameHole("h3", 30f, 70f),
            GameHole("h4", 70f, 70f),
            GameHole("he1", 50f, 10f), // Empty
            GameHole("he2", 50f, 90f) // Empty
        )
        val s = listOf(
            GameScrew("s1", "hc", "silver"),
            GameScrew("s2", "h1", "blue"),
            GameScrew("s3", "h2", "blue"),
            GameScrew("s4", "h3", "green"),
            GameScrew("s5", "h4", "green")
        )
        val p = listOf(
            GamePlate("p1", listOf("hc", "h1"), "blue", widthDp = 16f),
            GamePlate("p2", listOf("hc", "h2"), "blue", widthDp = 16f),
            GamePlate("p3", listOf("hc", "h3"), "green", widthDp = 16f),
            GamePlate("p4", listOf("hc", "h4"), "green", widthDp = 16f)
        )
        val b = listOf(
            CargoBox("b1", "blue", 2),
            CargoBox("b2", "green", 2),
            CargoBox("b3", "silver", 1)
        )
        return LevelData(8, "Starburst Shell", h, s, p, b, 180, "Hard")
    }

    // LEVEL 9: Ladder Climb (Descending cascade)
    private fun createLevel9(): LevelData {
        val h = listOf(
            GameHole("h1", 30f, 20f), GameHole("h2", 70f, 20f),
            GameHole("h3", 30f, 40f), GameHole("h4", 70f, 40f),
            GameHole("h5", 30f, 60f), GameHole("h6", 70f, 60f),
            GameHole("h7", 50f, 80f) // Empty bottom-central hold
        )
        val s = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "red"),
            GameScrew("s3", "h3", "orange"),
            GameScrew("s4", "h4", "orange"),
            GameScrew("s5", "h5", "yellow"),
            GameScrew("s6", "h6", "yellow")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1", "h2", "h3", "h4"), "red", widthDp = 15f),
            GamePlate("p2", listOf("h3", "h4", "h5", "h6"), "orange", widthDp = 15f)
        )
        val b = listOf(
            CargoBox("b1", "red", 2),
            CargoBox("b2", "orange", 2),
            CargoBox("b3", "yellow", 2)
        )
        return LevelData(9, "The Step Ladder", h, s, p, b, 150, "Medium")
    }

    // LEVEL 10: Clockwork Gears (Triple intersecting round plates)
    private fun createLevel10(): LevelData {
        val h = listOf(
            GameHole("h1", 35f, 35f),
            GameHole("h2", 65f, 35f),
            GameHole("h3", 50f, 65f),
            GameHole("he1", 20f, 80f), // Empty
            GameHole("he2", 80f, 80f)  // Empty
        )
        val s = listOf(
            GameScrew("s1", "h1", "red"),
            GameScrew("s2", "h2", "blue"),
            GameScrew("s3", "h3", "green")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1"), "red", isCircular = true, circleRadius = 35f),
            GamePlate("p2", listOf("h2"), "blue", isCircular = true, circleRadius = 35f),
            GamePlate("p3", listOf("h3"), "green", isCircular = true, circleRadius = 35f)
        )
        val b = listOf(
            CargoBox("b1", "red", 1),
            CargoBox("b2", "blue", 1),
            CargoBox("b3", "green", 1)
        )
        return LevelData(10, "Clockwork Clutter", h, s, p, b, 110, "Hard")
    }

    // LEVEL 11: Prison Gate (Horizontal & Vertical grid-locks)
    private fun createLevel11(): LevelData {
        val h = listOf(
            GameHole("h11", 25f, 25f), GameHole("h12", 50f, 25f), GameHole("h13", 75f, 25f),
            GameHole("h21", 25f, 50f), GameHole("h22", 50f, 50f), GameHole("h23", 75f, 50f),
            GameHole("h31", 25f, 75f), GameHole("h32", 50f, 75f), GameHole("h33", 75f, 75f),
            GameHole("he1", 10f, 50f), GameHole("he2", 90f, 50f) // Empty targets
        )
        val s = listOf(
            GameScrew("s1", "h11", "red"), GameScrew("s2", "h12", "green"), GameScrew("s3", "h13", "blue"),
            GameScrew("s4", "h21", "silver"), GameScrew("s5", "h22", "red"), GameScrew("s6", "h23", "silver"),
            GameScrew("s7", "h31", "blue"), GameScrew("s8", "h32", "green"), GameScrew("s9", "h33", "red")
        )
        val p = listOf(
            GamePlate("pv1", listOf("h11", "h21", "h31"), "red", widthDp = 12f),
            GamePlate("pv2", listOf("h12", "h22", "h32"), "green", widthDp = 12f),
            GamePlate("pv3", listOf("h13", "h23", "h33"), "blue", widthDp = 12f),
            GamePlate("ph1", listOf("h21", "h22", "h23"), "silver", widthDp = 12f)
        )
        val b = listOf(
            CargoBox("b1", "red", 3),
            CargoBox("b2", "green", 2),
            CargoBox("b3", "blue", 2),
            CargoBox("b4", "silver", 2)
        )
        return LevelData(11, "Prison Gate Lock", h, s, p, b, 200, "Insane")
    }

    // LEVEL 12: The Pendulum (Stacked swinging layers)
    private fun createLevel12(): LevelData {
        val h = listOf(
            GameHole("h1", 50f, 15f), // Top central anchor
            GameHole("h2", 50f, 40f), // Second anchor
            GameHole("h3", 50f, 65f), // Third anchor
            GameHole("h4", 25f, 65f), // Supporting wing left
            GameHole("h5", 75f, 65f), // Supporting wing right
            GameHole("he1", 10f, 10f), // Empty
            GameHole("he2", 90f, 10f)  // Empty
        )
        val s = listOf(
            GameScrew("s1", "h1", "blue"),
            GameScrew("s2", "h2", "orange"),
            GameScrew("s3", "h3", "orange"),
            GameScrew("s4", "h4", "red"),
            GameScrew("s5", "h5", "red")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1", "h2"), "blue", widthDp = 16f),
            GamePlate("p2", listOf("h2", "h3"), "orange", widthDp = 16f),
            GamePlate("p3", listOf("h4", "h3", "h5"), "red", widthDp = 16f)
        )
        val b = listOf(
            CargoBox("b1", "blue", 1),
            CargoBox("b2", "orange", 2),
            CargoBox("b3", "red", 2)
        )
        return LevelData(12, "Triple Pendulum", h, s, p, b, 160, "Hard")
    }

    // LEVEL 13: Pyramid Scheme (Overlapping triangles)
    private fun createLevel13(): LevelData {
        val h = listOf(
            GameHole("h1", 50f, 20f), // Top
            GameHole("h2", 30f, 50f), // Mid-left
            GameHole("h3", 70f, 50f), // Mid-right
            GameHole("h4", 15f, 80f), // Bottom-left
            GameHole("h5", 50f, 80f), // Bottom-mid
            GameHole("h6", 85f, 80f),  // Bottom-right
            GameHole("he1", 50f, 50f)  // Empty central node
        )
        val s = listOf(
            GameScrew("s1", "h1", "orange"),
            GameScrew("s2", "h2", "green"),
            GameScrew("s3", "h3", "green"),
            GameScrew("s4", "h4", "blue"),
            GameScrew("s5", "h5", "blue"),
            GameScrew("s6", "h6", "blue")
        )
        val p = listOf(
            // Triangle 1: h1, h2, h3
            GamePlate("pt1", listOf("h1", "h2"), "orange", widthDp = 14f),
            GamePlate("pt2", listOf("h2", "h3"), "green", widthDp = 14f),
            GamePlate("pt3", listOf("h3", "h1"), "orange", widthDp = 14f),
            // Triangle 2: h2, h4, h5
            GamePlate("pt4", listOf("h2", "h4", "h5"), "blue", widthDp = 12f),
            // Triangle 3: h3, h5, h6
            GamePlate("pt5", listOf("h3", "h5", "h6"), "blue", widthDp = 12f)
        )
        val b = listOf(
            CargoBox("b1", "orange", 2),
            CargoBox("b2", "green", 2),
            CargoBox("b3", "blue", 3)
        )
        return LevelData(13, "Pyramid Gate", h, s, p, b, 180, "Hard")
    }

    // LEVEL 14: Helix Twist (Geometric spiral)
    private fun createLevel14(): LevelData {
        val h = listOf(
            GameHole("h1", 50f, 50f), // Center
            GameHole("h2", 50f, 30f), // Top offset
            GameHole("h3", 70f, 50f), // Right offset
            GameHole("h4", 50f, 70f), // Bottom offset
            GameHole("h5", 30f, 50f), // Left offset
            GameHole("h6", 65f, 35f), // Top-right diag
            GameHole("h7", 35f, 65f),  // Bottom-left diag
            GameHole("he1", 10f, 90f)  // Empty
        )
        val s = listOf(
            GameScrew("s1", "h1", "silver"),
            GameScrew("s2", "h2", "red"),
            GameScrew("s3", "h3", "blue"),
            GameScrew("s4", "h4", "red"),
            GameScrew("s5", "h5", "blue"),
            GameScrew("s6", "h6", "green"),
            GameScrew("s7", "h7", "green")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1", "h2", "h3"), "red", widthDp = 14f),
            GamePlate("p2", listOf("h1", "h4", "h5"), "blue", widthDp = 14f),
            GamePlate("p3", listOf("h1", "h6", "h7"), "green", widthDp = 14f)
        )
        val b = listOf(
            CargoBox("b1", "red", 2),
            CargoBox("b2", "blue", 2),
            CargoBox("b3", "green", 2),
            CargoBox("b4", "silver", 1)
        )
        return LevelData(14, "Helix Pinwheel", h, s, p, b, 150, "Hard")
    }

    // LEVEL 15: Boss Board (Full board locking structure with intense overlap)
    private fun createLevel15(): LevelData {
        val h = listOf(
            GameHole("h1", 20f, 20f), GameHole("h2", 50f, 20f), GameHole("h3", 80f, 20f),
            GameHole("h4", 20f, 50f), GameHole("h5", 50f, 50f), GameHole("h6", 80f, 50f),
            GameHole("h7", 20f, 80f), GameHole("h8", 50f, 80f), GameHole("h9", 80f, 80f),
            GameHole("he1", 10f, 35f), GameHole("he2", 90f, 35f),
            GameHole("he3", 10f, 65f), GameHole("he4", 90f, 65f)
        )
        val s = listOf(
            GameScrew("s1", "h1", "red"), GameScrew("s2", "h2", "green"), GameScrew("s3", "h3", "blue"),
            GameScrew("s4", "h4", "orange"), GameScrew("s5", "h5", "silver"), GameScrew("s6", "h6", "orange"),
            GameScrew("s7", "h7", "blue"), GameScrew("s8", "h8", "green"), GameScrew("s9", "h9", "red")
        )
        val p = listOf(
            GamePlate("p1", listOf("h1", "h2", "h3"), "red", widthDp = 15f),
            GamePlate("p2", listOf("h4", "h5", "h6"), "orange", widthDp = 15f),
            GamePlate("p3", listOf("h7", "h8", "h9"), "blue", widthDp = 15f),
            GamePlate("pv1", listOf("h1", "h4", "h7"), "silver", widthDp = 15f),
            GamePlate("pv2", listOf("h3", "h6", "h9"), "green", widthDp = 15f)
        )
        val b = listOf(
            CargoBox("b1", "red", 2),
            CargoBox("b2", "orange", 2),
            CargoBox("b3", "blue", 2),
            CargoBox("b4", "green", 2),
            CargoBox("b5", "silver", 1)
        )
        return LevelData(15, "Super Grid Lock", h, s, p, b, 240, "Expert")
    }

    // PROCEDURAL GENERATOR (For endless infinite levels after level 15)
    private fun generateProceduralLevel(levelId: Int): LevelData {
        val difficulty = when {
            levelId < 25 -> "Medium"
            levelId < 50 -> "Hard"
            else -> "Expert"
        }
        val timeLimit = 120 + (levelId * 3).coerceAtMost(180)
        
        // Let's create holes on a grid
        val cols = 4
        val rows = 5
        val holes = mutableListOf<GameHole>()
        
        for (r in 0 until rows) {
            val y = 20f + r * (60f / (rows - 1))
            for (c in 0 until cols) {
                val x = 20f + c * (60f / (cols - 1))
                holes.add(GameHole("ph_${r}_${c}", x, y))
            }
        }
        
        // Add random empty holes
        val emptyHolesCount = 4
        val allHoleIds = holes.map { it.id }.shuffled()
        val emptyHoleIds = allHoleIds.take(emptyHolesCount).toSet()
        
        val colorsPool = listOf("red", "green", "blue", "yellow", "orange")
        val selectedColors = colorsPool.shuffled().take((3 + (levelId % 3)).coerceAtMost(5))
        
        val screws = mutableListOf<GameScrew>()
        val plates = mutableListOf<GamePlate>()
        val boxesMap = mutableMapOf<String, Int>()
        
        var screwIndex = 0
        
        // Form plates horizontally first
        for (r in 0 until rows step 2) {
            val plateHoles = mutableListOf<String>()
            val color = selectedColors[r % selectedColors.size]
            for (c in 0 until cols) {
                val hId = "ph_${r}_${c}"
                plateHoles.add(hId)
                if (hId !in emptyHoleIds) {
                    val sId = "ps_${screwIndex++}"
                    screws.add(GameScrew(sId, hId, color))
                    boxesMap[color] = (boxesMap[color] ?: 0) + 1
                }
            }
            if (plateHoles.isNotEmpty()) {
                plates.add(GamePlate("pp_h_$r", plateHoles, color, widthDp = 14f))
            }
        }
        
        // Form plates vertically intersecting
        for (c in 0 until cols step 2) {
            val plateHoles = mutableListOf<String>()
            val color = selectedColors[(c + 1) % selectedColors.size]
            for (r in 0 until rows) {
                val hId = "ph_${r}_${c}"
                plateHoles.add(hId)
                if (hId !in emptyHoleIds && screws.none { it.holeId == hId }) {
                    val sId = "ps_${screwIndex++}"
                    screws.add(GameScrew(sId, hId, color))
                    boxesMap[color] = (boxesMap[color] ?: 0) + 1
                }
            }
            if (plateHoles.isNotEmpty()) {
                plates.add(GamePlate("pp_v_$c", plateHoles, color, widthDp = 12f))
            }
        }
        
        // Build cargo boxes
        val boxes = boxesMap.map { (color, count) ->
            CargoBox("pbox_$color", color, count)
        }
        
        return LevelData(
            levelId = levelId,
            name = "Infinite Maze $levelId",
            holes = holes,
            screws = screws,
            plates = plates,
            targetBoxes = boxes,
            timeLimitSeconds = timeLimit,
            defaultDifficulty = difficulty
        )
    }
}
