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

    var playCrashSound by mutableStateOf(false)

    private var distanceSinceLastSpawn = 0f
    private var lastSpawnType: ObstacleType? = null

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
        if (ui.runState != RunState.RUNNING) return
        val delta = -ax * ui.sensitivity * 0.01f
        val newX = (ui.skierX + delta).coerceIn(0f, 1f)

        // Decide pose based on how strong the movement is
        val threshold = 0.0085f

        val newPose = when {
            delta > threshold  -> SkierPos.RIGHT
            delta < -threshold -> SkierPos.LEFT
            else -> SkierPos.STRAIGHT
        }

        ui = ui.copy(
            skierX = newX,
            skierPos = newPose
        )
    }

    private fun nextObstacleType(): ObstacleType {
        val last = lastSpawnType
        val type = if (last == null) {
            // first one truly random
            if (rng.nextBoolean()) ObstacleType.TREE else ObstacleType.ROCK
        } else {
            // 70% chance to flip, 30% chance to repeat
            if (rng.nextFloat() < 0.7f) {
                if (last == ObstacleType.TREE) ObstacleType.ROCK else ObstacleType.TREE
            } else {
                last
            }
        }
        lastSpawnType = type
        return type
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

        // accumulate distance traveled this frame
        val distanceThisFrame = speed * dtSec
        distanceSinceLastSpawn += distanceThisFrame

        val spawnSpacing = 0.4f

        // spawn based on distance, not frame probability
        while (distanceSinceLastSpawn >= spawnSpacing) {
            distanceSinceLastSpawn -= spawnSpacing

            // base X for this "row" of obstacles
            val baseX = rng.nextFloat()

            // 25% chance to spawn a cluster of 2,3,4 side-by-side
            val randomNumber = (2..4).random()
            val clusterSize = if (rng.nextFloat() < 0.25f) randomNumber else 1

            for (i in 0 until clusterSize) {
                val type = nextObstacleType()

                val radius = when (type) {
                    ObstacleType.TREE -> 0.06f   // bigger
                    ObstacleType.ROCK -> 0.03f   // smaller
                }

                // spread them horizontally around baseX
                val offset = (i - (clusterSize - 1) / 2f) * 0.18f
                val x = (baseX + offset).coerceIn(0.1f, 0.9f)

                val new = Obstacle(
                    id = rng.nextLong(),
                    x = x,
                    y = 1f + radius,
                    radius = radius,
                    type = type
                )
                obstacles = obstacles + new
            }
        }

        //Collision check
        val skierX = ui.skierX
        val skierY = ui.skierY
        val skierRadius = 0.015f

        val collided = obstacles.any { o ->
            val dx = o.x - skierX
            val dy = o.y - skierY
            hypot(dx, dy) < o.radius + skierRadius
        }

        // Score: +1 per obstacle successfully passed
        val newScore = ui. score + passedCount
        val newSpeed = (speed + dtSec * 0.01f).coerceAtMost(3.0f)

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
        playCrashSound = true
        ui = ui.copy(runState = RunState.GAME_OVER)
        // record score
        viewModelScope.launch {
            scoresRepo.record(ui.score)
            val best = scoresRepo.best()
            ui = ui.copy(bestScore = best)
        }
    }
}