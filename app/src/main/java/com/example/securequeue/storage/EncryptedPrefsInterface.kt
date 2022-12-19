package com.example.securequeue.storage

interface EncryptedPrefsInterface {

    fun savePassword(password: String)

    fun getPassword(): String

    fun deletePassword()
}