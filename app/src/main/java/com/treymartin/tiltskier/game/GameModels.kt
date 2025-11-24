package com.treymartin.tiltskier.game

data class Obstacle(
    val id: Long,
    val x: Float,      // world X
    val y: Float,      // world Y
    val radius: Float,
    val type: ObstacleType
)

enum class RunState { READY, RUNNING, PAUSED, GAME_OVER }

enum class ObstacleType { TREE, ROCK }

enum class SkierPos { STRAIGHT, LEFT, RIGHT }

data class GameUiState(
    val runState: RunState = RunState.READY,
    val score: Int = 0,
    val bestScore: Int = 0,
    val skierX: Float = 0.5f,
    val skierY: Float = 0.2f,
    val skierRadius: Float = 0.05f,
    val worldSpeed: Float = 0.4f,
    val sensitivity: Float = 1.0f,
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
    val obstacles: List<Obstacle> = emptyList(),
    val rngSeed: Long = System.currentTimeMillis(),
    val skierPos: SkierPos = SkierPos.STRAIGHT
)
