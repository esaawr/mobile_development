package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class DeletePizzaActivity : AppCompatActivity() {

    private lateinit var pizzaSpinner: Spinner
    private lateinit var pizzaNameEditText: TextView
    private lateinit var deletePizzaButton: LinearLayout
    private lateinit var backButton: LinearLayout
    private lateinit var pizzaInfoTextView: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedPizza: Pizza? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_pizza)

        pizzaSpinner = findViewById(R.id.pizzaSpinner)
        pizzaNameEditText = findViewById(R.id.pizzaNameEditText)
        deletePizzaButton = findViewById(R.id.deletePizzaButton)
        backButton = findViewById(R.id.backButton)
        pizzaInfoTextView = findViewById(R.id.pizzaInfoTextView)

        databaseHelper = DatabaseHelper(this)

        // Загружаем список пицц
        loadPizzas()

        // Обработчик выбора пиццы из Spinner
        pizzaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Пропускаем первый элемент "Выберите пиццу"
                    val pizzaName = parent?.getItemAtPosition(position).toString()
                    val pizza = databaseHelper.getPizzaByName(pizzaName)
                    pizza?.let {
                        selectedPizza = it
                        pizzaNameEditText.text = it.name
                        showPizzaInfo(it)
                    }
                } else {
                    selectedPizza = null
                    pizzaInfoTextView.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPizza = null
                pizzaInfoTextView.visibility = View.GONE
            }
        }

        // Обработчик ручного ввода названия
        pizzaNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && pizzaNameEditText.text.isNotEmpty()) {
                val pizzaName = pizzaNameEditText.text.toString()
                val pizza = databaseHelper.getPizzaByName(pizzaName)
                pizza?.let {
                    selectedPizza = it
                    showPizzaInfo(it)
                } ?: run {
                    selectedPizza = null
                    pizzaInfoTextView.visibility = View.GONE
                    Toast.makeText(this, "Пицца не найдена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Кнопка удаления пиццы
        deletePizzaButton.setOnClickListener {
            val pizzaName = pizzaNameEditText.text.toString().trim()

            if (pizzaName.isEmpty()) {
                Toast.makeText(this, "Введите название пиццы", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, существует ли пицца
            val pizza = databaseHelper.getPizzaByName(pizzaName)
            if (pizza == null) {
                Toast.makeText(this, "Пицца с названием '$pizzaName' не найдена", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Показываем диалог подтверждения
            showDeleteConfirmationDialog(pizza)
        }

        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadPizzas() {
        val pizzas = databaseHelper.getAllPizzas()

        val pizzaNames = mutableListOf("Выберите пиццу")
        pizzaNames.addAll(pizzas.map { it.name })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pizzaNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pizzaSpinner.adapter = adapter
    }

    private fun showPizzaInfo(pizza: Pizza) {
        val info = """
            Название: ${pizza.name}
            Цена: ${pizza.price} ₽
            Описание: ${pizza.description}
        """.trimIndent()

        pizzaInfoTextView.text = info
        pizzaInfoTextView.visibility = View.VISIBLE
    }

    private fun showDeleteConfirmationDialog(pizza: Pizza) {
        AlertDialog.Builder(this)
            .setTitle("Удаление пиццы")
            .setMessage("Вы уверены, что хотите удалить пиццу '${pizza.name}'?")
            .setPositiveButton("Удалить") { dialog, which ->
                deletePizza(pizza)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deletePizza(pizza: Pizza) {
        val success = databaseHelper.deletePizza(pizza.name)

        if (success) {
            Toast.makeText(this, "Пицца '${pizza.name}' успешно удалена", Toast.LENGTH_SHORT).show()

            // Обновляем список пицц
            loadPizzas()

            // Очищаем поля
            pizzaNameEditText.text = ""
            pizzaInfoTextView.visibility = View.GONE
            selectedPizza = null

            // Сбрасываем Spinner на первый элемент
            pizzaSpinner.setSelection(0)
        } else {
            Toast.makeText(this, "Ошибка при удалении пиццы", Toast.LENGTH_SHORT).show()
        }
    }
}