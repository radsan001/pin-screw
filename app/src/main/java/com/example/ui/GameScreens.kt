package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LevelProgressEntity
import com.example.game.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onLevelSelected: (Int) -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val activeMode by viewModel.gameMode.collectAsStateWithLifecycle()
    val levelsProgress by viewModel.allLevelsProgress.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2C1919), Color(0xFF140D0D))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // HEADER ROW: Coins and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound and Haptic quick buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleAudioSetting() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF3D2525))
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sound",
                            tint = Color(0xFFFFD700)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleHapticSetting() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF3D2525))
                    ) {
                        Icon(
                            imageVector = if (vibrationEnabled) Icons.Default.Vibration else Icons.Default.NetworkCheck,
                            contentDescription = "Haptics",
                            tint = Color(0xFFFFD700)
                        )
                    }
                }

                // Coins Indicator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3D2525))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = coins.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // BRAND LOGO DISPLAY
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SCREW SOLVER",
                    color = Color(0xFFFFD700),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "NUTS & BOLTS MASTER",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // GAME MODES TABS SELECTOR
            Text(
                text = "CHOOSE YOUR PUZZLE TYPE:",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1111))
                    .border(2.dp, Color(0xFF3D2525), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 1: Sort Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeMode == "SORT") Color(0xFFFF8C00) else Color.Transparent)
                        .clickable { viewModel.selectMode("SORT") },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = if (activeMode == "SORT") Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Color Sort Jam",
                            color = if (activeMode == "SORT") Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Tab 2: Solitaire Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeMode == "SOLITAIRE") Color(0xFFFF8C00) else Color.Transparent)
                        .clickable { viewModel.selectMode("SOLITAIRE") },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = if (activeMode == "SOLITAIRE") Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Unbolt Solitaire",
                            color = if (activeMode == "SOLITAIRE") Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // MODE EXPLAINER BADGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261515)),
                border = BorderStroke(1.dp, Color(0xFF3D2525))
            ) {
                Text(
                    text = if (activeMode == "SORT") {
                        "★ SCREW JAM SORTING: Extract colored bolts into slots. Fill moving cargo packages of matching colors before slots block!"
                    } else {
                        "★ CLASSIC SCREW SOLVER: Relocate screws from pins to empty layout holes. Release suspended plates to let gravity drop them!"
                    },
                    color = Color(0xFFFFD700).copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LEVEL SECTOR TITLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT LEVEL:",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                // Theme Shop navigation
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFA500))
                        .clickable { onOpenShop() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "THEME SHOP",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GRID CARD LIST of Levels (from 1 to 15, and Endless mode)
            val levelIndices = (1..16).toList()

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(levelIndices) { num ->
                    val isEndless = num == 16
                    val currentProgress = levelsProgress.find { it.levelId == num }
                    val isPreviousCompleted = num == 1 || levelsProgress.any { it.levelId == num - 1 && it.completed }
                    val isUnlocked = isEndless || isPreviousCompleted

                    LevelGridCard(
                        levelNum = num,
                        isUnlocked = isUnlocked,
                        isEndless = isEndless,
                        progress = currentProgress,
                        onClick = { if (isUnlocked) onLevelSelected(num) }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelGridCard(
    levelNum: Int,
    isUnlocked: Boolean,
    isEndless: Boolean,
    progress: LevelProgressEntity?,
    onClick: () -> Unit
) {
    val containerColor = when {
        isEndless -> Color(0xFF6A0DAD) // Purple for Endless
        !isUnlocked -> Color(0xFF1E1E1E).copy(alpha = 0.5f)
        progress?.completed == true -> Color(0xFF34495E) // Completed
        else -> Color(0xFF3E2723) // Open
    }

    val borderWidth = if (isUnlocked) 2.dp else 1.dp
    val borderColor = when {
        isEndless -> Color(0xFFDA70D6)
        !isUnlocked -> Color(0xFF333333)
        progress?.completed == true -> Color(0xFF2ECC71)
        else -> Color(0xFFFF8C00)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (isUnlocked) 4.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isUnlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEndless) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Endless Mode",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ENDLESS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Text(
                        text = "Lvl $levelNum",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    if (progress?.completed == true) {
                        // Draw mini stars scored
                        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                            repeat(3) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < progress.stars) Color(0xFFFFD700) else Color.Gray,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    } else {
                        val difficulty = when {
                            levelNum < 4 -> "EASY"
                            levelNum < 8 -> "MID"
                            levelNum < 12 -> "HARD"
                            else -> "PRO"
                        }
                        Text(
                            text = difficulty,
                            color = when (difficulty) {
                                "EASY" -> Color(0xFF2ECC71)
                                "MID" -> Color(0xFFF1C40F)
                                "HARD" -> Color(0xFFE67E22)
                                else -> Color(0xFFE74C3C)
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// MAIN ACTION PLATFORM GAMEPLAY SCREEN
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLevel by viewModel.currentLevelId.collectAsStateWithLifecycle()
    val levelName by viewModel.levelName.collectAsStateWithLifecycle()
    val holes by viewModel.holes.collectAsStateWithLifecycle()
    val screws by viewModel.screws.collectAsStateWithLifecycle()
    val plates by viewModel.plates.collectAsStateWithLifecycle()
    val cargoBoxes by viewModel.cargoBoxes.collectAsStateWithLifecycle()
    val sortingTray by viewModel.sortingTray.collectAsStateWithLifecycle()
    val selectedScrewId by viewModel.selectedScrewId.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.timeRemaining.collectAsStateWithLifecycle()
    val isGameOver by viewModel.isGameOver.collectAsStateWithLifecycle()
    val isGameWon by viewModel.isGameWon.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()

    val hammerCount by viewModel.boosterHammerCount.collectAsStateWithLifecycle()
    val extraHoleCount by viewModel.boosterExtraHoleCount.collectAsStateWithLifecycle()
    val undoCount by viewModel.boosterUndoCount.collectAsStateWithLifecycle()
    val sparks by viewModel.sparks.collectAsStateWithLifecycle()

    // Animation frames tracking
    val fallingPlates by viewModel.fallingPlates.collectAsStateWithLifecycle()
    val platePivots by viewModel.platePivots.collectAsStateWithLifecycle()

    // Mode tracker
    val activeMode by viewModel.gameMode.collectAsStateWithLifecycle()

    // Temporary active tools state
    var toolSelectedType by remember { mutableStateOf<String?>(null) } // "hammer" or null
    var showShopQuickDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = activeTheme.boardBgGradient
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // STEP 1: HUD TITLE ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                IconButton(
                    onClick = { onBackToMenu() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Title info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (currentLevel <= 15) "LEVEL $currentLevel" else "ENDLESS PLAY",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = levelName,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Balance indicator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = coins.toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // STEP 2: TIMER AND RESTART BUTTON LINE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Timer displaying seconds
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Time remaining",
                        tint = when {
                            timeRemaining < 20 -> Color.Red
                            timeRemaining < 45 -> Color.Yellow
                            else -> Color(0xFF2ECC71)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${timeRemaining / 60}:${String.format("%02d", timeRemaining % 60)}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Quick restart icon
                Row {
                    IconButton(
                        onClick = { viewModel.startLevel(currentLevel) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Level",
                            tint = Color.White
                        )
                    }
                }
            }

            // STEP 3: ACTIVATED CARGO BELT SECTION (Sort-mode only)
            if (activeMode == "SORT" && cargoBoxes.isNotEmpty()) {
                CargoBeltCarousel(cargoBoxes = cargoBoxes, activeTheme = activeTheme)
            }

            // STEP 4: EXTRACTION TRAY SLOT DISPLAY (Sort-mode only)
            if (activeMode == "SORT") {
                SortingTrayBelts(slots = sortingTray, limitSize = 5, activeTheme = activeTheme)
            }

            // STEP 5: THE CORE INTERACTIVE BOARD GAME CANVAS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF201313).copy(alpha = 0.4f))
                    .border(2.dp, activeTheme.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                InteractiveBoardCanvas(
                    holes = holes,
                    screws = screws,
                    plates = plates,
                    theme = activeTheme,
                    fallingPlates = fallingPlates,
                    platePivots = platePivots,
                    selectedScrewId = selectedScrewId,
                    toolActive = toolSelectedType,
                    onHoleTapped = { holeId ->
                        if (toolSelectedType == "hammer") {
                            val crushed = viewModel.useHammerBooster(holeId)
                            if (crushed) toolSelectedType = null
                        } else {
                            viewModel.handleHoleTap(holeId)
                        }
                    },
                    sparks = sparks
                )

                // Flash notice if Hammer Tool is selected
                if (toolSelectedType == "hammer") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Red)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🛠 TAP ANY BOLT TO SMASH IT!",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // STEP 6: BOOSTERS ACTION COMPANION TOOLBAR
            GameBoosterToolbar(
                hammerCount = hammerCount,
                extraHoleCount = extraHoleCount,
                undoCount = undoCount,
                onHammerClick = {
                    toolSelectedType = if (toolSelectedType == "hammer") null else "hammer"
                },
                onExtraHoleClick = {
                    if (extraHoleCount > 0) {
                        viewModel.useExtraHoleBooster()
                    } else {
                        showShopQuickDialog = true
                    }
                },
                onUndoClick = {
                    if (undoCount > 0) {
                        viewModel.performUndo()
                    } else {
                        showShopQuickDialog = true
                    }
                },
                onOpenShopQuick = { showShopQuickDialog = true }
            )
        }

        // STEP 7: WIN / GAME OVER OVERLAYS
        if (isGameWon) {
            LevelWinOverlayDialog(
                levelId = currentLevel,
                stars = when {
                    timeRemaining > 90 -> 3
                    timeRemaining > 45 -> 2
                    else -> 1
                },
                coinsWon = 15 + (timeRemaining / 3),
                onNextLevel = { viewModel.startLevel(currentLevel + 1) },
                onBack = onBackToMenu
            )
        }

        if (isGameOver) {
            LevelDefeatOverlayDialog(
                levelId = currentLevel,
                onRetry = { viewModel.startLevel(currentLevel) },
                onBack = onBackToMenu
            )
        }

        if (showShopQuickDialog) {
            QuickBoosterShopDialog(
                coins = coins,
                onClose = { showShopQuickDialog = false },
                onBuyBooster = { type, price ->
                    viewModel.buyShopBooster(type, price)
                }
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: CARGO CONVEYOR BELT BOXES DISPLAY
// -------------------------------------------------------------
@Composable
fun CargoBeltCarousel(
    cargoBoxes: List<CargoBox>,
    activeTheme: GameTheme,
    modifier: Modifier = Modifier
) {
    // Determine the first unsatisfied index
    val activeIdx = cargoBoxes.indexOfFirst { it.loadedScrewsCount < it.capacity }.coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF160E0E))
            .border(1.dp, Color(0xFF2C1919), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CARGO SHIPMENTS INBOUND",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "NEXT DELIVERY",
                color = Color(0xFFFF8C00),
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            cargoBoxes.forEachIndexed { idx, box ->
                val isCurrentActiveBox = idx == activeIdx
                val isPacked = box.loadedScrewsCount >= box.capacity
                
                val boxTargetColor = activeTheme.screwColors[box.colorId] ?: Color.LightGray

                val scale by animateFloatAsState(
                    targetValue = if (isCurrentActiveBox) 1.05f else 0.9f,
                    animationSpec = spring()
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCurrentActiveBox) Color(0xFF2A1B1B) else Color(0xFF100A0A)
                        )
                        .border(
                            width = if (isCurrentActiveBox) 2.dp else 1.dp,
                            color = if (isCurrentActiveBox) boxTargetColor else Color.DarkGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Drawing miniature visual cargo boxes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(boxTargetColor)
                            )
                            Text(
                                text = box.colorId.uppercase(),
                                color = if (isCurrentActiveBox) Color.White else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Capacity loaded text counter
                        Text(
                            text = if (isPacked) "✔ SEED" else "${box.loadedScrewsCount}/${box.capacity}",
                            color = if (isPacked) Color(0xFF2ECC71) else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Miniature linear loader bar
                        LinearProgressIndicator(
                            progress = {
                                if (box.capacity > 0) box.loadedScrewsCount.toFloat() / box.capacity else 0f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = boxTargetColor,
                            trackColor = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

// Helper to scale element sizes
fun Modifier.scale(scale: Float): Modifier = this.then(
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
)

// -------------------------------------------------------------
// COMPONENT: EXTRACTED SCREWS SLOT HOLDER (Toolbelt Tray)
// -------------------------------------------------------------
@Composable
fun SortingTrayBelts(
    slots: List<GameScrew>,
    limitSize: Int = 5,
    activeTheme: GameTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1414))
            .border(1.dp, Color(0xFF331E1E), RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BOLT TRAY CAPACITY: ${slots.size} / $limitSize",
                color = when {
                    slots.size >= limitSize - 1 -> Color.Red
                    slots.size >= limitSize - 2 -> Color.Yellow
                    else -> Color.White.copy(alpha = 0.5f)
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Draw slots
            for (i in 0 until limitSize) {
                val holdsIndexItem = slots.getOrNull(i)

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F0A0A))
                        .border(
                            BorderStroke(
                                1.dp,
                                if (holdsIndexItem != null) activeTheme.primaryColor else Color.DarkGray
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (holdsIndexItem != null) {
                        // Drawing individual colorful screw in the slot
                        val col = activeTheme.screwColors[holdsIndexItem.colorId] ?: Color.White
                        
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(col, Color.Black),
                                        center = Offset(20f, 20f)
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Crosshead details drawn in sorting tray
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(3.dp)
                                    .background(Color.White.copy(alpha = 0.4f))
                            )
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(14.dp)
                                    .background(Color.White.copy(alpha = 0.4f))
                            )
                        }
                    } else {
                        // Empty slot indicator dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: CORE SCALED GAME BOARD CANVAS RENDERER
// -------------------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InteractiveBoardCanvas(
    holes: List<GameHole>,
    screws: List<GameScrew>,
    plates: List<GamePlate>,
    theme: GameTheme,
    fallingPlates: Set<String>,
    platePivots: Map<String, GameViewModel.PlatePivotInfo>,
    selectedScrewId: String?,
    toolActive: String?,
    onHoleTapped: (String) -> Unit,
    sparks: List<GameViewModel.SparkParticle>,
    modifier: Modifier = Modifier
) {
    val scaleAnimatorsMap = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }
    val density = LocalDensity.current

    // Trigger pulse selection loops
    val infiniteTransition = rememberInfiniteTransition()
    val pulseSelectorScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(holes, screws, plates) {
                // Core gesture detector mapping relative coordinates
                detectTapCoordinates { offset ->
                    val boardWidth = size.width.toFloat()
                    val boardHeight = size.height.toFloat()

                    // Match clicks with local holes
                    var closestHole: GameHole? = null
                    var minDistance = Float.MAX_VALUE

                    for (hole in holes) {
                        val hx = hole.x * boardWidth / 100f
                        val hy = hole.y * boardHeight / 100f

                        val dx = offset.x - hx
                        val dy = offset.y - hy
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                        // 36dp match range
                        val touchRadius = with(density) { 36.dp.toPx() }
                        if (distance < touchRadius && distance < minDistance) {
                            closestHole = hole
                            minDistance = distance
                        }
                    }

                    closestHole?.let { onHoleTapped(it.id) }
                }
            }
    ) {
        val boardWidth = constraints.maxWidth.toFloat()
        val boardHeight = constraints.maxHeight.toFloat()

        val scope = rememberCoroutineScope()
        LaunchedEffect(fallingPlates) {
            fallingPlates.forEach { pId ->
                if (!scaleAnimatorsMap.contains(pId)) {
                    val anim = Animatable(0f)
                    scaleAnimatorsMap[pId] = anim
                    scope.launch {
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(1200, easing = FastOutSlowInEasing)
                        )
                    }
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // STEP 1: Draw Board Layout Grid lines
            drawBoardBackgroundOrnament(theme)

            // STEP 2: Draw Plates (layered first so bolts are rendered on top)
            for (p in plates) {
                val isPivoted = platePivots.containsKey(p.id)
                val isFalling = fallingPlates.contains(p.id)

                val plateColorValue = theme.plateColors[p.colorId] ?: Color(0xFF8B5A2B)

                // Render styles adjustments
                var translationY = 0f
                var alphaVal = 1f
                var angleDegrees = 0f
                var pivotPoint = Offset.Zero

                if (isFalling) {
                    val completionFraction = scaleAnimatorsMap[p.id]?.value ?: 0f
                    translationY = completionFraction * boardHeight * 0.9f
                    alphaVal = 1f - completionFraction
                }

                if (isPivoted) {
                    val physicsPivot = platePivots[p.id]!!
                    val pivotHole = holes.find { it.id == physicsPivot.pivotHoleId }
                    if (pivotHole != null) {
                        pivotPoint = Offset(
                            pivotHole.x * boardWidth / 100f,
                            pivotHole.y * boardHeight / 100f + translationY
                        )
                        // Animate rotational drop to downward gravity
                        val fraction = scaleAnimatorsMap[p.id]?.value ?: 1f
                        angleDegrees = (physicsPivot.targetAngleOffset * 180f / Math.PI).toFloat() * fraction
                    }
                }

                // Call rotated canvas layer
                rotate(degrees = angleDegrees, pivot = pivotPoint) {
                    drawIndividualPlateGeo(
                        plate = p,
                        holesList = holes,
                        color = plateColorValue,
                        boardWidth = boardWidth,
                        boardHeight = boardHeight,
                        translationY = translationY,
                        alpha = alphaVal,
                        theme = theme
                    )
                }
            }

            // STEP 3: Draw empty layout Holes
            for (h in holes) {
                val hx = h.x * boardWidth / 100f
                val hy = h.y * boardHeight / 100f

                // Concentric circles representing hollow metal holes
                drawCircle(
                    color = Color.Black,
                    radius = 18f,
                    center = Offset(hx, hy)
                )
                drawCircle(
                    color = theme.primaryColor.copy(alpha = 0.4f),
                    radius = 21f,
                    center = Offset(hx, hy),
                    style = Stroke(width = 3f)
                )
            }

            // STEP 4: Draw Screws sitting inside holes
            for (screw in screws) {
                if (screw.holeId.isEmpty()) continue

                val targetHole = holes.find { it.id == screw.holeId } ?: continue
                val hx = targetHole.x * boardWidth / 100f
                val hy = targetHole.y * boardHeight / 100f

                val isSelected = selectedScrewId == screw.id
                val scaleFactor = if (isSelected) pulseSelectorScale else 1.0f

                val screwBaseColor = theme.screwColors[screw.colorId] ?: Color.LightGray

                // Draw Bolt
                drawScrewBoltHead(
                    center = Offset(hx, hy),
                    scale = scaleFactor,
                    baseColor = screwBaseColor,
                    glow = theme.glowEffect,
                    metal = theme.metalTexture,
                    isSelected = isSelected
                )
            }

            // STEP 5: Draw visual Sparks bursting
            for (spark in sparks) {
                val sparkX = spark.startX * boardWidth / 100f
                val sparkY = spark.startY * boardHeight / 100f

                val sparkCol = theme.screwColors[spark.color] ?: Color.White
                val distance = 40f
                val endX = (sparkX + cos(spark.randomAngle) * distance).toFloat()
                val endY = (sparkY + sin(spark.randomAngle) * distance).toFloat()

                drawLine(
                    color = sparkCol.copy(alpha = 0.8f),
                    start = Offset(sparkX, sparkY),
                    end = Offset(endX, endY),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// Draw board grid textures
fun DrawScope.drawBoardBackgroundOrnament(theme: GameTheme) {
    val stepX = size.width / 15f
    val stepY = size.height / 20f

    for (i in 0..15) {
        drawLine(
            color = theme.primaryColor.copy(alpha = 0.05f),
            start = Offset(i * stepX, 0f),
            end = Offset(i * stepX, size.height),
            strokeWidth = 2f
        )
    }
    for (j in 0..20) {
        drawLine(
            color = theme.primaryColor.copy(alpha = 0.05f),
            start = Offset(0f, j * stepY),
            end = Offset(size.width, j * stepY),
            strokeWidth = 2f
        )
    }
}

// Specialized pointer input coordinate parser
suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectTapCoordinates(
    onTap: (Offset) -> Unit
) {
    detectTapGestures(
        onTap = { offset -> onTap(offset) }
    )
}

// Drawing individual structures on Canvas
fun DrawScope.drawIndividualPlateGeo(
    plate: GamePlate,
    holesList: List<GameHole>,
    color: Color,
    boardWidth: Float,
    boardHeight: Float,
    translationY: Float,
    alpha: Float,
    theme: GameTheme
) {
    if (plate.isCircular && plate.attachedHoleIds.isNotEmpty()) {
        val rootHole = holesList.find { it.id == plate.attachedHoleIds[0] } ?: return
        val rx = rootHole.x * boardWidth / 100f
        val ry = rootHole.y * boardHeight / 100f + translationY

        // Shadow disk
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f * alpha),
            radius = plate.circleRadius + 4f,
            center = Offset(rx + 4f, ry + 4f)
        )

        // Main disk
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = plate.circleRadius,
            center = Offset(rx, ry)
        )

        // Specular inner rings
        drawCircle(
            color = Color.White.copy(alpha = 0.15f * alpha),
            radius = plate.circleRadius * 0.7f,
            center = Offset(rx, ry),
            style = Stroke(width = 4f)
        )

        // Bevel border
        drawCircle(
            color = theme.primaryColor.copy(alpha = 0.25f * alpha),
            radius = plate.circleRadius,
            center = Offset(rx, ry),
            style = Stroke(width = 3f)
        )
        return
    }

    // DRAW BAR CAPSULES
    val anchors = plate.attachedHoleIds.mapNotNull { hId ->
        holesList.find { it.id == hId }?.let { h ->
            Offset(
                h.x * boardWidth / 100f,
                h.y * boardHeight / 100f + translationY
            )
        }
    }

    if (anchors.size < 2) return

    val thickness = plate.widthDp * 2.2f

    // Draw backing shadows for depth
    for (i in 0 until anchors.size - 1) {
        drawLine(
            color = Color.Black.copy(alpha = 0.25f * alpha),
            start = anchors[i] + Offset(5f, 6f),
            end = anchors[i + 1] + Offset(5f, 6f),
            strokeWidth = thickness,
            cap = StrokeCap.Round
        )
    }

    // Draw main bars
    for (i in 0 until anchors.size - 1) {
        drawLine(
            color = color.copy(alpha = alpha),
            start = anchors[i],
            end = anchors[i + 1],
            strokeWidth = thickness,
            cap = StrokeCap.Round
        )
    }

    // Draw highlight gloss lines (inner striping)
    for (i in 0 until anchors.size - 1) {
        drawLine(
            color = Color.White.copy(alpha = 0.18f * alpha),
            start = anchors[i],
            end = anchors[i + 1],
            strokeWidth = thickness * 0.3f,
            cap = StrokeCap.Round
        )
    }

    // If Neon theme, draw glowing bounds lines!
    if (theme.glowEffect) {
        for (i in 0 until anchors.size - 1) {
            drawLine(
                color = theme.primaryColor.copy(alpha = 0.5f * alpha),
                start = anchors[i],
                end = anchors[i + 1],
                strokeWidth = thickness * 1.1f,
                cap = StrokeCap.Round
            )
        }
    }
}

// Metallic bevel drawing for screw head bolt overlays
fun DrawScope.drawScrewBoltHead(
    center: Offset,
    scale: Float,
    baseColor: Color,
    glow: Boolean,
    metal: Boolean,
    isSelected: Boolean
) {
    val baseR = 24f * scale

    // Floating drop-shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.45f),
        radius = baseR + 3f,
        center = center + Offset(4f * scale, 5f * scale)
    )

    // Inner Bolt color
    drawCircle(
        color = baseColor,
        radius = baseR,
        center = center
    )

    // Metallic Specular Bevel light
    val shinyGradient = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
        center = center - Offset(baseR * 0.3f, baseR * 0.3f),
        radius = baseR * 1.2f
    )
    drawCircle(
        brush = shinyGradient,
        radius = baseR,
        center = center
    )

    // Screw threaded grooves slot (Cross philips icon)
    val slotW = 14f * scale
    val slotH = 4f * scale

    // Grooves Shadow
    drawRect(
        color = Color.Black.copy(alpha = 0.6f),
        topLeft = Offset(center.x - slotW / 2f, center.y - slotH / 2f) + Offset(1f, 1f),
        size = Size(slotW, slotH)
    )
    drawRect(
        color = Color.Black.copy(alpha = 0.6f),
        topLeft = Offset(center.x - slotH / 2f, center.y - slotW / 2f) + Offset(1f, 1f),
        size = Size(slotH, slotW)
    )

    // Grooves Inner Color
    val darkBevel = baseColor.copy(red = (baseColor.red * 0.3f), green = (baseColor.green * 0.3f), blue = (baseColor.blue * 0.3f))
    drawRect(
        color = darkBevel,
        topLeft = Offset(center.x - slotW / 2f, center.y - slotH / 2f),
        size = Size(slotW, slotH)
    )
    drawRect(
        color = darkBevel,
        topLeft = Offset(center.x - slotH / 2f, center.y - slotW / 2f),
        size = Size(slotH, slotW)
    )

    // Highlight outer edge
    drawCircle(
        color = Color.White.copy(alpha = 0.5f),
        radius = baseR,
        center = center,
        style = Stroke(width = 2f)
    )

    if (isSelected) {
        // Selection pulsating outer glow board
        drawCircle(
            color = Color(0xFFFFD700),
            radius = baseR + 5f,
            center = center,
            style = Stroke(width = 3f)
        )
    }
}

// -------------------------------------------------------------
// COMPONENT: GAME CONTROLS / BOOSTER TOOLBAR CARD
// -------------------------------------------------------------
@Composable
fun GameBoosterToolbar(
    hammerCount: Int,
    extraHoleCount: Int,
    undoCount: Int,
    onHammerClick: () -> Unit,
    onExtraHoleClick: () -> Unit,
    onUndoClick: () -> Unit,
    onOpenShopQuick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1414)),
        border = BorderStroke(1.dp, Color(0xFF331E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BOOSTER 1: HAMMER
            BoosterItemButton(
                icon = Icons.Default.Handyman,
                label = "Hammer",
                stockCount = hammerCount,
                onClick = onHammerClick
            )

            // BOOSTER 2: EXTRA HOLE
            BoosterItemButton(
                icon = Icons.Default.AddCircle,
                label = "Extra Hole",
                stockCount = extraHoleCount,
                onClick = onExtraHoleClick
            )

            // BOOSTER 3: UNDO
            BoosterItemButton(
                icon = Icons.Default.Undo,
                label = "Undo",
                stockCount = undoCount,
                onClick = onUndoClick
            )

            // QUICK SHOP BUTTON
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onOpenShopQuick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE67E22)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Buy Boosters",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Refill",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BoosterItemButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    stockCount: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(45.dp)
        ) {
            // Round button base
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF322323)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Badge with count
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE74C3C))
                    .border(1.dp, Color.White, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stockCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------
// COMPONENT: WIN / LOSS IN-GAME DIALOGS (Highly Interactive)
// -------------------------------------------------------------

@Composable
fun LevelWinOverlayDialog(
    levelId: Int,
    stars: Int,
    coinsWon: Int,
    onNextLevel: () -> Unit,
    onBack: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3B2B)), // Green background for victory
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(3.dp, Color(0xFF2ECC71))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VICTORY!",
                    color = Color(0xFF2ECC71),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Level $levelId Completed Successfully",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                // High score stars animations
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    repeat(3) { idx ->
                        val isLit = idx < stars
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isLit) Color(0xFFFFD700) else Color.DarkGray,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }

                // Balance gains
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D2518))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "+$coinsWon COINS REWARDED",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("PLAY NEXT LEVEL", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("BACK TO MAIN MENU", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelDefeatOverlayDialog(
    levelId: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF421D1D)), // Dark red background for loss
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(3.dp, Color(0xFFE74C3C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GAME OVER",
                    color = Color(0xFFE74C3C),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Out of Moves or Time Expired!",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color(0xFFE74C3C),
                    modifier = Modifier
                        .size(70.dp)
                        .padding(bottom = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("TRY AGAIN", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("BACK TO MAIN MENU", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: QUICK BOOSTER PURCHASE SHOP DIALOG
// -------------------------------------------------------------
@Composable
fun QuickBoosterShopDialog(
    coins: Int,
    onClose: () -> Unit,
    onBuyBooster: (String, Int) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261818)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFFE67E22))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REFILL AMMO",
                        color = Color(0xFFE67E22),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                // Balance coins Display
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF160E0E))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700))
                    Text(text = "MONETARY WALLET: $coins", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Booster rows
                BoosterShopItemRow(
                    name = "Sledge Hammer",
                    desc = "Bypasses trays to crush 1 blocking bolt",
                    cost = 50,
                    coins = coins,
                    icon = Icons.Default.Handyman,
                    onPurchase = { onBuyBooster("hammer", 50) }
                )

                BoosterShopItemRow(
                    name = "Layout Safe",
                    desc = "Appends +1 layout empty screw hole slot",
                    cost = 75,
                    coins = coins,
                    icon = Icons.Default.AddCircle,
                    onPurchase = { onBuyBooster("extra_hole", 75) }
                )

                BoosterShopItemRow(
                    name = "Undo Chrono",
                    desc = "Reverts board layout 1 action step backward",
                    cost = 40,
                    coins = coins,
                    icon = Icons.Default.Undo,
                    onPurchase = { onBuyBooster("undo", 40) }
                )
            }
        }
    }
}

@Composable
fun BoosterShopItemRow(
    name: String,
    desc: String,
    cost: Int,
    coins: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onPurchase: () -> Unit
) {
    val canAfford = coins >= cost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0F0F)),
        border = BorderStroke(1.dp, Color(0xFF2C1919))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF332020)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Column {
                    Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = desc, color = Color.Gray, fontSize = 10.sp, maxLines = 1)
                }
            }

            Button(
                onClick = onPurchase,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE67E22),
                    disabledContainerColor = Color(0xFF382F2C)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAfford) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = cost.toString(),
                        color = if (canAfford) Color.White else Color.Gray,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN: DEDICATED THEMES SECTOR SHOP (Store View)
