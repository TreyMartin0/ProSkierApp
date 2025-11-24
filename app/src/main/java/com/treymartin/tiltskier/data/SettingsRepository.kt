package com.treymartin.tiltskier.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.treymartin.tiltskier.data.datastore.PrefKeys
import com.treymartin.tiltskier.data.datastore.appDataStore
import com.treymartin.tiltskier.settings.SettingsUiState
import kotlinx.coroutines.flow.first

class SettingsRepository(private val context: Context) {

    suspend fun read(): SettingsUiState {
        val prefs = context.appDataStore.data.first()
        return SettingsUiState(
            sensitivity = prefs[PrefKeys.SENSITIVITY] ?: 1.0f,
            soundOn = prefs[PrefKeys.SOUND_ON] ?: true,
            hapticsOn = prefs[PrefKeys.HAPTICS_ON] ?: true,
        )
    }

    suspend fun update(block: (SettingsUiState) -> SettingsUiState) {
        context.appDataStore.edit { prefs ->
            val current = read()
            val updated = block(current)
            prefs[PrefKeys.SENSITIVITY] = updated.sensitivity
            prefs[PrefKeys.SOUND_ON] = updated.soundOn
            prefs[PrefKeys.HAPTICS_ON] = updated.hapticsOn
        }
    }
}
