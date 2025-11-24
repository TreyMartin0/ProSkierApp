package com.treymartin.tiltskier.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.treymartin.tiltskier.data.datastore.PrefKeys
import com.treymartin.tiltskier.data.datastore.appDataStore
import com.treymartin.tiltskier.scores.ScoreEntry
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.util.*

class ScoresRepository(private val context: Context) {

    private val gson = Gson()

    private suspend fun readEntries(): List<ScoreEntry> {
        val prefs = context.appDataStore.data.first()
        val json = prefs[PrefKeys.TOP_SCORES_JSON] ?: "[]"
        val arr = gson.fromJson(json, Array<ScoreEntry>::class.java) ?: emptyArray()
        return arr.toList()
    }

    suspend fun best(): Int = readEntries().maxOfOrNull { it.value } ?: 0

    suspend fun top5(): List<ScoreEntry> = readEntries()
        .sortedByDescending { it.value }
        .take(5)

    suspend fun record(score: Int) {
        context.appDataStore.edit { prefs ->
            val updated = (readEntries() + ScoreEntry(score, Date().time))
                .sortedByDescending { it.value }
                .take(10)

            prefs[PrefKeys.BEST_SCORE] = updated.maxOfOrNull { it.value } ?: 0
            prefs[PrefKeys.TOP_SCORES_JSON] = gson.toJson(updated)
        }
    }

    suspend fun clearBest() {
        context.appDataStore.edit { prefs ->
            prefs[PrefKeys.BEST_SCORE] = 0
            prefs[PrefKeys.TOP_SCORES_JSON] = "[]"
        }
    }
}
