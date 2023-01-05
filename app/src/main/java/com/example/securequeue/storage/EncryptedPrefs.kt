package com.example.securequeue.storage

interface EncryptedPrefs {

    fun savePassword(password: String)

    fun getPassword(): String

    fun deletePassword()
}