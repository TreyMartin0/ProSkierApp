package com.treymartin.tiltskier.settings


data class SettingsUiState(
    val sensitivity: Float = 1.0f, // 0.5..2.0
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
)