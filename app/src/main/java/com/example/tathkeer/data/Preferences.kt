package com.example.tathkeer.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("tathkeer_prefs")

class Preferences(private val context: Context) {

    companion object {
        val SECTION_KEY = stringPreferencesKey("current_section")  // "morning" or "evening"
        val INDEX_KEY = intPreferencesKey("current_index")
        val MORNING_DONE = stringPreferencesKey("morning_done")  // سنخزنها كـ JSON string
        val EVENING_DONE = stringPreferencesKey("evening_done")
    }

    suspend fun saveSection(section: String) {
        context.dataStore.edit { prefs ->
            prefs[SECTION_KEY] = section
        }
    }

    val sectionFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SECTION_KEY] ?: "morning" }

    suspend fun saveIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[INDEX_KEY] = index
        }
    }

    val indexFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[INDEX_KEY] ?: 0 }

    // حفظ حالة التقدم (تحويل list إلى JSON)
    suspend fun saveMorningDone(list: List<Zikr>) {
        val json = list.joinToString(";") { "${it.done}" }
        context.dataStore.edit { prefs ->
            prefs[MORNING_DONE] = json
        }
    }

    fun morningDoneFlow(): Flow<List<Int>> = context.dataStore.data
        .map { prefs ->
            val str = prefs[MORNING_DONE] ?: ""
            if (str.isBlank()) List(AdhkarProvider.morningAdhkar.size) { 0 }
            else str.split(";").map { it.toIntOrNull() ?: 0 }
        }

    suspend fun saveEveningDone(list: List<Zikr>) {
        val json = list.joinToString(";") { "${it.done}" }
        context.dataStore.edit { prefs ->
            prefs[EVENING_DONE] = json
        }
    }

    fun eveningDoneFlow(): Flow<List<Int>> = context.dataStore.data
        .map { prefs ->
            val str = prefs[EVENING_DONE] ?: ""
            if (str.isBlank()) List(AdhkarProvider.eveningAdhkar.size) { 0 }
            else str.split(";").map { it.toIntOrNull() ?: 0 }
        }
}
