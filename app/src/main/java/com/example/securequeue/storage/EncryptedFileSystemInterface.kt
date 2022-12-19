package com.example.securequeue.storage

interface EncryptedFileSystemInterface {

    fun savePassword(password: String)

    fun getPassword(): String

    fun deletePassword()

    fun saveUser(user: String)

    fun getUser(): String

    fun deleteUser()
}