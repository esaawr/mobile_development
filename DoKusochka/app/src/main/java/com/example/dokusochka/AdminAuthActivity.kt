package com.example.dokusochka

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminAuthActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: RelativeLayout
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_auth_screen)

        initViews()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun initViews() {
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            attemptLogin()
        }
    }

    private fun setupTextWatchers() {
        // Упрощенный TextWatcher без работы с hint-ами
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState()
            }
        }

        loginEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)
    }

    private fun updateLoginButtonState() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        loginButton.isEnabled = login.isNotEmpty() && password.isNotEmpty()
        loginButton.alpha = if (loginButton.isEnabled) 1.0f else 0.6f
    }

    private fun attemptLogin() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val success = authenticateAdmin(login, password)

        if (success) {
            Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateAdmin(login: String, password: String): Boolean {
        // будет логика проверки администратора в бд
        return login == "admin" && password == "admin123"
    }
}