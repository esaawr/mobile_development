// ClientProfileActivity.kt (исправленный файл)
package com.example.dokusochka

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ClientProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var backButton: ImageView
    private lateinit var userNameText: TextView
    private lateinit var userPhoneText: TextView
    private lateinit var userLoginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var passwordToggle: ImageView
    private lateinit var changePasswordButton: Button
    private lateinit var cityText: TextView  // Переименовал на cityText, как в макете
    private lateinit var changeCityButton: Button
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_profile)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        // Проверяем авторизацию
        if (!sessionManager.isLoggedIn()) {
            redirectToAuth()
            return
        }

        initViews()
        setupClickListeners()
        loadUserData()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        userNameText = findViewById(R.id.userNameText)
        userPhoneText = findViewById(R.id.userPhoneText)
        userLoginText = findViewById(R.id.userLoginText)
        passwordText = findViewById(R.id.passwordText)
        passwordToggle = findViewById(R.id.passwordToggle)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        cityText = findViewById(R.id.cityText)  // Инициализируем cityText
        changeCityButton = findViewById(R.id.changeCityButton)
        logoutButton = findViewById(R.id.logoutButton)

    }

    private fun setupClickListeners() {
        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        // Переключение видимости пароля
        passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        // Кнопка изменения пароля
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        // Кнопка изменения города/адреса
        changeCityButton.setOnClickListener {
            showChangeAddressDialog()
        }

        // Кнопка выхода
        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }


    }

    private fun loadUserData() {
        userNameText.text = sessionManager.getUserName()
        userPhoneText.text = sessionManager.getUserPhone()
        userLoginText.text = sessionManager.getUserLogin()

        // Пароль по умолчанию скрыт
        passwordText.text = "••••••••"
        passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Адрес (используем cityText)
        val address = sessionManager.getUserAddress()
        cityText.text = if (address.isNotEmpty()) address else "Адрес не указан"  // Изменил на cityText
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            // Показываем пароль
            passwordText.text = sessionManager.getUserPassword()
            passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Замените на свои иконки, если нужно
            // passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            // Скрываем пароль
            passwordText.text = "••••••••"
            passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // passwordToggle.setImageResource(R.drawable.ic_visibility)
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)

        AlertDialog.Builder(this)
            .setTitle("Изменить пароль")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Обновляем пароль в локальном хранилище
                sessionManager.updatePassword(newPassword)

                // TODO: Обновить пароль в базе данных
                Toast.makeText(this, "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                loadUserData()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showChangeAddressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_city, null)
        val addressEditText = dialogView.findViewById<EditText>(R.id.cityEditText)

        // Меняем заголовок и подсказку
        addressEditText.hint = "Введите ваш адрес"
        addressEditText.setText(sessionManager.getUserAddress())

        AlertDialog.Builder(this)
            .setTitle("Изменить адрес")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val newAddress = addressEditText.text.toString().trim()
                if (newAddress.isEmpty()) {
                    Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Сохраняем адрес в SessionManager
                sessionManager.updateAddress(newAddress)

                // Обновляем отображение
                cityText.text = newAddress  // Исправлено: cityText вместо addressText

                Toast.makeText(this, "Адрес изменен", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Выйти") { dialog, _ ->
                // Очищаем корзину при выходе
                CartManager.clearCart()

                // Завершаем сессию
                sessionManager.logout()

                Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()

                // Возвращаемся в меню
                val intent = Intent(this, MenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun redirectToAuth() {
        val intent = Intent(this, ClientAuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadUserData()
        }
    }

    companion object {
        private const val REQUEST_EDIT_PROFILE = 100
    }
}