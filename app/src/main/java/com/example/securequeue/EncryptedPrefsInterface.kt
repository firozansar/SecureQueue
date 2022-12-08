package com.example.securequeue

interface EncryptedPrefsInterface {

    fun savePassword(password: String)

    fun getPassword(): String

    fun deletePassword()
}