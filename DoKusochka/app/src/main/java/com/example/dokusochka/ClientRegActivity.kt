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

class ClientRegActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginHint: TextView
    private lateinit var passwordHint: TextView
    private lateinit var registerButton: RelativeLayout
    private lateinit var loginText: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_reg_screen)

        initViews()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun initViews() {
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginHint = findViewById(R.id.loginHint)
        passwordHint = findViewById(R.id.passwordHint)
        registerButton = findViewById(R.id.registerButton)
        loginText = findViewById(R.id.loginText)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {

        backButton.setOnClickListener {
            val intent = Intent(this, ClientAuthActivity::class.java)
            startActivity(intent)
            finish()
        }


        registerButton.setOnClickListener {
            attemptRegistration()
        }


        loginText.setOnClickListener {

            val intent = Intent(this, ClientAuthActivity::class.java)
            startActivity(intent)
            finish()
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
                updateRegisterButtonState()
            }
        })


        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

                passwordHint.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.INVISIBLE
                updateRegisterButtonState()
            }
        })
    }

    private fun updateRegisterButtonState() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()


        registerButton.isEnabled = login.isNotEmpty() && password.isNotEmpty()
        registerButton.alpha = if (registerButton.isEnabled) 1.0f else 0.6f
    }

    private fun attemptRegistration() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
            return
        }


        val success = registerUser(login, password)

        if (success) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Ошибка регистрации. Попробуйте другой логин", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(login: String, password: String): Boolean {
        //  будет логика регистрации в базе данных
        return true
    }
}