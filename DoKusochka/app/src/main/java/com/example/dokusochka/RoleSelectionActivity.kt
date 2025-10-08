package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout

class RoleSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val clientButton = findViewById<RelativeLayout>(R.id.clientButton)
        val adminButton = findViewById<RelativeLayout>(R.id.adminButton)

        clientButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        adminButton.setOnClickListener {
            val intent = Intent(this, AdminAuthActivity::class.java)
            startActivity(intent)
        }
    }
}