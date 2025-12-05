package com.treymartin.tiltskier.ui.howto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Objective",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Stay alive as long as possible while dodging trees and rocks. " +
                                "Your score increases as you survive and pass obstacles."
                    )

                    Divider()

                    Text(
                        text = "Controls",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• Tilt left and right to steer the skier.")
                    Text("• Sensitivity can be tuned in Settings.")

                    Divider()

                    Text(
                        text = "Obstacles",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• Trees are taller and harder to dodge.")
                    Text("• Rocks are smaller but can appear in clusters.")
                    Text("• A single collision ends the run with a big OUCH!")

                    Divider()

                    Text(
                        text = "Tips",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• Don’t over-steer — small tilts work best at high speeds.")
                    Text("• Watch for clusters of obstacles and pick a safe line early.")
                    Text("• Try different sensitivities to match how you hold your phone.")
                }
            }
        }
    }
}
