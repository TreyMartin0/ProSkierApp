package com.treymartin.tiltskier.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.imageResource
import com.treymartin.tiltskier.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    val pauseBitmap = ImageBitmap.imageResource(R.drawable.pause)
    val ui = viewModel.ui
    val lastTime = remember { mutableStateOf(System.currentTimeMillis()) }

    // Prevents screen from going inactive
    KeepScreenOn(ui.runState)

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
                    IconButton(onClick = { viewModel.pause() }) {
                        Icon(painter = BitmapPainter(pauseBitmap), "Pause")
                    }
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
                Dialog(
                    onDismissRequest = { /* don’t dismiss by tapping outside */ }
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Game Over",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Score: ${ui.score}   Best: ${ui.bestScore}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                            )

                            Button(
                                onClick = { viewModel.restart() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Play Again")
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onExit() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Return Home")
                            }
                        }
                    }
                }
            }
            if (ui.runState == RunState.PAUSED) {
                Dialog(onDismissRequest = { }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Paused",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.resume() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Resume")
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = onExit,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Return Home")
                            }
                        }
                    }
                }
            }
        }
    }
}
