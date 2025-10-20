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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_auth_screen)

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
        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        // Кнопка входа
        loginButton.setOnClickListener {
            attemptLogin()
        }

        // Текст регистрации
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

        // Здесь будет проверка с базой данных
        showToast("Вход выполнен!")
        // Переход к главному меню клиента
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}