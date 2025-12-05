package com.treymartin.tiltskier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.treymartin.tiltskier.data.ScoresRepository
import com.treymartin.tiltskier.data.SettingsRepository
import com.treymartin.tiltskier.ui.game.GameScreen
import com.treymartin.tiltskier.ui.game.GameViewModel
import com.treymartin.tiltskier.ui.menu.MenuScreen
import com.treymartin.tiltskier.ui.scores.ScoresScreen
import com.treymartin.tiltskier.ui.scores.ScoresViewModel
import com.treymartin.tiltskier.ui.settings.SettingsScreen
import com.treymartin.tiltskier.ui.settings.SettingsViewModel
import com.treymartin.tiltskier.ui.howto.HowToScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepo = SettingsRepository(this)
        val scoresRepo = ScoresRepository(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                val gameVm = remember { GameViewModel(settingsRepo, scoresRepo) }
                val settingsVm = remember { SettingsViewModel(settingsRepo) }
                val scoresVm = remember { ScoresViewModel(scoresRepo) }

                NavHost(
                    navController = navController,
                    startDestination = "menu"
                ) {
                    composable("menu") {
                        MenuScreen(
                            onStartRun = {
                                gameVm.startRun()
                                navController.navigate("game")
                            },
                            onTopScores = { navController.navigate("scores") },
                            onHowTo = { navController.navigate("howto") },
                            onSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("game") {
                        GameScreen(
                            viewModel = gameVm,
                            onExit = { navController.popBackStack() },
                            onSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsVm,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("scores") {
                        ScoresScreen(
                            viewModel = scoresVm,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("howto") {
                        HowToScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
