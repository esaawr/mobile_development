package com.example.dokusochka

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClientRegActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: RelativeLayout
    private lateinit var loginText: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_reg_screen)

        sessionManager = SessionManager(this)
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginText = findViewById(R.id.loginText)
    }

    private fun setupClickListeners() {
        // кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        // кнопка регистрации
        registerButton.setOnClickListener {
            attemptRegistration()
        }

        // текст входа
        loginText.setOnClickListener {
            val intent = Intent(this, ClientAuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun attemptRegistration() {
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (name.isEmpty()) {
            showToast("Введите имя")
            return
        }

        if (phone.isEmpty()) {
            showToast("Введите телефон")
            return
        }

        if (login.isEmpty()) {
            showToast("Введите логин")
            return
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            return
        }

        if (password.length < 6) {
            showToast("Пароль должен содержать минимум 6 символов")
            return
        }

        val databaseHelper = DatabaseHelper(this)

        // проверяю, не существует ли уже пользователь с таким логином
        if (databaseHelper.checkUserExists(login)) {
            showToast("Пользователь с таким логином уже существует")
            return
        }

        // регистрирую нового пользователя
        val success = databaseHelper.addUser(login, password, name, phone, address)

        if (success) {
            // Получаем ID нового пользователя
            val userId = getNewUserId(databaseHelper, login)

            // Автоматически авторизуем пользователя после регистрации
            sessionManager.saveUserSession(
                userId = userId,
                userName = name,
                userPhone = phone,
                userLogin = login,
                userPassword = password,
                userCity = "",
                userAddress = address,
                isAdmin = false
            )

            showToast("Регистрация успешна! Вы авторизованы.")
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showToast("Ошибка регистрации")
        }
    }

    private fun getNewUserId(databaseHelper: DatabaseHelper, login: String): Int {
        // Получаем ID нового пользователя
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE login = ?",
            arrayOf(login)
        )

        return try {
            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            cursor.close()
            db.close()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}