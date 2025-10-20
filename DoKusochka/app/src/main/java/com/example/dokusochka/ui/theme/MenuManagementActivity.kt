package com.example.dokusochka

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MenuManagementActivity : AppCompatActivity() {

    private lateinit var pizzaNameEditText: EditText
    private lateinit var pizzaPriceEditText: EditText
    private lateinit var pizzaDescriptionEditText: EditText
    private lateinit var selectImageButton: LinearLayout
    private lateinit var addPizzaButton: LinearLayout
    private lateinit var exportExcelButton: LinearLayout
    private lateinit var backButton: LinearLayout
    private lateinit var selectedImageView: ImageView

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_managment)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        pizzaNameEditText = findViewById(R.id.pizzaNameEditText)
        pizzaPriceEditText = findViewById(R.id.pizzaPriceEditText)
        pizzaDescriptionEditText = findViewById(R.id.pizzaDescriptionEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        addPizzaButton = findViewById(R.id.addPizzaButton)
        exportExcelButton = findViewById(R.id.exportExcelButton)
        backButton = findViewById(R.id.backButton)
        selectedImageView = findViewById(R.id.selectedImageView)
    }

    private fun setupClickListeners() {
        // Кнопка выбора изображения
        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // Кнопка добавления пиццы
        addPizzaButton.setOnClickListener {
            addPizzaToDatabase()
        }

        // Кнопка экспорта в Excel
        exportExcelButton.setOnClickListener {
            exportToExcel()
        }

        // Кнопка назад
        backButton.setOnClickListener {
            finish() // Возврат к предыдущему экрану (AdminManagementActivity)
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

        // Валидация данных
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

        // Здесь будет логика добавления в базу данных
        // Пока просто покажем сообщение об успехе
        showToast("Пицца '$name' добавлена!\nЦена: $price руб.\nОписание: $description")

        // Очистка полей после добавления
        clearFields()
    }

    private fun exportToExcel() {
        // Здесь будет логика экспорта в Excel
        // Пока просто покажем сообщение
        showToast("Экспорт в Excel выполнен")
    }

    private fun clearFields() {
        pizzaNameEditText.text.clear()
        pizzaPriceEditText.text.clear()
        pizzaDescriptionEditText.text.clear()
        selectedImageView.visibility = android.view.View.GONE
        selectedImageUri = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                selectedImageView.setImageURI(uri)
                selectedImageView.visibility = android.view.View.VISIBLE
                showToast("Изображение выбрано")
            }
        }
    }
}