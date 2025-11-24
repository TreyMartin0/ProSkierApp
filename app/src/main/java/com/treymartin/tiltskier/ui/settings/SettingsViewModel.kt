package com.treymartin.tiltskier.ui.settings

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treymartin.tiltskier.data.SettingsRepository
import com.treymartin.tiltskier.settings.SettingsUiState
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: SettingsRepository
) : ViewModel() {

    var ui by mutableStateOf(SettingsUiState())
        private set

    init {
        viewModelScope.launch {
            ui = repo.read()
        }
    }

    fun setSensitivity(v: Float) {
        ui = ui.copy(sensitivity = v)
        persist()
    }

    fun setSound(on: Boolean) {
        ui = ui.copy(soundOn = on)
        persist()
    }

    fun setHaptics(on: Boolean) {
        ui = ui.copy(hapticsOn = on)
        persist()
    }

    private fun persist() {
        viewModelScope.launch {
            repo.update { ui }
        }
    }
}