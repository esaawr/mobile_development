package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import android.view.LayoutInflater

class CartActivity : AppCompatActivity() {

    private lateinit var cartItemsContainer: LinearLayout
    private lateinit var emptyCartMessage: TextView
    private lateinit var cartScrollView: ScrollView
    private lateinit var totalPanel: RelativeLayout
    private lateinit var totalText: TextView
    private lateinit var orderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        // Инициализация UI элементов
        initViews()

        // Получаем данные из Intent
        val cartItemsCount = intent.getIntExtra("cart_items_count", 0)
        val totalPrice = intent.getDoubleExtra("cart_total_price", 0.0)
        val cartItemsString = intent.getStringExtra("cart_items") ?: ""

        // Кнопка назад
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Возврат к предыдущей активности
        }

        // Кнопка заказа
        orderButton.setOnClickListener {
            placeOrder(totalPrice)
        }

        // Обновление отображения корзины
        if (cartItemsCount > 0) {
            showCartItems(cartItemsString, totalPrice)
        } else {
            showEmptyCart()
        }
    }

    private fun initViews() {
        cartItemsContainer = findViewById(R.id.cartItemsContainer)
        emptyCartMessage = findViewById(R.id.emptyCartMessage)
        cartScrollView = findViewById(R.id.cartScrollView)
        totalPanel = findViewById(R.id.totalPanel)
        totalText = findViewById(R.id.totalText)
        orderButton = findViewById(R.id.orderButton)
    }

    private fun showCartItems(cartItemsString: String, totalPrice: Double) {
        emptyCartMessage.visibility = TextView.GONE
        cartScrollView.visibility = ScrollView.VISIBLE
        totalPanel.visibility = RelativeLayout.VISIBLE

        // Обновляем общую сумму
        totalText.text = "ИТОГО: ${totalPrice.toInt()} Р"

        // Парсим строку с товарами и отображаем их
        displayCartItems(cartItemsString)
    }

    private fun showEmptyCart() {
        emptyCartMessage.visibility = TextView.VISIBLE
        cartScrollView.visibility = ScrollView.GONE
        totalPanel.visibility = RelativeLayout.GONE
    }

    private fun displayCartItems(cartItemsString: String) {
        cartItemsContainer.removeAllViews()

        if (cartItemsString.isNotEmpty()) {
            val items = cartItemsString.split(";")
            items.forEach { itemString ->
                val parts = itemString.split(",")
                if (parts.size == 4) {
                    val id = parts[0].toInt()
                    val name = parts[1]
                    val price = parts[2].toDouble()
                    val quantity = parts[3].toInt()

                    val itemView = createCartItemView(id, name, price, quantity)
                    cartItemsContainer.addView(itemView)
                }
            }
        }
    }

    private fun createCartItemView(id: Int, name: String, price: Double, quantity: Int): TextView {
        return TextView(this).apply {
            text = "$name - ${price.toInt()} Р (Количество: $quantity)"
            setTextColor(ContextCompat.getColor(this@CartActivity, android.R.color.black))
            textSize = 16f
            setPadding(dipToPx(16), dipToPx(16), dipToPx(16), dipToPx(16))
            background = ContextCompat.getDrawable(this@CartActivity, R.drawable.rounded_white)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dipToPx(16))
            }
        }
    }

    private fun dipToPx(dip: Int): Int {
        return (dip * resources.displayMetrics.density).toInt()
    }

    private fun placeOrder(totalPrice: Double) {
        Toast.makeText(this, "Заказ оформлен на сумму ${totalPrice.toInt()} Р!", Toast.LENGTH_LONG).show()
        finish()
    }
}