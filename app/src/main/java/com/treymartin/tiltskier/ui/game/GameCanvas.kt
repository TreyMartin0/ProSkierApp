package com.treymartin.tiltskier.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.treymartin.tiltskier.game.GameUiState
import com.treymartin.tiltskier.R
import com.treymartin.tiltskier.game.ObstacleType
import com.treymartin.tiltskier.game.SkierPos
import kotlin.math.min

@Composable
fun GameCanvas(ui: GameUiState, modifier: Modifier = Modifier) {
    val skierCenterBitmap: ImageBitmap = ImageBitmap.imageResource(R.drawable.skier1)
    val skierLeftBitmap: ImageBitmap     = ImageBitmap.imageResource(R.drawable.skier3)
    val skierRightBitmap: ImageBitmap    = ImageBitmap.imageResource(R.drawable.skier2)
    val treeBitmap = ImageBitmap.imageResource(R.drawable.tree)
    val rockBitmap = ImageBitmap.imageResource(R.drawable.rock)
    Canvas(
        modifier = modifier
            .background(Color(0xFFFFFFFF)) // dark green slope
    ) {
        val w = size.width
        val h = size.height

        fun nx(x: Float) = x * w
        fun ny(y: Float) = h - y * h   // y=0 bottom, y=1 top

        val skierBitmap = when (ui.skierPos) {
            SkierPos.STRAIGHT -> skierCenterBitmap
            SkierPos.LEFT     -> skierLeftBitmap
            SkierPos.RIGHT    -> skierRightBitmap
            else -> {skierCenterBitmap}
        }

        // Skier
        val skierRadiusWorld = 0.035f
        val skierSizePx = (skierRadiusWorld * h * 2f).toInt()

        val center = Offset(nx(ui.skierX), ny(ui.skierY))
        val topLeft = IntOffset(
            (center.x - skierSizePx / 2f).toInt(),
            (center.y - skierSizePx / 2f).toInt()
        )

        drawImage(
            image = skierBitmap,
            dstOffset = topLeft,
            dstSize = IntSize(skierSizePx, skierSizePx)
        )

        // Obstacles
        ui.obstacles.sortedByDescending { it.y }.forEach { o ->
            val sizePx = (o.radius * h * 2).toInt()
            val center = Offset(nx(o.x), ny(o.y))
            val topLeft = IntOffset(
                (center.x - sizePx / 2).toInt(),
                (center.y - sizePx / 2).toInt()
            )

            val bitmap = when (o.type) {
                ObstacleType.TREE -> treeBitmap
                ObstacleType.ROCK -> rockBitmap
            }

            drawImage(
                image = bitmap,
                dstOffset = topLeft,
                dstSize = IntSize(sizePx, sizePx)
            )
        }
    }
}