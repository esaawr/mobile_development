package com.example.dokusochka

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.util.Log
import java.io.FileOutputStream

class MenuManagementActivity : AppCompatActivity() {

    private lateinit var pizzaNameEditText: EditText
    private lateinit var pizzaPriceEditText: EditText
    private lateinit var pizzaDescriptionEditText: EditText
    private lateinit var selectImageButton: LinearLayout
    private lateinit var addPizzaButton: LinearLayout
    private lateinit var backButton: LinearLayout
    private lateinit var selectedImageView: ImageView
    private lateinit var databaseHelper: DatabaseHelper

    private var selectedImageUri: Uri? = null
    private var imageBytes: ByteArray? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val MAX_IMAGE_SIZE = 1024 * 1024 // 1MB
        private const val TAG = "MenuManagementActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_managment)

        databaseHelper = DatabaseHelper(this)
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        pizzaNameEditText = findViewById(R.id.pizzaNameEditText)
        pizzaPriceEditText = findViewById(R.id.pizzaPriceEditText)
        pizzaDescriptionEditText = findViewById(R.id.pizzaDescriptionEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        addPizzaButton = findViewById(R.id.addPizzaButton)
        backButton = findViewById(R.id.backButton)
        selectedImageView = findViewById(R.id.selectedImageView)
    }

    private fun setupClickListeners() {
        // кнопка выбора изображения
        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // кнопка добавления пиццы
        addPizzaButton.setOnClickListener {
            addPizzaToDatabase()
        }

        // кнопка назад
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun addPizzaToDatabase() {
        val name = pizzaNameEditText.text.toString().trim()
        val priceText = pizzaPriceEditText.text.toString().trim()
        val description = pizzaDescriptionEditText.text.toString().trim()

        if (name.isEmpty()) {
            showToast("Введите название пиццы")
            return
        }

        if (priceText.isEmpty()) {
            showToast("Введите цену пиццы")
            return
        }

        val price = try {
            priceText.toDouble()
        } catch (e: NumberFormatException) {
            showToast("Введите корректную цену")
            return
        }

        if (price <= 0) {
            showToast("Цена должна быть больше 0")
            return
        }

        // Сохраняем изображение и получаем путь
        val imagePath = if (imageBytes != null) {
            saveImageToStorage(name)
        } else {
            "default_pizza" // используем изображение по умолчанию
        }

        // Добавляем пиццу в базу данных
        val success = databaseHelper.addPizza(name, price, description, imagePath)

        if (success) {
            showToast("Пицца '$name' успешно добавлена в меню!")

            // ВАЖНО: Отправляем broadcast для обновления MenuActivity
            sendPizzaAddedBroadcast()

            // Можно уведомить пользователей о новинке
            notifyUsersAboutNewPizza(name)

            // очистка полей после добавления
            clearFields()

            // Возвращаемся назад после успешного добавления (опционально)
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 1500)
        } else {
            showToast("Ошибка при добавлении пиццы")
        }
    }

    private fun sendPizzaAddedBroadcast() {
        try {
            // Отправляем broadcast для обновления MenuActivity
            // Для Android 14+ используем явную отправку с флагом
            val intent = Intent("ACTION_PIZZA_ADDED")

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Для Android 8.0+ используем sendBroadcast с флагом
                intent.setPackage(packageName) // Ограничиваем broadcast только нашим приложением
                sendBroadcast(intent)
            } else {
                sendBroadcast(intent)
            }

            Log.d(TAG, "Broadcast отправлен: ACTION_PIZZA_ADDED")

            // Также можно сохранить флаг в SharedPreferences
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("pizza_updated", true).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка отправки broadcast: ${e.message}")
        }
    }

    private fun saveImageToStorage(pizzaName: String): String {
        return try {
            val fileName = "pizza_${pizzaName.toLowerCase().replace(" ", "_")}_${System.currentTimeMillis()}.jpg"
            val file = File(getExternalFilesDir(null), fileName)

            FileOutputStream(file).use { fos ->
                imageBytes?.let { fos.write(it) }
            }

            fileName
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения изображения: ${e.message}")
            e.printStackTrace()
            "default_pizza"
        }
    }

    private fun notifyUsersAboutNewPizza(pizzaName: String) {
        // Здесь можно добавить логику уведомления пользователей
        // Например, через Firebase Cloud Messaging или локальные уведомления
        Log.d(TAG, "Новая пицца добавлена: $pizzaName")
    }

    private fun clearFields() {
        pizzaNameEditText.text.clear()
        pizzaPriceEditText.text.clear()
        pizzaDescriptionEditText.text.clear()
        selectedImageView.visibility = View.GONE
        selectedImageUri = null
        imageBytes = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                selectedImageView.setImageURI(uri)
                selectedImageView.visibility = View.VISIBLE

                // Конвертируем изображение в байты
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    imageBytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (imageBytes != null && imageBytes!!.size > MAX_IMAGE_SIZE) {
                        showToast("Изображение слишком большое. Максимум 1MB")
                        selectedImageView.visibility = View.GONE
                        imageBytes = null
                    } else {
                        showToast("Изображение выбрано")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка загрузки изображения: ${e.message}")
                    showToast("Ошибка загрузки изображения")
                    e.printStackTrace()
                }
            }
        }
    }
}