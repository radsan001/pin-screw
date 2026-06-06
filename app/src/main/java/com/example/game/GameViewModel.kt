package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.GameProgressEntity
import com.example.data.model.LevelProgressEntity
import com.example.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.atan2

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    
    // Core Game States
    private val _currentLevelId = MutableStateFlow(1)
    val currentLevelId: StateFlow<Int> = _currentLevelId.asStateFlow()

    private val _levelName = MutableStateFlow("")
    val levelName: StateFlow<String> = _levelName.asStateFlow()

    private val _holes = MutableStateFlow<List<GameHole>>(emptyList())
    val holes: StateFlow<List<GameHole>> = _holes.asStateFlow()

    private val _screws = MutableStateFlow<List<GameScrew>>(emptyList())
    val screws: StateFlow<List<GameScrew>> = _screws.asStateFlow()

    private val _plates = MutableStateFlow<List<GamePlate>>(emptyList())
    val plates: StateFlow<List<GamePlate>> = _plates.asStateFlow()

    private val _cargoBoxes = MutableStateFlow<List<CargoBox>>(emptyList())
    val cargoBoxes: StateFlow<List<CargoBox>> = _cargoBoxes.asStateFlow()

    // Sort Belt Slot (limited tray capacity, e.g. 5)
    private val _sortingTray = MutableStateFlow<List<GameScrew>>(emptyList())
    val sortingTray: StateFlow<List<GameScrew>> = _sortingTray.asStateFlow()

    private val _selectedScrewId = MutableStateFlow<String?>(null)
    val selectedScrewId: StateFlow<String?> = _selectedScrewId.asStateFlow()

    private val _timeRemaining = MutableStateFlow(120)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isGameActive = MutableStateFlow(false)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _isGameWon = MutableStateFlow(false)
    val isGameWon: StateFlow<Boolean> = _isGameWon.asStateFlow()

    // Particle Burst effects state
    data class SparkParticle(val id: Int, val startX: Float, val startY: Float, val color: String, val randomAngle: Float)
    private val _sparks = MutableStateFlow<List<SparkParticle>>(emptyList())
    val sparks: StateFlow<List<SparkParticle>> = _sparks.asStateFlow()

    // Animating physics states
    private val _fallingPlates = MutableStateFlow<Set<String>>(emptySet())
    val fallingPlates: StateFlow<Set<String>> = _fallingPlates.asStateFlow()

    // Map of Plate ID -> Pivot physics info (pivotHoleId, currentRotationOffsetRadians)
    data class PlatePivotInfo(val pivotHoleId: String, val targetAngleOffset: Float)
    private val _platePivots = MutableStateFlow<Map<String, PlatePivotInfo>>(emptyMap())
    val platePivots: StateFlow<Map<String, PlatePivotInfo>> = _platePivots.asStateFlow()

    // Game configurations and Shop states
    private val _coins = MutableStateFlow(200)
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _activeTheme = MutableStateFlow(GameThemePresets.WoodTheme)
    val activeTheme: StateFlow<GameTheme> = _activeTheme.asStateFlow()

    private val _unlockedThemeIds = MutableStateFlow(listOf("wood"))
    val unlockedThemeIds: StateFlow<List<String>> = _unlockedThemeIds.asStateFlow()

    private val _boosterHammerCount = MutableStateFlow(3)
    val boosterHammerCount: StateFlow<Int> = _boosterHammerCount.asStateFlow()

    private val _boosterExtraHoleCount = MutableStateFlow(3)
    val boosterExtraHoleCount: StateFlow<Int> = _boosterExtraHoleCount.asStateFlow()

    private val _boosterUndoCount = MutableStateFlow(3)
    val boosterUndoCount: StateFlow<Int> = _boosterUndoCount.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _gameMode = MutableStateFlow("SORT") // "SORT" or "SOLITAIRE"
    val gameMode: StateFlow<String> = _gameMode.asStateFlow()

    private val _allLevelsProgress = MutableStateFlow<List<LevelProgressEntity>>(emptyList())
    val allLevelsProgress: StateFlow<List<LevelProgressEntity>> = _allLevelsProgress.asStateFlow()

    // Game Undo stack state history
    private val undoHistory = mutableListOf<GameStateBackup>()

    private var timerJob: Job? = null
    private var sortingTraySlotsCount = 5

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())

        // Start listening to DB models
        viewModelScope.launch {
            repository.gameProgress.collectLatest { progress ->
                if (progress != null) {
                    _coins.value = progress.coins
                    _activeTheme.value = GameThemePresets.getById(progress.activeThemeId)
                    _unlockedThemeIds.value = progress.unlockedThemeIds.split(",")
                    _boosterHammerCount.value = progress.boosterHammerCount
                    _boosterExtraHoleCount.value = progress.boosterExtraHoleCount
                    _boosterUndoCount.value = progress.boosterUndoCount
                    _soundEnabled.value = progress.soundEnabled
                    _vibrationEnabled.value = progress.vibrationEnabled
                    _gameMode.value = progress.selectedMode
                } else {
                    // Seed database if empty
                    repository.updateGameProgress(GameProgressEntity())
                }
            }
        }

        viewModelScope.launch {
            repository.allLevelProgress.collectLatest { progressList ->
                _allLevelsProgress.value = progressList
            }
        }
    }

    fun startLevel(levelId: Int) {
        _currentLevelId.value = levelId
        undoHistory.clear()
        _fallingPlates.value = emptySet()
        _platePivots.value = emptyMap()
        _sortingTray.value = emptyList()
        _selectedScrewId.value = null
        _isGameOver.value = false
        _isGameWon.value = false

        val rawLevel = LevelPresetData.getLevel(levelId)
        _levelName.value = rawLevel.name
        _holes.value = rawLevel.holes
        _screws.value = rawLevel.screws
        _plates.value = rawLevel.plates
        _timeRemaining.value = rawLevel.timeLimitSeconds

        // Adapt target boxes if playing in sort vs solitaire mode
        if (_gameMode.value == "SORT") {
            _cargoBoxes.value = rawLevel.targetBoxes.map { it.copy() }
        } else {
            _cargoBoxes.value = emptyList()
        }

        _isGameActive.value = true
        startTimer()
        updatePlatesPhysics()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isGameActive.value && _timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value -= 1
            }
            if (_timeRemaining.value <= 0) {
                triggerGameOver()
            }
        }
    }

    fun selectMode(mode: String) {
        _gameMode.value = mode
        viewModelScope.launch {
            repository.selectMode(mode)
        }
    }

    // Backup current game state for Undo mechanics
    private fun saveBackup() {
        undoHistory.add(
            GameStateBackup(
                screws = _screws.value.toList(),
                holes = _holes.value.toList(),
                cargoBoxes = _cargoBoxes.value.toList(),
                sortingTray = _sortingTray.value.toList(),
                fallingPlates = _fallingPlates.value.toSet(),
                platePivots = _platePivots.value.toMap()
            )
        )
        if (undoHistory.size > 8) {
            undoHistory.removeAt(0)
        }
    }

    // MAIN PLAYER TAP RESOLVER
    fun handleHoleTap(holeId: String) {
        if (!_isGameActive.value || _isGameOver.value || _isGameWon.value) return

        val clickedScrew = _screws.value.find { it.holeId == holeId }
        val isSelectionActive = _selectedScrewId.value != null

        if (clickedScrew != null) {
            // Player clicked on a screw
            if (_gameMode.value == "SORT") {
                // SORT MODE: Tapping a screw unscrews it and sends it straight to the tray slots
                unscrewToTray(clickedScrew)
            } else {
                // CLASSIC MODE: Click selects/deselects a screw for moving
                if (_selectedScrewId.value == clickedScrew.id) {
                    _selectedScrewId.value = null // Deselect
                } else {
                    _selectedScrewId.value = clickedScrew.id
                }
            }
        } else {
            // Player clicked an empty hole
            if (_gameMode.value == "SOLITAIRE" && isSelectionActive) {
                solveManualPlacement(holeId)
            }
        }
    }

    // CLASSIC MODE manual screw placement logic
    private fun solveManualPlacement(targetHoleId: String) {
        val screwId = _selectedScrewId.value ?: return
        val currentScrew = _screws.value.find { it.id == screwId } ?: return

        saveBackup()

        // Update screw destination hole
        val prevHoleId = currentScrew.holeId
        _screws.value = _screws.value.map {
            if (it.id == screwId) it.copy(holeId = targetHoleId) else it
        }

        _selectedScrewId.value = null
        triggerSparkBurst(targetHoleId)

        // Recalculately gravity vectors
        updatePlatesPhysics()
        checkWinCondition()
    }

    // SORT MODE (Screw Jam) logic
    private fun unscrewToTray(screw: GameScrew) {
        val currentTray = _sortingTray.value
        if (currentTray.size >= sortingTraySlotsCount) {
            // Slot tray is full! Player is blocked!
            triggerGameOver()
            return
        }

        saveBackup()

        // Remove from the board hole
        val updatedBoardScrews = _screws.value.map {
            if (it.id == screw.id) it.copy(holeId = "") else it
        }
        _screws.value = updatedBoardScrews

        // Place in tray slot
        val screwInTray = screw.copy(holeId = "") // in tray
        val updatedTray = currentTray + screwInTray
        _sortingTray.value = updatedTray

        // Trigger sparks & physical sway
        triggerSparkBurst(screw.holeId)
        updatePlatesPhysics()

        // Process sorting mechanics
        viewModelScope.launch {
            processSortingSequence()
            checkWinCondition()
        }
    }

    // Animated color matching of screws to arriving boxes
    private suspend fun processSortingSequence() {
        var sortingNeeded = true
        while (sortingNeeded) {
            val tray = _sortingTray.value
            val activeBoxes = _cargoBoxes.value
            val activeBoxIndex = activeBoxes.indexOfFirst { it.loadedScrewsCount < it.capacity }

            if (activeBoxIndex == -1) {
                // All current boxes are filled!
                sortingNeeded = false
                break
            }

            val currentBox = activeBoxes[activeBoxIndex]
            val matchingScrewIndex = tray.indexOfFirst { it.colorId == currentBox.colorId }

            if (matchingScrewIndex != -1) {
                // Match found! Animate transfer
                delay(250) // Mini-delay to simulate physical flight to cargo box

                val matchedScrew = tray[matchingScrewIndex]

                // Slide unmatched items left
                val mutableTray = tray.toMutableList()
                mutableTray.removeAt(matchingScrewIndex)
                _sortingTray.value = mutableTray

                // Feed the cargo box
                _cargoBoxes.value = _cargoBoxes.value.mapIndexed { idx, box ->
                    if (idx == activeBoxIndex) box.copy(loadedScrewsCount = box.loadedScrewsCount + 1) else box
                }

                // Chaining reward coins
                repository.addCoins(2)

                // If this box becomes fully packed, play animations & slide it out
                val freshlyLoadedBox = _cargoBoxes.value[activeBoxIndex]
                if (freshlyLoadedBox.loadedScrewsCount >= freshlyLoadedBox.capacity) {
                    delay(400) // Slide box away animation buffer
                }
            } else {
                sortingNeeded = false
            }
        }

        // Check if slots are filled and can't make matches anymore
        if (_sortingTray.value.size >= sortingTraySlotsCount) {
            val activeBoxes = _cargoBoxes.value
            val currentBox = activeBoxes.find { it.loadedScrewsCount < it.capacity }
            val hasActiveBoxMatches = currentBox != null && _sortingTray.value.any { it.colorId == currentBox.colorId }
            
            if (!hasActiveBoxMatches) {
                triggerGameOver()
            }
        }
    }

    // Dynamic Gravity simulation and trigonometry calculation of screw hangers
    private fun updatePlatesPhysics() {
        val currentScrews = _screws.value
        val currentPlates = _plates.value
        val boardHoles = _holes.value
        val fallingSet = _fallingPlates.value.toMutableSet()
        val pivotsMap = _platePivots.value.toMutableMap()

        for (plate in currentPlates) {
            if (plate.id in fallingSet) continue

            // Determine which anchoring holes actually contain a screw right now on the board page
            val activeAnchors = plate.attachedHoleIds.filter { hId ->
                currentScrews.any { it.holeId == hId }
            }

            when (activeAnchors.size) {
                0 -> {
                    // 0 Screws: This plate has no support left! It falls!
                    fallingSet.add(plate.id)
                    pivotsMap.remove(plate.id)
                    
                    // Award bonus score/coins for drops
                    viewModelScope.launch {
                        repository.addCoins(10)
                        triggerPlateFallAnimation(plate.id)
                    }
                }
                1 -> {
                    // Exactly 1 screw left: The plate rotates and sways downward under gravity pivoting on this 1 screw!
                    val pivotHoleId = activeAnchors[0]
                    val pivotHole = boardHoles.find { it.id == pivotHoleId }
                    
                    if (pivotHole != null) {
                        // Calculate target rotation to make it point vertically downwards relative to initial shape layout
                        // Let's compute default geometric angle of plate from the pivot to other anchors
                        val otherHoleId = plate.attachedHoleIds.firstOrNull { it != pivotHoleId }
                        val otherHole = boardHoles.find { it.id == otherHoleId }
                        
                        val targetAngleOffset = if (otherHole != null) {
                            val dy = otherHole.y - pivotHole.y
                            val dx = otherHole.x - pivotHole.x
                            val originalAngle = atan2(dy, dx)
                            // Gravitational vertical down is PI/2 radians (90 deg). We align original angle to head down.
                            val verticalDownAngle = (Math.PI / 2).toFloat()
                            val difference = verticalDownAngle - originalAngle
                            difference.toFloat()
                        } else {
                            0f
                        }

                        pivotsMap[plate.id] = PlatePivotInfo(pivotHoleId, targetAngleOffset)
                    }
                }
                else -> {
                    // 2 or more screws: Fixed stable plate positions, no rotation offset
                    pivotsMap.remove(plate.id)
                }
            }
        }

        _fallingPlates.value = fallingSet
        _platePivots.value = pivotsMap
    }

    private suspend fun triggerPlateFallAnimation(plateId: String) {
        delay(1200) // Allow smooth translation and exit before purging completely from the layout tree
        _plates.value = _plates.value.filter { it.id != plateId }
        val remainingFalling = _fallingPlates.value.toMutableSet()
        remainingFalling.remove(plateId)
        _fallingPlates.value = remainingFalling
        checkWinCondition()
    }

    private fun checkWinCondition() {
        if (_isGameOver.value || _isGameWon.value) return

        val allPlatesCleared = _plates.value.isEmpty()
        
        val modeWin = if (_gameMode.value == "SORT") {
            val allBoxesFilled = _cargoBoxes.value.all { it.loadedScrewsCount >= it.capacity }
            allBoxesFilled || allPlatesCleared
        } else {
            allPlatesCleared
        }

        if (modeWin) {
            triggerWin()
        }
    }

    private fun triggerWin() {
        _isGameActive.value = false
        _isGameWon.value = true
        timerJob?.cancel()

        val starsScored = when {
            _timeRemaining.value > 90 -> 3
            _timeRemaining.value > 45 -> 2
            else -> 1
        }

        val bonusCoins = 15 + (_timeRemaining.value / 3)

        viewModelScope.launch {
            repository.addCoins(bonusCoins)
            repository.updateLevelProgress(
                levelId = _currentLevelId.value,
                completed = true,
                stars = starsScored,
                highScore = (_timeRemaining.value * 25)
            )
        }
    }

    private fun triggerGameOver() {
        _isGameActive.value = false
        _isGameOver.value = true
        timerJob?.cancel()
    }

    // SPARK ENGINE (Visual particles)
    private fun triggerSparkBurst(holeId: String) {
        val targetHole = _holes.value.find { it.id == holeId } ?: return
        val colors = listOf("red", "green", "blue", "yellow", "orange", "silver")
        val randomColor = colors.random()

        val newSparks = (1..12).map { sparkId ->
            SparkParticle(
                id = (0..10000).random(),
                startX = targetHole.x,
                startY = targetHole.y,
                color = randomColor,
                randomAngle = (Math.random() * Math.PI * 2).toFloat()
            )
        }
        _sparks.value = newSparks

        viewModelScope.launch {
            delay(500)
            _sparks.value = emptyList() // clear sparks
        }
    }

    // ==========================================
    // ACTION HELPERS / SHOP PURCHASING
    // ==========================================

    fun useHammerBooster(holeId: String): Boolean {
        val targetScrew = _screws.value.find { it.holeId == holeId } ?: return false

        saveBackup()

        viewModelScope.launch {
            val used = repository.useBooster("hammer")
            if (used) {
                _screws.value = _screws.value.filter { it.id != targetScrew.id }
                triggerSparkBurst(holeId)
                updatePlatesPhysics()
                checkWinCondition()
            }
        }
        return true
    }

    fun useExtraHoleBooster() {
        viewModelScope.launch {
            val used = repository.useBooster("extra_hole")
            if (used) {
                saveBackup()
                val nextHoleNum = _holes.value.size + 1
                // Add unique extra hole in spacing
                val xCoord = if (nextHoleNum % 2 == 0) 15f else 85f
                val yCoord = 88f
                val extraHole = GameHole("h_extra_$nextHoleNum", xCoord, yCoord)
                
                _holes.value = _holes.value + extraHole
                triggerSparkBurst(extraHole.id)
            }
        }
    }

    fun performUndo() {
        if (undoHistory.isNotEmpty() && _boosterUndoCount.value > 0) {
            viewModelScope.launch {
                val used = repository.useBooster("undo")
                if (used) {
                    val lastState = undoHistory.removeAt(undoHistory.size - 1)
                    _screws.value = lastState.screws
                    _holes.value = lastState.holes
                    _cargoBoxes.value = lastState.cargoBoxes
                    _sortingTray.value = lastState.sortingTray
                    _fallingPlates.value = lastState.fallingPlates
                    _platePivots.value = lastState.platePivots
                    _selectedScrewId.value = null
                }
            }
        }
    }

    fun buyShopBooster(boosterType: String, cost: Int) {
        viewModelScope.launch {
            repository.buyBooster(boosterType, cost)
        }
    }

    fun buyOrSelectTheme(themeId: String, cost: Int) {
        viewModelScope.launch {
            val unlocked = _unlockedThemeIds.value.contains(themeId)
            if (unlocked) {
                repository.selectTheme(themeId)
            } else {
                repository.unlockTheme(themeId, cost)
            }
        }
    }

    fun toggleAudioSetting() {
        viewModelScope.launch {
            repository.toggleSound()
        }
    }

    fun toggleHapticSetting() {
        viewModelScope.launch {
            repository.toggleVibration()
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

// Backup structure for full level Undo
data class GameStateBackup(
    val screws: List<GameScrew>,
    val holes: List<GameHole>,
    val cargoBoxes: List<CargoBox>,
    val sortingTray: List<GameScrew>,
    val fallingPlates: Set<String>,
    val platePivots: Map<String, GameViewModel.PlatePivotInfo>
)
