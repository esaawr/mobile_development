package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MenuActivity : AppCompatActivity() {

    // Данные корзины (хранятся временно в памяти)
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        menuIcon.setOnClickListener {
            val intent = Intent(this, ClientAuthActivity::class.java)
            startActivity(intent)
        }

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener {
            // Переход к корзине с передачей данных
            val intent = Intent(this, CartActivity::class.java)

            // Передаем данные корзины через Intent
            intent.putExtra("cart_items_count", cartItems.size)
            intent.putExtra("cart_total_price", cartItems.sumOf { it.price * it.quantity })

            // Можно передать список как строку (для простоты)
            val cartItemsString = cartItems.joinToString(";") { "${it.id},${it.name},${it.price},${it.quantity}" }
            intent.putExtra("cart_items", cartItemsString)

            startActivity(intent)
        }

        // Обработчики для кнопок пицц
        setupPizzaButtons()
    }

    private fun setupPizzaButtons() {
        // Пицца 1: 4 СЫРА
        setupPizzaControls(1,
            findViewById(R.id.pizza1PriceButton),
            findViewById(R.id.pizza1QuantityControls),
            findViewById(R.id.pizza1MinusButton),
            findViewById(R.id.pizza1PlusButton),
            findViewById(R.id.pizza1QuantityText)
        )

        // Пицца 2: ПЕППЕРОНИ
        setupPizzaControls(2,
            findViewById(R.id.pizza2PriceButton),
            findViewById(R.id.pizza2QuantityControls),
            findViewById(R.id.pizza2MinusButton),
            findViewById(R.id.pizza2PlusButton),
            findViewById(R.id.pizza2QuantityText)
        )

        // Пицца 3: МЯСНАЯ
        setupPizzaControls(3,
            findViewById(R.id.pizza3PriceButton),
            findViewById(R.id.pizza3QuantityControls),
            findViewById(R.id.pizza3MinusButton),
            findViewById(R.id.pizza3PlusButton),
            findViewById(R.id.pizza3QuantityText)
        )
    }

    private fun setupPizzaControls(
        pizzaId: Int,
        priceButton: TextView,
        quantityControls: LinearLayout,
        minusButton: TextView,
        plusButton: TextView,
        quantityText: TextView
    ) {
        val pizzaName = when (pizzaId) {
            1 -> "4 СЫРА"
            2 -> "ПЕППЕРОНИ"
            3 -> "МЯСНАЯ"
            else -> ""
        }
        val pizzaPrice = 289.0

        // Обработчик кнопки цены
        priceButton.setOnClickListener {
            // Скрываем кнопку цены, показываем управление количеством
            priceButton.visibility = TextView.GONE
            quantityControls.visibility = LinearLayout.VISIBLE

            // Добавляем пиццу в корзину
            addPizzaToCart(pizzaId, pizzaName, pizzaPrice)

            // Обновляем отображение количества
            updateQuantityDisplay(pizzaId, quantityText)
        }

        // Обработчик кнопки минус
        minusButton.setOnClickListener {
            changePizzaQuantity(pizzaId, -1)
            updateQuantityDisplay(pizzaId, quantityText)

            // Если количество стало 0, возвращаем кнопку цены
            val item = cartItems.find { it.id == pizzaId }
            if (item?.quantity == 0) {
                priceButton.visibility = TextView.VISIBLE
                quantityControls.visibility = LinearLayout.GONE
            }
        }

        // Обработчик кнопки плюс
        plusButton.setOnClickListener {
            changePizzaQuantity(pizzaId, 1)
            updateQuantityDisplay(pizzaId, quantityText)
        }
    }

    private fun addPizzaToCart(pizzaId: Int, pizzaName: String, pizzaPrice: Double) {
        val existingItem = cartItems.find { it.id == pizzaId }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(pizzaId, pizzaName, pizzaPrice, 1))
        }
    }

    private fun changePizzaQuantity(pizzaId: Int, change: Int) {
        val item = cartItems.find { it.id == pizzaId }
        item?.let {
            val newQuantity = it.quantity + change
            if (newQuantity >= 0) {
                it.quantity = newQuantity
                if (newQuantity == 0) {
                    cartItems.remove(it)
                }
            }
        }
    }

    private fun updateQuantityDisplay(pizzaId: Int, quantityText: TextView) {
        val item = cartItems.find { it.id == pizzaId }
        quantityText.text = item?.quantity?.toString() ?: "0"
    }

    // Data class для товара в корзине
    data class CartItem(
        val id: Int,
        val name: String,
        val price: Double,
        var quantity: Int
    )
}