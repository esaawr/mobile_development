package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MenuDrinksActivity : AppCompatActivity() {

    // ID напитков
    private val SPRITE_ID = 101
    private val COLA_ID = 102
    private val FANTA_ID = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_n)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // обновляю отображение корзины при возвращении на экран
        updateCartBadge()
        // обновляю отображение количества напитков
        updateDrinksDisplay()
    }

    private fun setupClickListeners() {
        // иконка профиля/меню
        val profileIcon = findViewById<ImageView>(R.id.menuIcon)
        profileIcon.setOnClickListener {
            val sessionManager = SessionManager(this)
            if (sessionManager.isLoggedIn()) {
                val intent = Intent(this, ClientProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, ClientAuthActivity::class.java)
                startActivity(intent)
            }
        }

        // иконка корзины
        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener {
            openCart()
        }

        // вкладка для перехода
        setupTabs()

        // обработчики для напитков
        setupDrinksButtons()
    }

    private fun setupTabs() {
        //  ЗАКУСКИ
        val snacksTab = findViewById<RelativeLayout>(R.id.comboTab) // ID в layout - comboTab, но это закуски
        snacksTab?.setOnClickListener {
            val intent = Intent(this, MenuSnacksActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  НАПИТКИ
        val drinksTab = findViewById<RelativeLayout>(R.id.drinksTab)
        drinksTab?.setOnClickListener {
            // Уже на этой вкладке
        }

        //  ПИЦЦА
        val pizzaTab = findViewById<RelativeLayout>(R.id.pizzaTab)
        pizzaTab?.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupDrinksButtons() {
        // Sprite
        setupDrinkControls(
            SPRITE_ID,
            findViewById(R.id.spritePriceButton),
            findViewById(R.id.spriteQuantityControls),
            findViewById(R.id.spriteMinusButton),
            findViewById(R.id.spritePlusButton),
            findViewById(R.id.spriteQuantityText),
            "Sprite",
            100.0
        )

        // Cola
        setupDrinkControls(
            COLA_ID,
            findViewById(R.id.colaPriceButton),
            findViewById(R.id.colaQuantityControls),
            findViewById(R.id.colaMinusButton),
            findViewById(R.id.colaPlusButton),
            findViewById(R.id.colaQuantityText),
            "Coca Cola",
            150.0
        )

        // Fanta
        setupDrinkControls(
            FANTA_ID,
            findViewById(R.id.fantaPriceButton),
            findViewById(R.id.fantaQuantityControls),
            findViewById(R.id.fantaMinusButton),
            findViewById(R.id.fantaPlusButton),
            findViewById(R.id.fantaQuantityText),
            "Fanta",
            100.0
        )
    }

    private fun updateDrinksDisplay() {
        // Sprite
        updateDrinkDisplay(
            SPRITE_ID,
            findViewById(R.id.spritePriceButton),
            findViewById(R.id.spriteQuantityControls),
            findViewById(R.id.spriteQuantityText)
        )

        // Cola
        updateDrinkDisplay(
            COLA_ID,
            findViewById(R.id.colaPriceButton),
            findViewById(R.id.colaQuantityControls),
            findViewById(R.id.colaQuantityText)
        )

        // Fanta
        updateDrinkDisplay(
            FANTA_ID,
            findViewById(R.id.fantaPriceButton),
            findViewById(R.id.fantaQuantityControls),
            findViewById(R.id.fantaQuantityText)
        )
    }

    private fun updateDrinkDisplay(
        drinkId: Int,
        priceButton: TextView,
        quantityControls: LinearLayout,
        quantityText: TextView
    ) {
        val currentQuantity = CartManager.getItemQuantity(drinkId, "drink")
        if (currentQuantity > 0) {
            priceButton.visibility = TextView.GONE
            quantityControls.visibility = LinearLayout.VISIBLE
            quantityText.text = currentQuantity.toString()
        } else {
            priceButton.visibility = TextView.VISIBLE
            quantityControls.visibility = LinearLayout.GONE
            quantityText.text = "1"
        }
    }

    private fun setupDrinkControls(
        drinkId: Int,
        priceButton: TextView,
        quantityControls: LinearLayout,
        minusButton: TextView,
        plusButton: TextView,
        quantityText: TextView,
        drinkName: String,
        drinkPrice: Double
    ) {
        // проверяю, есть ли уже этот напиток в общей корзине
        val currentQuantity = CartManager.getItemQuantity(drinkId, "drink")
        if (currentQuantity > 0) {
            priceButton.visibility = TextView.GONE
            quantityControls.visibility = LinearLayout.VISIBLE
            quantityText.text = currentQuantity.toString()
        } else {
            priceButton.visibility = TextView.VISIBLE
            quantityControls.visibility = LinearLayout.GONE
            quantityText.text = "1"
        }

        priceButton.setOnClickListener {
            priceButton.visibility = TextView.GONE
            quantityControls.visibility = LinearLayout.VISIBLE

            // добавляю напиток в общую корзину
            CartManager.addItem(CartItem(drinkId, drinkName, drinkPrice, 1, "drink"))

            // обновляю отображение количества
            quantityText.text = CartManager.getItemQuantity(drinkId, "drink").toString()
            updateCartBadge()
        }

        minusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(drinkId, "drink")
            if (currentQty > 0) {
                CartManager.updateQuantity(drinkId, "drink", currentQty - 1)
                val newQty = CartManager.getItemQuantity(drinkId, "drink")
                quantityText.text = newQty.toString()

                if (newQty == 0) {
                    priceButton.visibility = TextView.VISIBLE
                    quantityControls.visibility = LinearLayout.GONE
                    quantityText.text = "1"
                }
                updateCartBadge()
            }
        }

        plusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(drinkId, "drink")
            CartManager.updateQuantity(drinkId, "drink", currentQty + 1)
            quantityText.text = CartManager.getItemQuantity(drinkId, "drink").toString()
            updateCartBadge()
        }
    }

    private fun openCart() {
        if (CartManager.getTotalCount() == 0) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun updateCartBadge() {

        val cartBadge = findViewById<TextView?>(R.id.cartBadge)
        cartBadge?.let {
            val totalCount = CartManager.getTotalCount()
            if (totalCount > 0) {
                it.text = totalCount.toString()
                it.visibility = android.view.View.VISIBLE
            } else {
                it.visibility = android.view.View.GONE
            }
        }
    }
}