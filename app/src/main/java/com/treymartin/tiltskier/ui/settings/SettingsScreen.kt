package com.treymartin.tiltskier.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
