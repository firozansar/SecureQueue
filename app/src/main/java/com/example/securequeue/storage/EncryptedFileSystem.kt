package com.example.securequeue.storage

import android.util.Log
import androidx.security.crypto.EncryptedFile
import com.example.securequeue.App
import com.example.securequeue.EMPTY_STRING
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class EncryptedFileSystem(masterKey: String) : EncryptedFileSystemInterface {

    private val encryptedFile = EncryptedFile.Builder(
        File(App.instance.filesDir, FILE_NAME),
        App.instance,
        masterKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()


    override fun savePassword(password: String) {
        deletePassword()
        encryptedFile.openFileOutput().apply {
            write(password.toByteArray(StandardCharsets.UTF_8))
            flush()
            close()
        }
    }

    override fun getPassword(): String {
        var password = EMPTY_STRING
        try {
            val bufferReader = encryptedFile.openFileInput().bufferedReader()
            password = bufferReader.readText()
            bufferReader.close()
        } catch (exception: IOException) {
            Log.d(LOG_EXCEPTION_TAG, exception.message ?: "")
        }
        return password
    }

    override fun deletePassword() {
        val file = File(App.instance.filesDir, FILE_NAME)
        if (file.exists()) file.delete()
    }

    override fun saveUser(user: String) {
        TODO("Not yet implemented")
    }

    override fun getUser(): String {
        TODO("Not yet implemented")
    }

    override fun deleteUser() {
        TODO("Not yet implemented")
    }


    companion object {
        private const val FILE_NAME = "secret_file.txt"
        private const val LOG_EXCEPTION_TAG = "Read file exception"
    }
}