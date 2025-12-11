package com.example.botzone.Login


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


val Context.userDataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveUser(username: String, password: String) {
        context.userDataStore.edit { prefs ->
            prefs[USERNAME] = username
            prefs[PASSWORD] = password

        }
    }

    suspend fun getUser(): Pair<String?, String?> {
        val prefs = context.userDataStore.data.first()
        Log.e("USERNAME", prefs[USERNAME].toString())
        Log.e("PASSWORD", prefs[PASSWORD].toString())
        return prefs[USERNAME] to prefs[PASSWORD]
    }

    suspend fun isLoggedIn(): Boolean {
        val prefs = context.userDataStore.data.first()
        return prefs[IS_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.userDataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = value
        }
    }

    suspend fun clearUser() {
        context.userDataStore.edit { it.clear() }
    }
}