package com.treymartin.tiltskier.ui.game


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treymartin.tiltskier.data.SettingsRepository
import com.treymartin.tiltskier.data.ScoresRepository
import com.treymartin.tiltskier.game.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

class GameViewModel(
    private val settingsRepo: SettingsRepository,
    private val scoresRepo: ScoresRepository
) : ViewModel() {

    private val rng = Random(System.currentTimeMillis())

    var ui by mutableStateOf(GameUiState())
        private set

    init {
        // load best score once
        viewModelScope.launch {
            val best = scoresRepo.best()
            ui = ui.copy(bestScore = best)
        }

        // react to settings changes in real time
        viewModelScope.launch {
            settingsRepo.settingsFlow.collectLatest { settings ->
                ui = ui.copy(
                    sensitivity = settings.sensitivity,
                    soundOn = settings.soundOn,
                    hapticsOn = settings.hapticsOn
                )
            }
        }
    }

    // ax from accelerometer (tilt left/right), ay unused or for future
    fun onTilt(ax: Float, ay: Float) {
        val delta = -ax * ui.sensitivity * 0.01f
        val newX = (ui.skierX + delta).coerceIn(0f, 1f)
        ui = ui.copy(skierX = newX)
    }

    fun tick(dtMillis: Long) {
        if (ui.runState != RunState.RUNNING) return

        val dtSec = dtMillis / 1000f
        val speed = ui.worldSpeed

        // Move obstacles downward
        val moved = ui.obstacles.map { it.copy(y = it.y - speed * dtSec) }

        // Keep obstacles while ANY part is still on the green:
        // y + radius > 0  => top edge above bottom line
        val remaining = moved.filter { it.y + it.radius > 0f }

        // Obstacles that just fully passed off the bottom:
        val passedCount = moved.count { it.y + it.radius <= 0f }

        var obstacles = remaining

        // Spawn new obstacles near the very top edge
        if (rng.nextFloat() < 0.03f) {  // spawn probability per frame
            val radius = 0.04f          // tweak size if you want
            val new = Obstacle(
                id = rng.nextLong(),
                x = rng.nextFloat(),         // 0..1 across width
                y = 1f + radius,             // starts just above top
                radius = radius,
                type = if (rng.nextBoolean()) ObstacleType.TREE else ObstacleType.ROCK
            )
            obstacles = obstacles + new
        }

        //Collision check
        val skierX = ui.skierX
        val skierY = ui.skierY
        val skierRadius = 0.035f

        val collided = obstacles.any { o ->
            val dx = o.x - skierX
            val dy = o.y - skierY
            hypot(dx, dy) < o.radius + skierRadius
        }

        // Score: +1 per obstacle successfully passed
        val newScore = ui.score + passedCount
        val newSpeed = (speed + dtSec * 0.02f).coerceAtMost(3.0f)

        if (collided) {
            crash()
        } else {
            ui = ui.copy(
                obstacles = obstacles,
                score = newScore,
                worldSpeed = newSpeed
            )
        }
    }

    fun startRun() {
        ui = ui.copy(
            runState = RunState.RUNNING,
            score = 0,
            worldSpeed = 0.4f,
            obstacles = emptyList()
        )
    }

    fun pause() {
        ui = ui.copy(runState = RunState.PAUSED)
    }

    fun resume() {
        ui = ui.copy(runState = RunState.RUNNING)
    }

    fun restart() {
        startRun()
    }

    fun crash() {
        ui = ui.copy(runState = RunState.GAME_OVER)
        // record score
        viewModelScope.launch {
            scoresRepo.record(ui.score)
            val best = scoresRepo.best()
            ui = ui.copy(bestScore = best)
        }
        // TODO: play sound / haptics
    }
}