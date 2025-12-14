package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import java.io.File

class MenuActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var pizzasFromDb = listOf<Pizza>()
    private lateinit var sessionManager: SessionManager
    private lateinit var pizzasContainer: LinearLayout

    companion object {
        private const val TAG = "MenuActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация менеджера сессии
        sessionManager = SessionManager(this)
        Log.d(TAG, "SessionManager инициализирован")
        Log.d(TAG, "Пользователь авторизован: ${sessionManager.isLoggedIn()}")
        Log.d(TAG, "Имя пользователя: ${sessionManager.getUserName()}")
        Log.d(TAG, "Логин пользователя: ${sessionManager.getUserLogin()}")

        // Если пользователь авторизован и является администратором, перенаправляем его
        if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
            Log.d(TAG, "Пользователь является администратором, перенаправляем в AdminManagementActivity")
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Если не администратор, продолжаем обычный процесс
        setContentView(R.layout.menu)

        Log.d(TAG, "=== MenuActivity создается ===")

        // Инициализация БД
        databaseHelper = DatabaseHelper(this)
        pizzasContainer = findViewById(R.id.pizzasContainer)

        // Загружаем пиццы из БД
        loadPizzasFromDatabase()

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        menuIcon.setOnClickListener {
            Log.d(TAG, "Нажата иконка профиля")
            Log.d(TAG, "Статус авторизации: ${sessionManager.isLoggedIn()}")

            try {
                // Проверяем авторизацию
                if (sessionManager.isLoggedIn()) {
                    Log.d(TAG, "Пользователь авторизован, открываем профиль")

                    // Проверяем, является ли администратором
                    if (sessionManager.isAdmin()) {
                        Log.d(TAG, "Администратор, открываем AdminManagementActivity")
                        val intent = Intent(this, AdminManagementActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Обычный пользователь авторизован - открываем профиль
                        val intent = Intent(this, ClientProfileActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Log.d(TAG, "Пользователь не авторизован, открываем авторизацию")

                    // Пользователь не авторизован - открываем авторизацию
                    val intent = Intent(this, ClientAuthActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при переходе в профиль/авторизацию", e)
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()

                // В случае ошибки всегда открываем авторизацию
                val intent = Intent(this, ClientAuthActivity::class.java)
                startActivity(intent)
            }
        }

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener {
            openCart()
        }

        // обработчики для кнопок пицц
        setupPizzaButtons()
        setupTabs()


        Log.d(TAG, "=== MenuActivity создан успешно ===")
    }

    private fun loadPizzasFromDatabase() {
        pizzasFromDb = databaseHelper.getAllPizzas()
        Log.d(TAG, "Загружено пицц из БД: ${pizzasFromDb.size}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MenuActivity onResume")

        // ПРОВЕРКА ПРИ ВОЗВРАЩЕНИИ: если администратор - перенаправляем
        if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
            Log.d(TAG, "Администратор вернулся, перенаправляем в AdminManagementActivity")
            val intent = Intent(this, AdminManagementActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Обновляем данные из БД
        pizzasFromDb = databaseHelper.getAllPizzas()
        Log.d(TAG, "Загружено пицц из БД: ${pizzasFromDb.size}")

        // Обновляем все элементы интерфейса пицц
        updateAllPizzaViews()

        // обновление отображения корзины при возвращении на экран
        updateCartBadge()
        // обновление отображения количества пицц
        updatePizzasDisplay()
        // Обновляем иконку профиля (на случай выхода)


        Log.d(TAG, "Иконка профиля обновлена. Авторизован: ${sessionManager.isLoggedIn()}, Админ: ${sessionManager.isAdmin()}")
    }



    private fun setupTabs() {
        val drinksTab = findViewById<RelativeLayout>(R.id.drinksTab)
        drinksTab?.setOnClickListener {
            Log.d(TAG, "Переход к напиткам")
            val intent = Intent(this, MenuDrinksActivity::class.java)
            startActivity(intent)
            finish()
        }

        // обработчик для закусок
        val snacksTab = findViewById<RelativeLayout>(R.id.snacksTab)
        snacksTab?.setOnClickListener {
            Log.d(TAG, "Переход к закускам")
            val intent = Intent(this, MenuSnacksActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupPizzaButtons() {
        Log.d(TAG, "Настройка кнопок пицц. Количество в БД: ${pizzasFromDb.size}")

        // Очищаем контейнер
        pizzasContainer.removeAllViews()

        // Динамически создаем элементы для каждой пиццы
        pizzasFromDb.forEachIndexed { index, pizza ->
            createPizzaItemView(pizza, index + 1)
        }

        Log.d(TAG, "Используются все пиццы из БД: ${pizzasFromDb.size}")
    }

    private fun createPizzaItemView(pizza: Pizza, pizzaNumber: Int) {
        // Создаем макет из layout-файла или программно
        val pizzaItemView = layoutInflater.inflate(R.layout.pizza_item_layout, null)

        // Находим элементы
        val pizzaImage = pizzaItemView.findViewById<ImageView>(R.id.pizzaImage)
        val pizzaTitle = pizzaItemView.findViewById<TextView>(R.id.pizzaTitle)
        val pizzaDescription = pizzaItemView.findViewById<TextView>(R.id.pizzaDescription)
        val priceButton = pizzaItemView.findViewById<TextView>(R.id.priceButton)
        val quantityControls = pizzaItemView.findViewById<LinearLayout>(R.id.quantityControls)
        val minusButton = pizzaItemView.findViewById<TextView>(R.id.minusButton)
        val plusButton = pizzaItemView.findViewById<TextView>(R.id.plusButton)
        val quantityText = pizzaItemView.findViewById<TextView>(R.id.quantityText)

        // Заполняем данными
        pizzaTitle.text = pizza.name
        pizzaDescription.text = pizza.description
        priceButton.text = "${pizza.price.toInt()} Р"

        // Загружаем изображение
        updatePizzaImageDynamic(pizzaImage, pizza.imagePath)

        // Настраиваем контролы
        setupPizzaControls(
            pizza,
            priceButton,
            quantityControls,
            minusButton,
            plusButton,
            quantityText
        )

        // Добавляем в контейнер
        pizzasContainer.addView(pizzaItemView)
    }

    private fun updatePizzaImageDynamic(imageView: ImageView, imagePath: String) {
        try {
            if (imagePath.isNotEmpty() && imagePath != "default_pizza") {
                val imageFile = File(getExternalFilesDir(null), imagePath)
                if (imageFile.exists()) {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
                    imageView.setImageBitmap(bitmap)
                    Log.d(TAG, "Загружено изображение: ${imageFile.absolutePath}")
                } else {
                    // Пробуем загрузить из ресурсов
                    val resourceId = resources.getIdentifier(imagePath, "drawable", packageName)
                    if (resourceId != 0) {
                        imageView.setImageResource(resourceId)
                        Log.d(TAG, "Загружено изображение из ресурсов: $imagePath")
                    } else {
                        // Используем изображение по умолчанию
                        imageView.setImageResource(R.drawable.default_pizza)
                    }
                }
            } else {
                // Используеn изображение по умолчанию
                imageView.setImageResource(R.drawable.default_pizza)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки изображения: ${e.message}")
            imageView.setImageResource(R.drawable.default_pizza)
        }
    }

    private fun updatePizzasDisplay() {
        // lля каждой пиццы обновлtybt отображение
        pizzasFromDb.forEachIndexed { index, pizza ->
            updatePizzaDisplayDynamic(pizza, index + 1)
        }
    }

    private fun updatePizzaDisplayDynamic(pizza: Pizza, pizzaNumber: Int) {
        // boen соответствующий View по тегу или индексу
        if (pizzaNumber - 1 < pizzasContainer.childCount) {
            val pizzaView = pizzasContainer.getChildAt(pizzaNumber - 1)
            val priceButton = pizzaView.findViewById<TextView>(R.id.priceButton)
            val quantityControls = pizzaView.findViewById<LinearLayout>(R.id.quantityControls)
            val quantityText = pizzaView.findViewById<TextView>(R.id.quantityText)

            val currentQuantity = CartManager.getItemQuantity(pizza.id, "pizza")
            if (currentQuantity > 0) {
                priceButton.visibility = View.GONE
                quantityControls.visibility = View.VISIBLE
                quantityText.text = currentQuantity.toString()
            } else {
                priceButton.visibility = View.VISIBLE
                quantityControls.visibility = View.GONE
                quantityText.text = "1"
            }
        }
    }

    private fun setupPizzaControls(
        pizza: Pizza,
        priceButton: TextView,
        quantityControls: LinearLayout,
        minusButton: TextView,
        plusButton: TextView,
        quantityText: TextView
    ) {
        // установка цены из БД
        priceButton.text = "${pizza.price.toInt()} Р"

        // проверка, есть ли уже эта пицца в общей корзине
        val currentQuantity = CartManager.getItemQuantity(pizza.id, "pizza")
        if (currentQuantity > 0) {
            priceButton.visibility = View.GONE
            quantityControls.visibility = View.VISIBLE
            quantityText.text = currentQuantity.toString()
        } else {
            priceButton.visibility = View.VISIBLE
            quantityControls.visibility = View.GONE
            quantityText.text = "1"
        }

        // обработчик кнопки цены
        priceButton.setOnClickListener {
            // скрывает кнопку цены, показывает управление количеством
            priceButton.visibility = View.GONE
            quantityControls.visibility = View.VISIBLE

            // добавляет пиццу в общую корзину
            CartManager.addItem(CartItem(pizza.id, pizza.name, pizza.price, 1, "pizza"))

            // обновляет отображение количества
            quantityText.text = CartManager.getItemQuantity(pizza.id, "pizza").toString()
            updateCartBadge()

            // уведомление о добавлении
            Toast.makeText(this, "Добавлено: ${pizza.name}", Toast.LENGTH_SHORT).show()
        }

        // обработчик кнопки минус
        minusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(pizza.id, "pizza")
            if (currentQty > 0) {
                CartManager.updateQuantity(pizza.id, "pizza", currentQty - 1)
                val newQty = CartManager.getItemQuantity(pizza.id, "pizza")
                quantityText.text = newQty.toString()

                if (newQty == 0) {
                    priceButton.visibility = View.VISIBLE
                    quantityControls.visibility = View.GONE
                    quantityText.text = "1"
                    Toast.makeText(this, "Удалено: ${pizza.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Количество уменьшено: ${pizza.name} x$newQty", Toast.LENGTH_SHORT).show()
                }
                updateCartBadge()
            }
        }

        // обработчик кнопки плюс
        plusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(pizza.id, "pizza")
            CartManager.updateQuantity(pizza.id, "pizza", currentQty + 1)
            val newQty = CartManager.getItemQuantity(pizza.id, "pizza")
            quantityText.text = newQty.toString()
            updateCartBadge()
            Toast.makeText(this, "Добавлено: ${pizza.name} x$newQty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAllPizzaViews() {
        // Очищаем и пересоздаем все View
        setupPizzaButtons()
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
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }
}