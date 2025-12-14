package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class AdminManagementActivity : AppCompatActivity() {

    private lateinit var clientsRecyclerView: RecyclerView
    private lateinit var clientsAdapter: ClientsAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_menegement)

        val menuManagementButton = findViewById<LinearLayout>(R.id.menuManagementsButton)
        val ordersButton = findViewById<LinearLayout>(R.id.ordersButton)
        val clientsReportButton = findViewById<LinearLayout>(R.id.clientsReportButton)
        val logoutButton = findViewById<LinearLayout>(R.id.logoutButton)

        // инициализация менеджера сессии
        sessionManager = SessionManager(this)

        // инициализация базы данных
        databaseHelper = DatabaseHelper(this)

        menuManagementButton.setOnClickListener {
            val intent = Intent(this, MenuManagementActivity::class.java)
            startActivity(intent)
        }

        ordersButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        clientsReportButton.setOnClickListener {
            showClientsReport()
        }

        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }

        // В методе onCreate после других кнопок
        val exportDataButton = findViewById<LinearLayout>(R.id.exportDataButton)
        exportDataButton.setOnClickListener {
            val intent = Intent(this, ExportActivity::class.java)
            startActivity(intent)
        }
        val deletePizzaButton = findViewById<LinearLayout>(R.id.deletePizzaButton)
        deletePizzaButton.setOnClickListener {
            val intent = Intent(this, DeletePizzaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Выход из администратора")
            .setMessage("Вы уверены, что хотите выйти из аккаунта администратора?")
            .setPositiveButton("Выйти") { dialog, which ->
                logoutAdmin()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun logoutAdmin() {
        // Очищаем данные сессии через SessionManager
        sessionManager.logout()

        // Дополнительно очищаем SharedPreferences (для уверенности)
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Очищаем корзину (если нужно)
        CartManager.clearCart()

        Toast.makeText(this, "Вы вышли из аккаунта администратора", Toast.LENGTH_SHORT).show()

        // Возвращаемся в меню с очисткой стека
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showClientsReport() {
        // получаю список клиентов из базы данных
        val clients = databaseHelper.getAllClients()

        if (clients.isEmpty()) {
            Toast.makeText(this, "Нет данных о клиентах", Toast.LENGTH_SHORT).show()
            return
        }

        // создаею диалог для отображения клиентов
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Отчет по клиентам")
            .setMessage("Всего клиентов: ${clients.size}")
            .setView(createClientsListView(clients))
            .setPositiveButton("Закрыть", null)  // Оставил только одну кнопку "Закрыть"
            .create()

        dialog.show()
    }

    private fun createClientsListView(clients: List<Client>): RecyclerView {
        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        clientsAdapter = ClientsAdapter(clients)
        recyclerView.adapter = clientsAdapter
        recyclerView.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return recyclerView
    }



    // Обработка кнопки "назад"
    override fun onBackPressed() {
        // При нажатии "назад" показываем диалог выхода
        showLogoutConfirmation()
    }
}