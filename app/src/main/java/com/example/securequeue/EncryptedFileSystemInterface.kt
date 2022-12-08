package com.example.securequeue

interface EncryptedFileSystemInterface {

    fun savePassword(password: String)

    fun getPassword(): String

    fun deletePassword()
}