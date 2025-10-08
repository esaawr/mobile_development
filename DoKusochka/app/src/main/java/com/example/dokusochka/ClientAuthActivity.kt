package com.example.dokusochka

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClientAuthActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginHint: TextView
    private lateinit var passwordHint: TextView
    private lateinit var loginButton: RelativeLayout
    private lateinit var registerText: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_auth_screen)

        initViews()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun initViews() {
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginHint = findViewById(R.id.loginHint)
        passwordHint = findViewById(R.id.passwordHint)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.registerText)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {

        backButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }


        loginButton.setOnClickListener {
            attemptLogin()
        }


        registerText.setOnClickListener {
            showRegistrationScreen()
        }


        findViewById<RelativeLayout>(R.id.loginContainer).setOnClickListener {
            loginEditText.requestFocus()
        }


        findViewById<RelativeLayout>(R.id.passwordContainer).setOnClickListener {
            passwordEditText.requestFocus()
        }
    }

    private fun setupTextWatchers() {

        loginEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

                loginHint.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.INVISIBLE
                updateLoginButtonState()
            }
        })


        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

                passwordHint.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.INVISIBLE
                updateLoginButtonState()
            }
        })
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


        val success = authenticateUser(login, password)

        if (success) {
            Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateUser(login: String, password: String): Boolean {
        //будет логика проверки в бд
        return login == "user" && password == "password"
    }

    private fun showRegistrationScreen() {

        val intent = Intent(this, ClientRegActivity::class.java)
        startActivity(intent)

    }
}