package com.example.dokusochka

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "UserSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_PHONE = "userPhone"
        private const val KEY_USER_LOGIN = "userLogin"
        private const val KEY_USER_PASSWORD = "userPassword"
        private const val KEY_USER_CITY = "userCity"
        private const val KEY_USER_ADDRESS = "userAddress"
        private const val KEY_IS_ADMIN = "isAdmin"
    }

    // Тег для логов
    private val TAG = "SessionManager"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(
        userId: Int,
        userName: String,
        userPhone: String,
        userLogin: String,
        userPassword: String,
        userCity: String = "",
        userAddress: String = "",
        isAdmin: Boolean = false
    ) {
        Log.d(TAG, "Сохранение сессии пользователя:")
        Log.d(TAG, "ID: $userId")
        Log.d(TAG, "Имя: $userName")
        Log.d(TAG, "Логин: $userLogin")
        Log.d(TAG, "isAdmin: $isAdmin")

        with(sharedPrefs.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_PHONE, userPhone)
            putString(KEY_USER_LOGIN, userLogin)
            putString(KEY_USER_PASSWORD, userPassword)
            putString(KEY_USER_CITY, userCity)
            putString(KEY_USER_ADDRESS, userAddress)
            putBoolean(KEY_IS_ADMIN, isAdmin)
            apply()
        }

        Log.d(TAG, "Сессия сохранена успешно")
    }

    fun isLoggedIn(): Boolean {
        val result = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
        Log.d(TAG, "Проверка авторизации: $result")
        return result
    }

    fun getUserName(): String = sharedPrefs.getString(KEY_USER_NAME, "") ?: ""
    fun getUserPhone(): String = sharedPrefs.getString(KEY_USER_PHONE, "") ?: ""
    fun getUserLogin(): String = sharedPrefs.getString(KEY_USER_LOGIN, "") ?: ""
    fun getUserPassword(): String = sharedPrefs.getString(KEY_USER_PASSWORD, "") ?: ""
    fun getUserCity(): String = sharedPrefs.getString(KEY_USER_CITY, "") ?: ""
    fun getUserAddress(): String = sharedPrefs.getString(KEY_USER_ADDRESS, "") ?: ""
    fun getUserId(): Int = sharedPrefs.getInt(KEY_USER_ID, -1)
    fun isAdmin(): Boolean = sharedPrefs.getBoolean(KEY_IS_ADMIN, false)

    fun logout() {
        Log.d(TAG, "Выход из аккаунта")
        with(sharedPrefs.edit()) {
            clear()
            apply()
        }
    }

    fun updatePassword(newPassword: String) {
        Log.d(TAG, "Обновление пароля")
        sharedPrefs.edit().putString(KEY_USER_PASSWORD, newPassword).apply()
    }

    fun updateAddress(newAddress: String) {
        Log.d(TAG, "Обновление адреса: $newAddress")
        sharedPrefs.edit().putString(KEY_USER_ADDRESS, newAddress).apply()
    }

    fun updateProfile(name: String, phone: String, address: String) {
        Log.d(TAG, "Обновление профиля: $name, $phone, $address")
        with(sharedPrefs.edit()) {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ADDRESS, address)
            apply()
        }
    }
}