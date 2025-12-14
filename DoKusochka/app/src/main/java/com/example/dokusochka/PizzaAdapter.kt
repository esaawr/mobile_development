package com.example.dokusochka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PizzaAdapter(
    private val pizzas: List<Pizza>,
    private val onPizzaClick: (Pizza) -> Unit
) : RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder>() {

    class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pizzaImage: ImageView = itemView.findViewById(R.id.pizzaImage)
        val pizzaTitle: TextView = itemView.findViewById(R.id.pizzaTitle)
        val pizzaDescription: TextView = itemView.findViewById(R.id.pizzaDescription)
        val priceButton: TextView = itemView.findViewById(R.id.priceButton)
        val quantityControls: LinearLayout = itemView.findViewById(R.id.quantityControls)
        val minusButton: TextView = itemView.findViewById(R.id.minusButton)
        val plusButton: TextView = itemView.findViewById(R.id.plusButton)
        val quantityText: TextView = itemView.findViewById(R.id.quantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pizza, parent, false)
        return PizzaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizza = pizzas[position]
        val context = holder.itemView.context

        // Установка данных
        holder.pizzaTitle.text = pizza.name
        holder.pizzaDescription.text = pizza.description
        holder.priceButton.text = "${pizza.price.toInt()} Р"

        // Загрузка изображения
        if (pizza.imagePath.isNotEmpty() && pizza.imagePath != "default_pizza") {
            val imageFile = File(context.getExternalFilesDir(null), pizza.imagePath)
            if (imageFile.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
                holder.pizzaImage.setImageBitmap(bitmap)
            }
        }

        // Проверка количества в корзине
        val currentQuantity = CartManager.getItemQuantity(pizza.id, "pizza")
        if (currentQuantity > 0) {
            holder.priceButton.visibility = TextView.GONE
            holder.quantityControls.visibility = LinearLayout.VISIBLE
            holder.quantityText.text = currentQuantity.toString()
        } else {
            holder.priceButton.visibility = TextView.VISIBLE
            holder.quantityControls.visibility = LinearLayout.GONE
            holder.quantityText.text = "1"
        }

        // Обработчики кликов
        holder.priceButton.setOnClickListener {
            holder.priceButton.visibility = TextView.GONE
            holder.quantityControls.visibility = LinearLayout.VISIBLE

            CartManager.addItem(CartItem(pizza.id, pizza.name, pizza.price, 1, "pizza"))
            holder.quantityText.text = CartManager.getItemQuantity(pizza.id, "pizza").toString()
            onPizzaClick(pizza)
            updateCartBadge(context)
        }

        holder.minusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(pizza.id, "pizza")
            if (currentQty > 0) {
                CartManager.updateQuantity(pizza.id, "pizza", currentQty - 1)
                val newQty = CartManager.getItemQuantity(pizza.id, "pizza")
                holder.quantityText.text = newQty.toString()

                if (newQty == 0) {
                    holder.priceButton.visibility = TextView.VISIBLE
                    holder.quantityControls.visibility = LinearLayout.GONE
                    holder.quantityText.text = "1"
                }
                updateCartBadge(context)
            }
        }

        holder.plusButton.setOnClickListener {
            val currentQty = CartManager.getItemQuantity(pizza.id, "pizza")
            CartManager.updateQuantity(pizza.id, "pizza", currentQty + 1)
            val newQty = CartManager.getItemQuantity(pizza.id, "pizza")
            holder.quantityText.text = newQty.toString()
            updateCartBadge(context)
        }
    }

    override fun getItemCount(): Int = pizzas.size

    private fun updateCartBadge(context: android.content.Context) {
        // Обновление бейджа корзины
        // В реальном приложении здесь нужно уведомить активность
        // или использовать LiveData/Flow
        Toast.makeText(context, "Корзина обновлена", Toast.LENGTH_SHORT).show()
    }
}