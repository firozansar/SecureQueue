package com.example.securequeue

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC
import com.example.securequeue.databinding.ActivityMainBinding
import com.squareup.moshi.Moshi
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val masterKey: String by lazy { MasterKeys.getOrCreate(AES256_GCM_SPEC) }
    private val encryptedPrefs: EncryptedPrefsInterface by lazy { EncryptedPrefs(masterKey) }
    private val encryptedFile: EncryptedFileSystem by lazy { EncryptedFileSystem(masterKey) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUI()
        setupListeners()
    }

    private fun initUI() {
        if (getPasswordFromEncryptedPrefs().isBlank()) {
            binding.sharedPrefsPassword.text = EMPTY_STRING
        } else {
            hidePrefsPassword()
        }
        if (getPasswordFromEncryptedFile().isBlank()) {
            binding.filesPassword.text = EMPTY_STRING
        } else {
            hideFilesPassword()
        }
        if (getQueueFromEncryptedFile().isBlank()) {
            binding.fileQueue.text = EMPTY_STRING
        }
    }

    private fun setupListeners() {
        binding.run {
            sharedPrefsSubmit.setOnClickListener { savePasswordToEncryptedPrefs() }
            filesSubmit.setOnClickListener { savePasswordToEncryptedFile() }
            showSharedPrefsPassword.setOnClickListener { toggleSharedPrefsPasswordVisibility() }
            showFilesPassword.setOnClickListener { toggleFilesPasswordVisibility() }
            addToQueue.setOnClickListener { addToSecureQueue()}
            showQueue.setOnClickListener { showStoredQueue() }
            clearQueue.setOnClickListener { getRetryQueue().clear() }
            deleteSharedPrefsPassword.setOnClickListener { deleteSharedPrefsPassword() }
            deleteFilePassword.setOnClickListener { deleteFilePassword() }
        }
    }

    private fun savePasswordToEncryptedPrefs() {
        closeKeyboard()
        if (isInputValid(binding.sharedPrefsPasswordInput.text.toString())) {
            encryptedPrefs.savePassword(binding.sharedPrefsPasswordInput.text.toString())
            binding.sharedPrefsPassword.text = getText(R.string.hidden_password)
            binding.showSharedPrefsPassword.text = getText(R.string.show_password)
            binding.sharedPrefsPasswordInput.text.clear()
            showToast(getString(R.string.password_saved))
        } else {
            showToast(getString(R.string.input_invalid))
        }
    }

    private fun savePasswordToEncryptedFile() {
        closeKeyboard()
        if (isInputValid(binding.filesPasswordInput.text.toString())) {
            encryptedFile.savePassword(binding.filesPasswordInput.text.toString())
            binding.filesPassword.text = getText(R.string.hidden_password)
            binding.showFilesPassword.text = getText(R.string.show_password)
            binding.filesPasswordInput.text.clear()
            showToast(getString(R.string.password_saved))
        } else {
            showToast(getString(R.string.input_invalid))
        }
    }

    private fun toggleSharedPrefsPasswordVisibility() {
        if (binding.showSharedPrefsPassword.text == getText(R.string.show_password)) {
            if (binding.sharedPrefsPassword.text != EMPTY_STRING) {
                binding.showSharedPrefsPassword.text = getText(R.string.hide_password)
                showSharedPrefsPassword()
            } else {
                showToast(getString(R.string.password_not_set))
            }
        } else {
            binding.showSharedPrefsPassword.text = getText(R.string.show_password)
            hidePrefsPassword()
        }
    }

    private fun showSharedPrefsPassword() {
        val password = getPasswordFromEncryptedPrefs()
        if (password.isBlank()) {
            binding.sharedPrefsPassword.text = EMPTY_STRING
            showToast(getString(R.string.password_not_set))
        } else {
            binding.sharedPrefsPassword.text = password
        }
    }

    private fun addToSecureQueue() {
        val newUser = generateUser()
        getRetryQueue().add(newUser)
        Log.d(TAG, "New user added: $newUser")
    }

    private fun showStoredQueue() {
        val iterator =  getRetryQueue().iterator()
        val sb = StringBuilder()
        while (iterator.hasNext()) {
            val user = iterator.next()
            sb.append(user.toString())
            Log.d(TAG, "User in the queue: $user")
        }
        binding.fileQueue.text = sb.toString()
    }

    private fun hidePrefsPassword() {
        binding.sharedPrefsPassword.text = getText(R.string.hidden_password)
    }

    private fun toggleFilesPasswordVisibility() {
        if (binding.showFilesPassword.text == getText(R.string.show_password)) {
            if (binding.filesPassword.text != EMPTY_STRING) {
                binding.showFilesPassword.text = getText(R.string.hide_password)
                showFilesPassword()
            } else {
                showToast(getString(R.string.password_not_set))
            }
        } else {
            binding.showFilesPassword.text = getText(R.string.show_password)
            hideFilesPassword()
        }
    }

    private fun showFilesPassword() {
        val password = getPasswordFromEncryptedFile()
        if (password.isBlank()) {
            binding.filesPassword.text = ""
            showToast(getString(R.string.password_not_set))
        } else {
            binding.filesPassword.text = password
        }
    }

    private fun hideFilesPassword() {
        binding.filesPassword.text = getText(R.string.hidden_password)
    }

    private fun getPasswordFromEncryptedPrefs(): String {
        return encryptedPrefs.getPassword()
    }

    private fun getQueueFromEncryptedFile(): String {
        return encryptedFile.getPassword()
    }

    private fun getPasswordFromEncryptedFile(): String {
        return encryptedFile.getPassword()
    }

    private fun deleteSharedPrefsPassword() {
        if (binding.sharedPrefsPassword.text.isNotBlank()) {
            encryptedPrefs.deletePassword()
            binding.sharedPrefsPassword.text = EMPTY_STRING
            binding.showSharedPrefsPassword.text = getText(R.string.show_password)
            showToast(getString(R.string.password_deleted))
        } else {
            showToast(getString(R.string.password_not_set))
        }
    }

    private fun deleteFilePassword() {
        if (binding.filesPassword.text.isNotBlank()) {
            encryptedFile.deletePassword()
            binding.filesPassword.text = EMPTY_STRING
            binding.showFilesPassword.text = getText(R.string.show_password)
            showToast(getString(R.string.password_deleted))
        } else {
            showToast(getString(R.string.password_not_set))
        }
    }

    private fun isInputValid(input: String): Boolean {
        return input.isNotBlank()
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    private fun closeKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    // Queue implementation
    private fun generateUser(): User {
        val candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until 8) {
            sb.append(candidateChars[random.nextInt(candidateChars.length)])
        }
        val id = random.nextInt(candidateChars.length)
        val name = sb.toString()
        return User(id, name)
    }

    private fun getRetryQueue(): FileQueue<User> {
        val fileQueueFactory = FileQueueFactory(baseContext, Moshi.Builder().build())
        return fileQueueFactory.create(
            SECURE_QUEUE_FILE,
            User::class.java
        )
    }
}

const val TAG = "Firoz"
const val EMPTY_STRING = ""
const val SECURE_QUEUE_FILE = "secure_queue_file"