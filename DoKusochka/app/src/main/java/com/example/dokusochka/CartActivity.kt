package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat

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

        initViews()

        // кнопка назад
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // кнопка заказа
        orderButton.setOnClickListener {
            placeOrder()
        }

        // обновление отображения корзины
        updateCartDisplay()
    }

    private fun initViews() {
        cartItemsContainer = findViewById(R.id.cartItemsContainer)
        emptyCartMessage = findViewById(R.id.emptyCartMessage)
        cartScrollView = findViewById(R.id.cartScrollView)
        totalPanel = findViewById(R.id.totalPanel)
        totalText = findViewById(R.id.totalText)
        orderButton = findViewById(R.id.orderButton)
    }

    private fun updateCartDisplay() {
        val cartItems = CartManager.getItems()
        val totalPrice = CartManager.getTotalPrice()

        if (cartItems.isEmpty()) {
            showEmptyCart()
        } else {
            showCartItems(cartItems, totalPrice)
        }
    }

    private fun showCartItems(cartItems: List<CartItem>, totalPrice: Double) {
        emptyCartMessage.visibility = TextView.GONE
        cartScrollView.visibility = ScrollView.VISIBLE
        totalPanel.visibility = RelativeLayout.VISIBLE

        totalText.text = "ИТОГО: ${totalPrice.toInt()} Р"
        displayCartItems(cartItems)
    }

    private fun showEmptyCart() {
        emptyCartMessage.visibility = TextView.VISIBLE
        cartScrollView.visibility = ScrollView.GONE
        totalPanel.visibility = RelativeLayout.GONE
    }

    private fun displayCartItems(cartItems: List<CartItem>) {
        cartItemsContainer.removeAllViews()

        cartItems.forEach { item ->
            val itemView = createCartItemView(item)
            cartItemsContainer.addView(itemView)
        }
    }

    private fun createCartItemView(item: CartItem): TextView {
        return TextView(this).apply {
            text = "${item.name} - ${item.price.toInt()} Р (Количество: ${item.quantity})"
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

    private fun placeOrder() {
        val totalPrice = CartManager.getTotalPrice()
        val items = CartManager.getItems()
        
        // формирую список товаров
        val itemsList = items.joinToString("\n") { 
            "${it.name} x${it.quantity} - ${(it.price * it.quantity).toInt()} Р" 
        }
        
        // создание заказа в базе данных
        val databaseHelper = DatabaseHelper(this)
        val orderId = databaseHelper.createOrder(
            customerName = "Гость",
            items = itemsList,
            totalPrice = totalPrice
        )
        
        if (orderId > 0) {
            Toast.makeText(this, "Заказ #$orderId оформлен на сумму ${totalPrice.toInt()} Р!", Toast.LENGTH_LONG).show()
            
            // очищаю корзину после оформления заказа
            CartManager.clearCart()
            finish()
        } else {
            Toast.makeText(this, "Ошибка при создании заказа", Toast.LENGTH_SHORT).show()
        }
    }
}