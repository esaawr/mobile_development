package com.example.dokusochka

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pizzeria.db"
        private const val DATABASE_VERSION = 1

        // Константы для таблиц и колонок
        const val TABLE_PIZZAS = "pizzas"
        const val TABLE_USERS = "users"
        const val TABLE_ADMINS = "admins"
        const val TABLE_ORDERS = "orders"
    }

    private val dbPath: String by lazy {
        context.getDatabasePath(DATABASE_NAME).absolutePath
    }

    init {
        // Проверяем и копируем БД из assets при первом запуске
        if (!checkDatabaseExists()) {
            val copied = copyDatabaseFromAssets(context)
            if (!copied) {
                // Если копирование не удалось, создаем новую БД
                println("⚠️ Не удалось скопировать БД из assets, создаем новую")
                writableDatabase // Это вызовет onCreate()
            }
        } else {
            // Проверяем, что таблицы существуют
            verifyDatabaseStructure()
        }
    }

    private fun checkDatabaseExists(): Boolean {
        val dbFile = File(dbPath)
        return dbFile.exists()
    }

    private fun copyDatabaseFromAssets(context: Context): Boolean {
        return try {
            // Открываем БД из assets/database
            val inputStream: InputStream = context.assets.open("database/$DATABASE_NAME")

            // Создаем папку databases если её нет
            val dbFile = File(dbPath)
            dbFile.parentFile?.mkdirs()

            // Копируем файл
            val outputStream: FileOutputStream = FileOutputStream(dbPath)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            println("✅ База данных успешно скопирована из assets")
            true
        } catch (e: IOException) {
            println("❌ Ошибка копирования БД: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun verifyDatabaseStructure() {
        try {
            val db = readableDatabase
            // Проверяем существование таблицы pizzas
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='pizzas'",
                null
            )
            val tableExists = cursor.count > 0
            cursor.close()

            if (!tableExists) {
                println("⚠️ Таблица pizzas не найдена, пересоздаем базу данных")
                db.close()
                // Удаляем поврежденную БД
                context.deleteDatabase(DATABASE_NAME)
                // Создаем новую БД
                writableDatabase
            } else {
                println("✅ База данных проверена, все таблицы на месте")
                db.close()
            }
        } catch (e: Exception) {
            println("❌ Ошибка проверки БД: ${e.message}")
            e.printStackTrace()
            // Пересоздаем БД
            context.deleteDatabase(DATABASE_NAME)
            writableDatabase
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создаем таблицы, если БД не была скопирована из assets
        println("📁 Создание таблиц базы данных")
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        // Таблица пользователей
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                name TEXT NOT NULL,
                phone TEXT,
                address TEXT
            )"""
        )

        // Таблица администраторов
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS $TABLE_ADMINS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                name TEXT NOT NULL
            )"""
        )

        // Таблица пицц
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS $TABLE_PIZZAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT,
                image_path TEXT,
                category TEXT DEFAULT 'pizza'
            )"""
        )

        // Таблица заказов
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS $TABLE_ORDERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_name TEXT NOT NULL,
                items_list TEXT NOT NULL,
                total_price REAL NOT NULL,
                order_date TEXT NOT NULL,
                status TEXT DEFAULT 'новый'
            )"""
        )

        // Добавляем администратора по умолчанию
        db.execSQL(
            """INSERT OR IGNORE INTO $TABLE_ADMINS (login, password, name) 
               VALUES ('admin', 'admin123', 'Администратор')"""
        )

        // Добавляем пиццы по умолчанию
        db.execSQL(
            """INSERT OR IGNORE INTO $TABLE_PIZZAS (id, name, price, description, image_path, category) 
               VALUES (1, '4 СЫРА', 289.0, 'МОЦАРЕЛЛА, СЫР ЧЕДДЕР И ПАРМЕЗАН, ФИРМЕННЫЙ СОУС АЛЬФРЕДО', 'pizza_4_cheese', 'pizza')"""
        )
        db.execSQL(
            """INSERT OR IGNORE INTO $TABLE_PIZZAS (id, name, price, description, image_path, category) 
               VALUES (2, 'ПЕППЕРОНИ', 289.0, 'ПИКАНТНАЯ ПЕППЕРОНИ, ПОРЦИЯ МОЦЦАРЕЛЛЫ, ФИРМЕННЫЙ ТОМАТНЫЙ СОУС', 'pizza_pepperoni', 'pizza')"""
        )
        db.execSQL(
            """INSERT OR IGNORE INTO $TABLE_PIZZAS (id, name, price, description, image_path, category) 
               VALUES (3, 'МЯСНАЯ', 289.0, 'ЦЫПЛЕНОК, ВЕТЧИНА, ПИКАНТНАЯ ПЕППЕРОНИ, КОЛБАСКИ ЧОРИЗО, ФИРМЕННЫЙ ТОМАТНЫЙ СОУС', 'pizza_meat', 'pizza')"""
        )

        println("✅ Таблицы созданы и данные добавлены")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // логика обновления БД
        println("🔄 Обновление БД с $oldVersion на $newVersion")
    }

    // Методы для работы с пиццами (ИСПРАВЛЕННЫЕ)
    fun getAllPizzas(): List<Pizza> {
        val pizzas = mutableListOf<Pizza>()
        val db = readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_PIZZAS ORDER BY name", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))

                    pizzas.add(Pizza(id, name, price, description, imagePath, category))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            println("❌ Ошибка при получении пицц: ${e.message}")
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }

        return pizzas
    }

    fun getPizzaByName(pizzaName: String): Pizza? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PIZZAS WHERE name = ?",
            arrayOf(pizzaName)
        )

        return try {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))

                Pizza(id, name, price, description, imagePath, category)
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ Ошибка при поиске пиццы: ${e.message}")
            e.printStackTrace()
            null
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun deletePizza(pizzaName: String): Boolean {
        val db = writableDatabase
        return try {
            val result = db.delete(TABLE_PIZZAS, "name = ?", arrayOf(pizzaName))
            db.close()
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            db.close()
            false
        }
    }

    fun addPizza(name: String, price: Double, description: String, imagePath: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("price", price)
            put("description", description)
            put("image_path", imagePath)
            put("category", "pizza")
        }
        val result = db.insert(TABLE_PIZZAS, null, values)
        db.close()
        return result != -1L
    }

    // Метод для получения всех клиентов
    fun getAllClients(): List<Client> {
        val clients = mutableListOf<Client>()
        val db = readableDatabase

        val query = "SELECT * FROM $TABLE_USERS ORDER BY name"
        val cursor: Cursor = db.rawQuery(query, null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val client = Client(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("login")), // используем login как email
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        registrationDate = "2024-01-01" // дата по умолчанию, т.к. в текущей схеме её нет
                    )
                    clients.add(client)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            println("❌ Ошибка при получении клиентов: ${e.message}")
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }

        return clients
    }

    fun getClientStatistics(): ClientStatistics {
        val db = readableDatabase

        val totalClientsQuery = "SELECT COUNT(*) FROM $TABLE_USERS"
        val totalClientsCursor = db.rawQuery(totalClientsQuery, null)
        val totalClients = if (totalClientsCursor.moveToFirst()) totalClientsCursor.getInt(0) else 0
        totalClientsCursor.close()

        val ordersQuery = "SELECT COUNT(DISTINCT customer_name) FROM $TABLE_ORDERS"
        val ordersCursor = db.rawQuery(ordersQuery, null)
        val clientsWithOrders = if (ordersCursor.moveToFirst()) ordersCursor.getInt(0) else 0
        ordersCursor.close()

        db.close()

        return ClientStatistics(totalClients, clientsWithOrders)
    }

    fun exportClientsToCsv(): File {
        val clients = getAllClients()
        val csvContent = StringBuilder()

        // Заголовок CSV
        csvContent.append("ID;Имя;Логин;Телефон;Адрес\n")

        // Данные клиентов
        clients.forEach { client ->
            // Получаем адрес из базы
            val db = readableDatabase
            val addressCursor = db.rawQuery(
                "SELECT address FROM $TABLE_USERS WHERE id = ?",
                arrayOf(client.id.toString())
            )
            val address = if (addressCursor.moveToFirst()) {
                addressCursor.getString(addressCursor.getColumnIndexOrThrow("address"))
            } else {
                ""
            }
            addressCursor.close()
            db.close()

            csvContent.append("${client.id};${client.name};${client.email};${client.phone};$address\n")
        }

        // Создаем файл
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "clients_$timestamp.csv"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toString().toByteArray(charset("Windows-1251")))
        }

        return file
    }

    // Экспорт заказов в CSV
    fun exportOrdersToCsv(): File {
        val orders = getAllOrders()
        val csvContent = StringBuilder()

        // Заголовок CSV
        csvContent.append("ID заказа;Клиент;Товары;Сумма (Р);Дата заказа;Статус\n")

        // Данные заказов
        orders.forEach { order ->
            csvContent.append("${order.id};${order.customerName};${order.itemsList.replace("\n", ", ")};${order.totalPrice.toInt()};${order.orderDate};${order.status}\n")
        }

        // Создаем файл
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "orders_$timestamp.csv"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toString().toByteArray(charset("Windows-1251")))
        }

        return file
    }

    // Экспорт меню (пицц) в CSV
    fun exportMenuToCsv(): File {
        val pizzas = getAllPizzas()
        val csvContent = StringBuilder()

        // Заголовок CSV
        csvContent.append("ID;Название;Цена (Р);Описание;Категория\n")

        // Данные пицц
        pizzas.forEach { pizza ->
            csvContent.append("${pizza.id};${pizza.name};${pizza.price.toInt()};${pizza.description};${pizza.category}\n")
        }

        // Создаем файл
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "menu_$timestamp.csv"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toString().toByteArray(charset("Windows-1251")))
        }

        return file
    }

    // Экспорт статистики в CSV
    fun exportStatisticsToCsv(): File {
        val orders = getAllOrders()
        val clients = getAllClients()
        val pizzas = getAllPizzas()

        val csvContent = StringBuilder()

        // Заголовок CSV
        csvContent.append("Категория;Значение\n")

        // Общая статистика
        csvContent.append("Всего клиентов;${clients.size}\n")
        csvContent.append("Всего заказов;${orders.size}\n")
        csvContent.append("Всего пицц в меню;${pizzas.size}\n")

        // Статистика по статусам заказов
        val statusGroups = orders.groupBy { it.status }
        statusGroups.forEach { (status, ordersList) ->
            csvContent.append("Заказов со статусом '$status';${ordersList.size}\n")
        }

        // Общая сумма заказов
        val totalRevenue = orders.sumOf { it.totalPrice }
        csvContent.append("Общая выручка (Р);${totalRevenue.toInt()}\n")

        // Средний чек
        val averageOrder = if (orders.isNotEmpty()) totalRevenue / orders.size else 0.0
        csvContent.append("Смердний чек (Р);${averageOrder.toInt()}\n")

        // Создаем файл
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val fileName = "statistics_$timestamp.csv"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toString().toByteArray(charset("Windows-1251")))
        }

        return file
    }

    // метод для получения клиента по ID
    fun getClientById(id: Int): Client? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        return try {
            if (cursor.moveToFirst()) {
                Client(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("login")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    registrationDate = "2024-01-01"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun addUser(login: String, password: String, name: String, phone: String, address: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("login", login)
            put("password", password)
            put("name", name)
            put("phone", phone)
            put("address", address)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun authenticateUser(login: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE login = ? AND password = ?",
            arrayOf(login, password)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkUserExists(login: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE login = ?",
            arrayOf(login)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun authenticateAdmin(login: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ADMINS WHERE login = ? AND password = ?",
            arrayOf(login, password)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getUserDetails(login: String, password: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, name, phone, address FROM $TABLE_USERS WHERE login = ? AND password = ?",
            arrayOf(login, password)
        )

        val result = if (cursor.moveToFirst()) {
            mapOf(
                "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")).toString(),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow("name")),
                "phone" to cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                "address" to cursor.getString(cursor.getColumnIndexOrThrow("address"))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return result
    }

    // методы для работы с заказами
    fun createOrder(customerName: String, items: String, totalPrice: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("customer_name", customerName)
            put("items_list", items)
            put("total_price", totalPrice)
            put("order_date", getCurrentDateTime())
            put("status", "новый")
        }
        val result = db.insert(TABLE_ORDERS, null, values)
        db.close()
        return result
    }

    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ORDERS ORDER BY id DESC", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val order = Order(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name")),
                        itemsList = cursor.getString(cursor.getColumnIndexOrThrow("items_list")),
                        totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                        orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date")),
                        status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    )
                    orders.add(order)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            println("❌ Ошибка при получении заказов: ${e.message}")
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }

        return orders
    }

    fun updateOrderStatus(orderId: Int, newStatus: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", newStatus)
        }
        val result = db.update(TABLE_ORDERS, values, "id = ?", arrayOf(orderId.toString()))
        db.close()
        return result > 0
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}

// классы данных
data class Client(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val registrationDate: String
)

data class ClientStatistics(
    val totalClients: Int,
    val clientsWithOrders: Int
)

data class Pizza(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imagePath: String,
    val category: String = "pizza"
)

data class Order(
    val id: Int,
    val customerName: String,
    val itemsList: String,
    val totalPrice: Double,
    val orderDate: String,
    val status: String
)

data class UserData(
    val id: Int,
    val name: String,
    val phone: String,
    val address: String
)

data class UserFullInfo(
    val id: Int,
    val name: String,
    val phone: String,
    val address: String
)