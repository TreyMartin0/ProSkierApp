package com.treymartin.tiltskier.scores

data class ScoreEntry(val value: Int, val atEpochMillis: Long)

data class ScoresUiState(
    val best: Int = 0,
    val top5: List<ScoreEntry> = emptyList()
)