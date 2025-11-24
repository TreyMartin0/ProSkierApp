package com.treymartin.tiltskier.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.treymartin.tiltskier.game.GameUiState
import kotlin.math.min

@Composable
fun GameCanvas(ui: GameUiState, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .background(Color(0xFF004D40)) // dark green slope
    ) {
        val w = size.width
        val h = size.height

        fun nx(x: Float) = x * w
        fun ny(y: Float) = h - y * h   // y=0 bottom, y=1 top

        // ---- Skier ----
        val skierRadiusWorld = 0.035f
        val skierRadiusPx = skierRadiusWorld * h

        drawCircle(
            color = Color.Red,
            center = Offset(nx(ui.skierX), ny(ui.skierY)),
            radius = skierRadiusPx
        )

        // ---- Obstacles ----
        ui.obstacles.forEach { o ->
            val rPx = o.radius * h    // radius scaled by height
            drawRect(
                color = Color.Yellow,
                topLeft = Offset(nx(o.x) - rPx, ny(o.y) - rPx),
                size = Size(rPx * 2, rPx * 2)
            )
        }
    }
}