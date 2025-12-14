package com.example.dokusochka

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClientAuthActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: RelativeLayout
    private lateinit var registerText: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_auth_screen)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.registerText)
    }

    private fun setupClickListeners() {
        // кнопка назад - возвращаемся в меню
        backButton.setOnClickListener {
            finish()
        }

        // кнопка входа
        loginButton.setOnClickListener {
            attemptLogin()
        }

        // текст регистрации
        registerText.setOnClickListener {
            val intent = Intent(this, ClientRegActivity::class.java)
            startActivity(intent)
        }
    }

    private fun attemptLogin() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (login.isEmpty()) {
            showToast("Введите логин")
            return
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            return
        }

        // СНАЧАЛА ПРОВЕРЯЕМ АДМИНИСТРАТОРА
        val isAdmin = databaseHelper.authenticateAdmin(login, password)

        if (isAdmin) {
            sessionManager.saveUserSession(
                userId = 0,
                userName = "Администратор",
                userPhone = "",
                userLogin = login,
                userPassword = password,
                userCity = "",
                userAddress = "",
                isAdmin = true
            )

            showToast("Вход администратора выполнен!")
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // ЕСЛИ НЕ АДМИН, ПРОВЕРЯЕМ ПОЛЬЗОВАТЕЛЯ
        val isUser = databaseHelper.authenticateUser(login, password)

        if (isUser) {
            // Получаем данные пользователя
            val userDetails = databaseHelper.getUserDetails(login, password)

            if (userDetails != null) {
                sessionManager.saveUserSession(
                    userId = userDetails["id"]?.toInt() ?: 0,
                    userName = userDetails["name"] ?: "",
                    userPhone = userDetails["phone"] ?: "",
                    userLogin = login,
                    userPassword = password,
                    userCity = "",
                    userAddress = userDetails["address"] ?: "",
                    isAdmin = false
                )

                showToast("Вход пользователя выполнен!")
                finish() // возвращаемся в меню
            } else {
                // Если не удалось получить детали, сохраняем только логин
                sessionManager.saveUserSession(
                    userId = 0,
                    userName = login,
                    userPhone = "",
                    userLogin = login,
                    userPassword = password,
                    userCity = "",
                    userAddress = "",
                    isAdmin = false
                )
                showToast("Вход выполнен!")
                finish()
            }
        } else {
            showToast("Неверный логин или пароль")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}