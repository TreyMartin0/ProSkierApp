package com.treymartin.tiltskier.ui.game


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treymartin.tiltskier.data.SettingsRepository
import com.treymartin.tiltskier.data.ScoresRepository
import com.treymartin.tiltskier.game.*
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

class GameViewModel(
    private val settingsRepo: SettingsRepository,
    private val scoresRepo: ScoresRepository
) : ViewModel() {

    var ui by mutableStateOf(GameUiState())
        private set

    init {
        // Load settings and best score when VM starts
        viewModelScope.launch {
            val settings = settingsRepo.read()
            val best = scoresRepo.best()
            ui = ui.copy(
                sensitivity = settings.sensitivity,
                soundOn = settings.soundOn,
                hapticsOn = settings.hapticsOn,
                bestScore = best
            )
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
        var obstacles = ui.obstacles.map { it.copy(y = it.y - speed * dtSec) }
            .filter { it.y + it.radius > 0f } // keep only on-screen

        // Spawn new obstacles probabilistically
        val rand = Random(ui.rngSeed)
        var seed = ui.rngSeed
        if (rand.nextFloat() < 0.05f) {
            seed = rand.nextLong()
            val new = Obstacle(
                id = seed,
                x = rand.nextFloat(),  // 0..1
                y = 1.5f,
                radius = 0.05f,
                type = if (rand.nextBoolean()) ObstacleType.TREE else ObstacleType.ROCK
            )
            obstacles = obstacles + new
        }

        // Collision check
        val skierX = ui.skierX
        val skierY = ui.skierY
        val collided = obstacles.any {
            val dx = it.x - skierX
            val dy = it.y - skierY
            hypot(dx, dy) < it.radius + 0.04f
        }

        val newScore = ui.score + (speed * dtSec * 10).toInt()
        val newSpeed = (speed + dtSec * 0.05f).coerceAtMost(4.0f)

        if (collided) {
            crash()
        } else {
            ui = ui.copy(
                obstacles = obstacles,
                score = newScore,
                worldSpeed = newSpeed,
                rngSeed = seed
            )
        }
    }

    fun startRun() {
        ui = ui.copy(
            runState = RunState.RUNNING,
            score = 0,
            worldSpeed = 1.0f,
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