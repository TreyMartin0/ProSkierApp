package com.treymartin.tiltskier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.treymartin.tiltskier.data.ScoresRepository
import com.treymartin.tiltskier.data.SettingsRepository
import com.treymartin.tiltskier.ui.game.GameViewModel
import com.treymartin.tiltskier.ui.scores.ScoresViewModel
import com.treymartin.tiltskier.ui.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // repositories
        val settingsRepo = SettingsRepository(this)
        val scoresRepo = ScoresRepository(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                // Manual ViewModel creation for now
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
                        ScoresScreen(scoresVm) { navController.popBackStack() }
                    }
                    composable("howto") {
                        HowToScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onStartRun: () -> Unit,
    onTopScores: () -> Unit,
    onHowTo: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tilt Skier") },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onStartRun, modifier = Modifier.fillMaxWidth()) {
                Text("Start Run")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onTopScores, modifier = Modifier.fillMaxWidth()) {
                Text("Top Scores")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onHowTo, modifier = Modifier.fillMaxWidth()) {
                Text("How To Play")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val ui = viewModel.ui

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Sensitivity")
            Slider(
                value = ui.sensitivity,
                onValueChange = { viewModel.setSensitivity(it) },
                valueRange = 0.5f..2.0f
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sound")
                Spacer(Modifier.weight(1f))
                Switch(ui.soundOn, onCheckedChange = { viewModel.setSound(it) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Haptics")
                Spacer(Modifier.weight(1f))
                Switch(ui.hapticsOn, onCheckedChange = { viewModel.setHaptics(it) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresScreen(
    viewModel: ScoresViewModel,
    onBack: () -> Unit
) {
    val ui = viewModel.ui

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Scores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Best: ${ui.best}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(ui.top5) { index, entry ->
                    Text("${index + 1}. Score: ${entry.value}")
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How To Play") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Movement", style = MaterialTheme.typography.titleMedium)
            Text("Tilt your phone left and right to steer the skier.")
            Spacer(Modifier.height(12.dp))
            Text("Obstacles", style = MaterialTheme.typography.titleMedium)
            Text("Avoid trees and rocks. A collision ends the run.")
            Spacer(Modifier.height(12.dp))
            Text("Goal", style = MaterialTheme.typography.titleMedium)
            Text("Stay alive as long as possible and beat your top score!")
        }
    }
}