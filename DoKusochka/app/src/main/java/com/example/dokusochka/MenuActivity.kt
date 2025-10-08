package com.example.dokusochka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MenuActivity : AppCompatActivity() {
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
            // будет переход к корзине

        }
    }
}