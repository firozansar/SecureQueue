package com.example.securequeue.storage

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.securequeue.App
import com.example.securequeue.EMPTY_STRING

class EncryptedPrefsImpl(masterKey: String) : EncryptedPrefs {

    private val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        SHARED_PREFS_FILENAME,
        masterKey,
        App.instance,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun savePassword(password: String) {
        encryptedSharedPreferences.edit().putString(PASSWORD_KEY, password).apply()
    }

    override fun getPassword(): String {
        return encryptedSharedPreferences.getString(PASSWORD_KEY, EMPTY_STRING) ?: EMPTY_STRING
    }

    override fun deletePassword() {
        encryptedSharedPreferences.edit().putString(PASSWORD_KEY, EMPTY_STRING).apply()
    }

    companion object {
        private const val PASSWORD_KEY = "PASSWORD"
        private const val SHARED_PREFS_FILENAME = "sharedPrefs"
    }
}
