package com.example.dokusochka

import org.junit.Test
import org.junit.Assert.*

/**
 * Business logic tests
 */
class BusinessLogicTest {

    @Test
    fun orderTotal_multipleItems_calculatedCorrectly() {
        // Simulate creating an order
        val items = listOf(
            CartItem(1, "Пепперони", 289.0, 2, "pizza"),
            CartItem(2, "Маргарита", 259.0, 1, "pizza"),
            CartItem(3, "Кола", 99.0, 2, "drink")
        )

        val totalPrice = items.sumOf { it.price * it.quantity }

        // 289*2 + 259*1 + 99*2 = 578 + 259 + 198 = 1035
        assertEquals(1035.0, totalPrice, 0.01)
    }

    @Test
    fun orderItemsList_formatting() {
        // Test formatting order items list
        val items = listOf(
            CartItem(1, "Пепперони", 289.0, 2, "pizza"),
            CartItem(2, "Кола", 99.0, 1, "drink")
        )

        val itemsList = items.joinToString(", ") { "${it.name} x${it.quantity}" }

        assertEquals("Пепперони x2, Кола x1", itemsList)
    }

    @Test
    fun cart_addMultipleItemsOfSameType_aggregates() {
        CartManager.clearCart()

        // Customer adds pizza multiple times
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 1, "pizza"))
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 1, "pizza"))
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 1, "pizza"))

        assertEquals(1, CartManager.getItems().size)
        assertEquals(3, CartManager.getItemQuantity(1, "pizza"))

        CartManager.clearCart()
    }

    @Test
    fun cart_differentPricesForSameId_shouldNotMerge() {
        CartManager.clearCart()

        // This shouldn't happen in real scenario, but testing edge case
        val item1 = CartItem(1, "Item", 100.0, 1, "pizza")
        val item2 = CartItem(1, "Item", 200.0, 1, "drink")

        CartManager.addItem(item1)
        CartManager.addItem(item2)

        // They have different types, so they should be separate
        assertEquals(2, CartManager.getItems().size)

        CartManager.clearCart()
    }

    @Test
    fun orderStatus_transitions_valid() {
        // Test order status transitions
        val statuses = listOf("новый", "в обработке", "выполнен")

        // Simulate order lifecycle
        var order = Order(1, "Customer", "items", 500.0, "2024-01-01", "новый")
        assertEquals("новый", order.status)

        order = order.copy(status = "в обработке")
        assertEquals("в обработке", order.status)

        order = order.copy(status = "выполнен")
        assertEquals("выполнен", order.status)
    }

    @Test
    fun clientStatistics_percentageCalculation() {
        // Test statistics calculation
        val stats = ClientStatistics(100, 45)

        val percentage = (stats.clientsWithOrders.toDouble() / stats.totalClients.toDouble()) * 100

        assertEquals(45.0, percentage, 0.01)
    }

    @Test
    fun pizza_priceComparison_findCheapest() {
        // Test finding cheapest pizza
        val pizzas = listOf(
            Pizza(1, "Expensive", 500.0, "desc", "path"),
            Pizza(2, "Medium", 300.0, "desc", "path"),
            Pizza(3, "Cheap", 200.0, "desc", "path")
        )

        val cheapest = pizzas.minByOrNull { it.price }

        assertNotNull(cheapest)
        assertEquals("Cheap", cheapest?.name)
        assertEquals(200.0, cheapest?.price)
    }

    @Test
    fun pizza_priceComparison_findMostExpensive() {
        // Test finding most expensive pizza
        val pizzas = listOf(
            Pizza(1, "Expensive", 500.0, "desc", "path"),
            Pizza(2, "Medium", 300.0, "desc", "path"),
            Pizza(3, "Cheap", 200.0, "desc", "path")
        )

        val expensive = pizzas.maxByOrNull { it.price }

        assertNotNull(expensive)
        assertEquals("Expensive", expensive?.name)
        assertEquals(500.0, expensive?.price)
    }

    @Test
    fun cart_discountApplication() {
        // Test applying discount
        CartManager.clearCart()

        CartManager.addItem(CartItem(1, "Pizza", 1000.0, 1, "pizza"))

        val originalPrice = CartManager.getTotalPrice()
        val discountPercent = 10
        val finalPrice = originalPrice * (100 - discountPercent) / 100

        assertEquals(1000.0, originalPrice, 0.01)
        assertEquals(900.0, finalPrice, 0.01)

        CartManager.clearCart()
    }

    @Test
    fun cart_minimumOrderAmount_validation() {
        // Test minimum order validation
        CartManager.clearCart()

        CartManager.addItem(CartItem(1, "Cheap", 50.0, 1, "pizza"))

        val total = CartManager.getTotalPrice()
        val minimumOrder = 200.0

        assertTrue(total < minimumOrder)

        CartManager.clearCart()
    }

    @Test
    fun order_itemsCount_validation() {
        // Test that order has items
        val items = listOf(
            CartItem(1, "Pizza", 289.0, 1, "pizza")
        )

        assertTrue(items.isNotEmpty())
        assertTrue(items.sumOf { it.quantity } > 0)
    }

    @Test
    fun cart_taxCalculation() {
        // Test tax calculation (if applicable)
        CartManager.clearCart()

        CartManager.addItem(CartItem(1, "Pizza", 100.0, 1, "pizza"))

        val subtotal = CartManager.getTotalPrice()
        val taxRate = 0.20 // 20% VAT
        val tax = subtotal * taxRate
        val total = subtotal + tax

        assertEquals(100.0, subtotal, 0.01)
        assertEquals(20.0, tax, 0.01)
        assertEquals(120.0, total, 0.01)

        CartManager.clearCart()
    }

    @Test
    fun client_ordersHistory_filtering() {
        // Test filtering orders by customer
        val orders = listOf(
            Order(1, "Иван", "items", 500.0, "2024-01-01", "новый"),
            Order(2, "Петр", "items", 600.0, "2024-01-02", "новый"),
            Order(3, "Иван", "items", 700.0, "2024-01-03", "выполнен")
        )

        val ivanOrders = orders.filter { it.customerName == "Иван" }

        assertEquals(2, ivanOrders.size)
        assertTrue(ivanOrders.all { it.customerName == "Иван" })
    }

    @Test
    fun orders_totalRevenue_calculation() {
        // Test calculating total revenue
        val orders = listOf(
            Order(1, "Customer1", "items", 500.0, "2024-01-01", "выполнен"),
            Order(2, "Customer2", "items", 600.0, "2024-01-02", "выполнен"),
            Order(3, "Customer3", "items", 700.0, "2024-01-03", "отменен")
        )

        val completedOrders = orders.filter { it.status == "выполнен" }
        val totalRevenue = completedOrders.sumOf { it.totalPrice }

        assertEquals(1100.0, totalRevenue, 0.01)
    }

    @Test
    fun cart_bulkUpdate_efficiency() {
        // Test bulk operations
        CartManager.clearCart()

        val itemsToAdd = (1..10).map { 
            CartItem(it, "Item $it", 100.0, 1, "pizza") 
        }

        itemsToAdd.forEach { CartManager.addItem(it) }

        assertEquals(10, CartManager.getItems().size)
        assertEquals(10, CartManager.getTotalCount())
        assertEquals(1000.0, CartManager.getTotalPrice(), 0.01)

        CartManager.clearCart()
    }

    @Test
    fun pizza_categoryGrouping() {
        // Test grouping pizzas by category
        val items = listOf(
            Pizza(1, "Pepperoni", 289.0, "desc", "path", "pizza"),
            Pizza(2, "Margarita", 259.0, "desc", "path", "pizza"),
            Pizza(3, "Cola", 99.0, "desc", "path", "drink"),
            Pizza(4, "Chips", 79.0, "desc", "path", "snack")
        )

        val grouped = items.groupBy { it.category }

        assertEquals(3, grouped.size)
        assertEquals(2, grouped["pizza"]?.size)
        assertEquals(1, grouped["drink"]?.size)
        assertEquals(1, grouped["snack"]?.size)
    }

    @Test
    fun order_dateRange_filtering() {
        // Test filtering orders by date range
        val orders = listOf(
            Order(1, "Customer", "items", 500.0, "2024-01-01 10:00:00", "новый"),
            Order(2, "Customer", "items", 600.0, "2024-01-15 12:00:00", "новый"),
            Order(3, "Customer", "items", 700.0, "2024-02-01 14:00:00", "новый")
        )

        // Filter January orders
        val januaryOrders = orders.filter { it.orderDate.startsWith("2024-01") }

        assertEquals(2, januaryOrders.size)
    }

    @Test
    fun cart_averageItemPrice() {
        // Test calculating average item price
        CartManager.clearCart()

        CartManager.addItem(CartItem(1, "Item1", 100.0, 1, "pizza"))
        CartManager.addItem(CartItem(2, "Item2", 200.0, 1, "pizza"))
        CartManager.addItem(CartItem(3, "Item3", 300.0, 1, "pizza"))

        val items = CartManager.getItems()
        val averagePrice = items.map { it.price }.average()

        assertEquals(200.0, averagePrice, 0.01)

        CartManager.clearCart()
    }
}
