package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MenuSnacksActivity : AppCompatActivity() {

    // ID закусок
    private val FRIES_ID = 201
    private val NUGGETS_ID = 202
    private val SALAD_ID = 203

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_s)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // обновляю отображение корзины при возвращении на экран
        updateCartBadge()
        // обновляяю отображение количества закусок
        updateSnacksDisplay()
    }

    private fun setupClickListeners() {
        // Иконка профиля/меню
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

        // вкладки для перехода
        setupTabs()

        // обработчики для закусок
        setupSnacksButtons()
    }

    private fun setupTabs() {
        //  НАПИТКИ
        val drinksTab = findViewById<RelativeLayout>(R.id.drinksTab)
        drinksTab?.setOnClickListener {
            val intent = Intent(this, MenuDrinksActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  ЗАКУСКИ
        val snacksTab = findViewById<RelativeLayout>(R.id.snacksTab)
        snacksTab?.setOnClickListener {

        }

        //  ПИЦЦА
        val pizzaTab = findViewById<RelativeLayout>(R.id.pizzaTab)
        pizzaTab?.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSnacksButtons() {
        // картошка фри
        setupSnackControls(
            FRIES_ID,
            findViewById(R.id.friesPriceButton),
            findViewById(R.id.friesQuantityControls),
            findViewById(R.id.friesMinusButton),
            findViewById(R.id.friesPlusButton),
            findViewById(R.id.friesQuantityText),
            "Картофель фри",
            129.0
        )

        // куриные наггетсы
        setupSnackControls(
            NUGGETS_ID,
            findViewById(R.id.nuggetsPriceButton),
            findViewById(R.id.nuggetsQuantityControls),
            findViewById(R.id.nuggetsMinusButton),
            findViewById(R.id.nuggetsPlusButton),
            findViewById(R.id.nuggetsQuantityText),
            "Куриные наггетсы",
            179.0
        )

        // салат Цезарь
        setupSnackControls(
            SALAD_ID,
            findViewById(R.id.saladPriceButton),
            findViewById(R.id.saladQuantityControls),
            findViewById(R.id.saladMinusButton),
            findViewById(R.id.saladPlusButton),
            findViewById(R.id.saladQuantityText),
            "Салат Цезарь",
            199.0
        )
    }

    private fun updateSnacksDisplay() {
        // картошка фри
        updateSnackDisplay(
            FRIES_ID,
            findViewById(R.id.friesPriceButton),
            findViewById(R.id.friesQuantityControls),
            findViewById(R.id.friesQuantityText)
        )

        // куриные наггетсы
        updateSnackDisplay(
            NUGGETS_ID,
            findViewById(R.id.nuggetsPriceButton),
            findViewById(R.id.nuggetsQuantityControls),
            findViewById(R.id.nuggetsQuantityText)
        )

        // салат Цезарь
        updateSnackDisplay(
            SALAD_ID,
            findViewById(R.id.saladPriceButton),
            findViewById(R.id.saladQuantityControls),
            findViewById(R.id.saladQuantityText)
        )
    }

    private fun updateSnackDisplay(
        snackId: Int,
        priceButton: TextView,
        quantityControls: LinearLayout,
        quantityText: TextView
    ) {
        val currentQuantity = CartManager.getItemQuantity(snackId, "snack")
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

    private fun setupSnackControls(
        snackId: Int,
        priceButton: TextView,
        quantityControls: LinearLayout,
        minusButton: TextView,
        plusButton: TextView,
        quantityText: TextView,
        snackName: String,
        snackPrice: Double
    ) {
        // проверка, есть ли уже эта закуска в общей корзине
        val currentQuantity = CartManager.getItemQuantity(snackId, "snack")
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

            // добавляю закуску в общую корзину
            CartManager.addItem(CartItem(snackId, snackName, snackPrice, 1, "snack"))

            // обновляю отображение количества
            quantityText.text = CartManager.getItemQuantity(snackId, "snack").toString()
            updateCartBadge()
        }

        minusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(snackId, "snack")
            if (currentQty > 0) {
                CartManager.updateQuantity(snackId, "snack", currentQty - 1)
                val newQty = CartManager.getItemQuantity(snackId, "snack")
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
            val currentQty = CartManager.getItemQuantity(snackId, "snack")
            CartManager.updateQuantity(snackId, "snack", currentQty + 1)
            quantityText.text = CartManager.getItemQuantity(snackId, "snack").toString()
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
        //  TextView для карточки корзины
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