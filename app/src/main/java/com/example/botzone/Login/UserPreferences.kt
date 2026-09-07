package com.example.botzone.Login


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//ذخیره Token در DataStore (حافظه محلی)

class UserPreferences(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("user_prefs")

    companion object {
        val KEY_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")
        val KEY_DEVICE = stringPreferencesKey("device_id")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val username: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_LOGGED_IN] ?: false }
    val deviceId: Flow<String?> = context.dataStore.data.map { it[KEY_DEVICE] }

    suspend fun saveLogin(token: String, username: String, deviceId: String) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_USERNAME] = username
            it[KEY_DEVICE] = deviceId
            it[KEY_LOGGED_IN] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