// -------------------------------------------------------------
@Composable
fun MainThemeShopScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val unlockedThemeIds by viewModel.unlockedThemeIds.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E0F1C), Color(0xFF0F040E)) // Magenta dark cyber theme
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Text(
                    text = "COSMETIC BOUTIQUE",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Text(text = coins.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PERSONALIZE BOARD OVERLAYS AND BOLT THREADS",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(15.dp))

            // List of themes (Presets)
            val allThemesList = GameThemePresets.list

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                allThemesList.forEach { themeItem ->
                    val isUnlocked = unlockedThemeIds.contains(themeItem.id)
                    val isActive = activeTheme.id == themeItem.id

                    ThemeStoreItemCard(
                        themeItem = themeItem,
                        isActive = isActive,
                        isUnlocked = isUnlocked,
                        walletCoins = coins,
                        onAction = {
                            viewModel.buyOrSelectTheme(themeItem.id, themeItem.cost)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeStoreItemCard(
    themeItem: GameTheme,
    isActive: Boolean,
    isUnlocked: Boolean,
    walletCoins: Int,
    onAction: () -> Unit
) {
    val canAfford = walletCoins >= themeItem.cost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261526)),
        border = BorderStroke(
            if (isActive) 2.dp else 1.dp,
            if (isActive) themeItem.primaryColor else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1.0f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Miniature preview visual dot block
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.verticalGradient(themeItem.boardBgGradient)
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Miniature screw rendering representation
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(themeItem.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.White.copy(alpha = 0.5f)))
                    }
                }

                // Metadata Details
                Column {
                    Text(
                        text = themeItem.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = when (themeItem.id) {
                            "wood" -> "Golden rustic forest timbers texture"
                            "neon" -> "Radiating synthetic fluorescent lasers glow"
                            "candy" -> "Velvet sweet marshmallow glaze overlay"
                            else -> "Brushed chrome carbon industrial steel plates"
                        },
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            // CTA Action Button Row
            Button(
                onClick = onAction,
                enabled = isActive || isUnlocked || canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isActive -> Color(0xFF2ECC71) // Green selected
                        isUnlocked -> Color(0xFF34495E) // Gray Selectable
                        else -> Color(0xFFDA70D6) // Buyable Magenta
                    },
                    disabledContainerColor = Color(0xFF201320)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                when {
                    isActive -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("ACTIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    isUnlocked -> {
                        Text("SELECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    else -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                            Text(themeItem.cost.toString(), fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
