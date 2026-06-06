package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.ui.MainMenuScreen
import com.example.ui.GamePlayScreen
import com.example.ui.MainThemeShopScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val gameViewModel: GameViewModel = viewModel()
                    var currentScreen by remember { mutableStateOf("MENU") } // "MENU", "PLAY", "SHOP"

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState == "PLAY" || targetState == "SHOP") {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            "MENU" -> {
                                MainMenuScreen(
                                    viewModel = gameViewModel,
                                    onLevelSelected = { levelId ->
                                        gameViewModel.startLevel(levelId)
                                        currentScreen = "PLAY"
                                    },
                                    onOpenShop = {
                                        currentScreen = "SHOP"
                                    }
                                )
                            }
                            "PLAY" -> {
                                GamePlayScreen(
                                    viewModel = gameViewModel,
                                    onBackToMenu = {
                                        currentScreen = "MENU"
                                    }
                                )
                            }
                            "SHOP" -> {
                                MainThemeShopScreen(
                                    viewModel = gameViewModel,
                                    onBack = {
                                        currentScreen = "MENU"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
