package com.treymartin.tiltskier.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treymartin.tiltskier.game.GameUiState
import com.treymartin.tiltskier.game.RunState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExit: () -> Unit,
    onSettings: () -> Unit
) {
    val ui = viewModel.ui
    val lastTime = remember { mutableStateOf(System.currentTimeMillis()) }

    // Tilt
    TiltListener { ax, ay -> viewModel.onTilt(ax, ay) }

    // Game loop: calls tick(dt) over and over
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val dt = now - lastTime.value
            lastTime.value = now
            viewModel.tick(dt)
            delay(16L) // ~60 fps
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Score: ${ui.score}") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit")
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GameCanvas(ui = ui, modifier = Modifier.fillMaxSize())

            if (ui.runState == RunState.GAME_OVER) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("DEBUG: ${ui.skierX}, ${ui.skierY}")
                    Text("Game Over")
                    Text("Score: ${ui.score}  Best: ${ui.bestScore}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.restart() }) {
                        Text("Play Again")
                    }
                }
            }
        }
    }
}
