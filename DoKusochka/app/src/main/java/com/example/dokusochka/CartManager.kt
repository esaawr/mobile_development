package com.example.dokusochka

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existingItem = cartItems.find { it.id == item.id && it.type == item.type }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            cartItems.add(item)
        }
    }

    fun removeItem(id: Int, type: String) {
        cartItems.removeAll { it.id == id && it.type == type }
    }

    fun updateQuantity(id: Int, type: String, quantity: Int) {
        val item = cartItems.find { it.id == id && it.type == type }
        item?.let {
            if (quantity > 0) {
                it.quantity = quantity
            } else {
                removeItem(id, type)
            }
        }
    }

    fun getItemQuantity(id: Int, type: String): Int {
        return cartItems.find { it.id == id && it.type == type }?.quantity ?: 0
    }

    fun getItems(): List<CartItem> {
        return cartItems.toList()
    }

    fun getTotalCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }

    // доп метод для отладки
    fun printCartContents() {
        println("=== Содержимое корзины ===")
        cartItems.forEach { item ->
            println("${item.name} (${item.type}): ${item.quantity} x ${item.price} Р")
        }
        println("Итого: ${getTotalCount()} товаров на сумму ${getTotalPrice()} Р")
        println("=========================")
    }
}

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
    val type: String
)