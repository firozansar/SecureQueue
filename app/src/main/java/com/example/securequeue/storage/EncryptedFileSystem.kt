package com.example.securequeue.storage

interface EncryptedFileSystem {

    fun saveString(password: String)

    fun getString(): String

    fun deleteFile()
}