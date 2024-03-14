package com.example.gym_app.common

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.Date

object TokenManager {
    private const val PREFS_FILE_NAME = "encrypted_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val EXPIRATION_TIME_KEY = "expiration_time"

    private fun getEncryptedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAccessToken(context: Context, token: String, expiresAt: Long) {
        getEncryptedPreferences(context).edit().apply {
            putString(ACCESS_TOKEN_KEY, token)
            putLong(EXPIRATION_TIME_KEY, expiresAt)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        return getEncryptedPreferences(context).getString(ACCESS_TOKEN_KEY, null)
    }

    fun getExpirationTime(context: Context): Long {
        return getEncryptedPreferences(context).getLong(EXPIRATION_TIME_KEY, -1)
    }

    fun isTokenActive(context: Context): Boolean {
        val expirationTimeMillis = getExpirationTime(context)
        return expirationTimeMillis > System.currentTimeMillis()
    }

    fun removeToken(context: Context) {
        val editor = getEncryptedPreferences(context).edit()
        editor.remove(ACCESS_TOKEN_KEY)
        editor.remove(EXPIRATION_TIME_KEY)
        editor.apply()
    }
}