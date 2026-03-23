package com.syntax_institut.whatssyntax.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ACCESS_TOKEN  = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID       = stringPreferencesKey("user_id")
        private val KEY_PHONE         = stringPreferencesKey("phone")
    }

    val accessToken: Flow<String?> = context.dataStore.data
        .map { it[KEY_ACCESS_TOKEN] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_REFRESH_TOKEN] != null }

    suspend fun saveTokens(accessToken: String, refreshToken: String, userId: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN]  = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID]       = userId
            prefs[KEY_PHONE]         = phone
        }
    }

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }
            .let { flow ->
                var result: String? = null
                flow.collect { result = it; return@collect }
                result
            }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
