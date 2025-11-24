package com.treymartin.tiltskier.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.appDataStore by preferencesDataStore(name = "tilt_skier")

object PrefKeys {
    val SENSITIVITY = floatPreferencesKey("sensitivity")
    val SOUND_ON = booleanPreferencesKey("soundOn")
    val HAPTICS_ON = booleanPreferencesKey("hapticsOn")
    val TILT_BIAS_X = floatPreferencesKey("tiltBiasX")
    val BEST_SCORE = intPreferencesKey("bestScore")
    val TOP_SCORES_JSON = stringPreferencesKey("topScoresJson")
}