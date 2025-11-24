package com.treymartin.tiltskier.ui.scores

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treymartin.tiltskier.data.ScoresRepository
import com.treymartin.tiltskier.scores.ScoreEntry
import com.treymartin.tiltskier.scores.ScoresUiState
import kotlinx.coroutines.launch

class ScoresViewModel(
    private val repo: ScoresRepository
) : ViewModel() {

    var ui by mutableStateOf(ScoresUiState())
        private set

    // Call this whenever the Scores screen is opened
    fun refresh() {
        viewModelScope.launch {
            val top = repo.top5()
            val best = repo.best()
            ui = ScoresUiState(best = best, top5 = top)
        }
    }
}
