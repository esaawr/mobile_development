package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast

class AdminManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_menegement)

        val menuManagementButton = findViewById<LinearLayout>(R.id.menuManagementsButton)
        val ordersButton = findViewById<LinearLayout>(R.id.ordersButton)
        val clientsReportButton = findViewById<LinearLayout>(R.id.clientsReportButton)
        val logoutButton = findViewById<LinearLayout>(R.id.logoutButton)

        menuManagementButton.setOnClickListener {
            Toast.makeText(this, "Управление меню", Toast.LENGTH_SHORT).show()
            // будет переход к управлению меню

        }

        ordersButton.setOnClickListener {
            Toast.makeText(this, "Просмотр заказов", Toast.LENGTH_SHORT).show()
            //будет переход к просмотру заказов

        }

        clientsReportButton.setOnClickListener {
            Toast.makeText(this, "Отчет по клиентам", Toast.LENGTH_SHORT).show()
            //будет переход к отчетам

        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}