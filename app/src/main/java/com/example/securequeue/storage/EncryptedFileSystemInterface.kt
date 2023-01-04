package com.example.securequeue.storage

interface EncryptedFileSystemInterface {

    fun saveString(password: String)

    fun getString(): String

    fun deleteFile()
}