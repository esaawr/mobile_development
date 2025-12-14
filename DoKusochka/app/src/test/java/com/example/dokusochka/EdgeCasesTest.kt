package com.example.dokusochka

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Edge cases and boundary tests
 */
class EdgeCasesTest {

    @Before
    fun setUp() {
        CartManager.clearCart()
    }

    @After
    fun tearDown() {
        CartManager.clearCart()
    }

    @Test
    fun cartManager_veryLargeQuantity_shouldHandle() {
        // Test with very large quantity
        val item = CartItem(1, "Пепперони", 289.0, 1000, "pizza")
        CartManager.addItem(item)

        assertEquals(1000, CartManager.getTotalCount())
        assertEquals(289000.0, CartManager.getTotalPrice(), 0.01)
    }

    @Test
    fun cartManager_manyDifferentItems_shouldHandle() {
        // Test with many different items
        for (i in 1..100) {
            CartManager.addItem(CartItem(i, "Item $i", 100.0, 1, "pizza"))
        }

        assertEquals(100, CartManager.getItems().size)
        assertEquals(100, CartManager.getTotalCount())
    }

    @Test
    fun cartItem_veryLongName_shouldHandle() {
        // Test with very long product name
        val longName = "A".repeat(1000)
        val item = CartItem(1, longName, 100.0, 1, "pizza")

        assertEquals(longName, item.name)
        assertEquals(1000, item.name.length)
    }

    @Test
    fun cartItem_specialCharactersInName_shouldHandle() {
        // Test with special characters
        val specialName = "Пицца «4 Сыра» & Напиток №1 @ 50% скидка!"
        val item = CartItem(1, specialName, 100.0, 1, "pizza")

        assertEquals(specialName, item.name)
    }

    @Test
    fun cartItem_verySmallPrice_shouldHandle() {
        // Test with very small price
        val item = CartItem(1, "Cheap", 0.01, 1, "pizza")

        assertEquals(0.01, item.price, 0.001)
    }

    @Test
    fun cartItem_veryLargePrice_shouldHandle() {
        // Test with very large price
        val item = CartItem(1, "Expensive", 999999.99, 1, "pizza")

        assertEquals(999999.99, item.price, 0.01)
    }

    @Test
    fun cartManager_addRemoveMultipleTimes_shouldMaintainConsistency() {
        // Test add/remove cycles
        for (i in 1..10) {
            CartManager.addItem(CartItem(1, "Pizza", 100.0, 1, "pizza"))
            assertEquals(i, CartManager.getTotalCount())
        }

        for (i in 10 downTo 1) {
            CartManager.updateQuantity(1, "pizza", i - 1)
            if (i > 1) {
                assertEquals(i - 1, CartManager.getTotalCount())
            } else {
                assertEquals(0, CartManager.getTotalCount())
            }
        }
    }

    @Test
    fun cartManager_concurrentTypeModifications_shouldHandleCorrectly() {
        // Add same ID but different types
        CartManager.addItem(CartItem(1, "Item", 100.0, 2, "pizza"))
        CartManager.addItem(CartItem(1, "Item", 100.0, 3, "drink"))
        CartManager.addItem(CartItem(1, "Item", 100.0, 4, "snack"))

        assertEquals(3, CartManager.getItems().size)
        assertEquals(9, CartManager.getTotalCount())
    }

    @Test
    fun cartManager_removeNonExistentType_shouldNotAffectOthers() {
        // Add item and try to remove different type
        CartManager.addItem(CartItem(1, "Pizza", 100.0, 2, "pizza"))
        CartManager.removeItem(1, "drink")

        assertEquals(1, CartManager.getItems().size)
        assertEquals(2, CartManager.getTotalCount())
    }

    @Test
    fun cartManager_multipleClears_shouldBeIdempotent() {
        // Test multiple clear operations
        CartManager.addItem(CartItem(1, "Pizza", 100.0, 1, "pizza"))
        CartManager.clearCart()
        CartManager.clearCart()
        CartManager.clearCart()

        assertEquals(0, CartManager.getItems().size)
    }

    @Test
    fun pizza_emptyDescription_shouldBeValid() {
        // Test pizza with empty description
        val pizza = Pizza(1, "Simple", 100.0, "", "path")

        assertEquals("", pizza.description)
        assertTrue(pizza.description.isEmpty())
    }

    @Test
    fun pizza_emptyImagePath_shouldBeValid() {
        // Test pizza with empty image path
        val pizza = Pizza(1, "NoImage", 100.0, "Desc", "")

        assertEquals("", pizza.imagePath)
    }

    @Test
    fun order_emptyItemsList_shouldBeValid() {
        // Test order with empty items list
        val order = Order(1, "Customer", "", 0.0, "2024-01-01", "новый")

        assertEquals("", order.itemsList)
    }

    @Test
    fun order_zeroPrice_shouldBeValid() {
        // Test order with zero price
        val order = Order(1, "Customer", "Free item", 0.0, "2024-01-01", "новый")

        assertEquals(0.0, order.totalPrice, 0.01)
    }

    @Test
    fun client_specialCharactersInName_shouldHandle() {
        // Test client with special characters
        val client = Client(1, "O'Brien-Smith", "test@mail.ru", "+7-900-123", "2024")

        assertTrue(client.name.contains("'"))
        assertTrue(client.name.contains("-"))
    }

    @Test
    fun clientStatistics_zeroValues_shouldBeValid() {
        // Test statistics with zero values
        val stats = ClientStatistics(0, 0)

        assertEquals(0, stats.totalClients)
        assertEquals(0, stats.clientsWithOrders)
    }

    @Test
    fun clientStatistics_allClientsHaveOrders_shouldBeValid() {
        // Test when all clients have orders
        val stats = ClientStatistics(100, 100)

        assertEquals(stats.totalClients, stats.clientsWithOrders)
    }

    @Test
    fun cartItem_quantityUpdate_extremeValues() {
        // Test quantity updates with extreme values
        val item = CartItem(1, "Pizza", 100.0, 1, "pizza")
        CartManager.addItem(item)

        CartManager.updateQuantity(1, "pizza", Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, CartManager.getItemQuantity(1, "pizza"))
    }

    @Test
    fun cartManager_findItem_withNullSafety() {
        // Test finding non-existent items
        val quantity = CartManager.getItemQuantity(999, "nonexistent")

        assertEquals(0, quantity)
    }

    @Test
    fun dataClass_hashCode_consistency() {
        // Test hashCode consistency
        val item1 = CartItem(1, "Pizza", 100.0, 1, "pizza")
        val item2 = CartItem(1, "Pizza", 100.0, 1, "pizza")

        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun dataClass_equals_symmetry() {
        // Test equals symmetry
        val item1 = CartItem(1, "Pizza", 100.0, 1, "pizza")
        val item2 = CartItem(1, "Pizza", 100.0, 1, "pizza")

        assertTrue(item1 == item2)
        assertTrue(item2 == item1)
    }

    @Test
    fun dataClass_equals_transitivity() {
        // Test equals transitivity
        val item1 = CartItem(1, "Pizza", 100.0, 1, "pizza")
        val item2 = CartItem(1, "Pizza", 100.0, 1, "pizza")
        val item3 = CartItem(1, "Pizza", 100.0, 1, "pizza")

        assertTrue(item1 == item2)
        assertTrue(item2 == item3)
        assertTrue(item1 == item3)
    }

    @Test
    fun cartManager_totalPrice_floatingPointPrecision() {
        // Test floating point precision
        CartManager.addItem(CartItem(1, "Item1", 0.1, 1, "pizza"))
        CartManager.addItem(CartItem(2, "Item2", 0.2, 1, "pizza"))

        val total = CartManager.getTotalPrice()

        assertEquals(0.3, total, 0.001)
    }

    @Test
    fun list_operations_emptyList() {
        // Test operations on empty list
        val items = CartManager.getItems()

        assertTrue(items.isEmpty())
        assertEquals(0, items.size)
        assertFalse(items.any())
    }

    @Test
    fun string_operations_cyrillicText() {
        // Test with Cyrillic text
        val russianText = "Пицца с колбасой"
        val item = CartItem(1, russianText, 100.0, 1, "pizza")

        assertEquals(russianText, item.name)
        assertTrue(item.name.contains("Пицца"))
    }

    @Test
    fun order_multilineItemsList_shouldHandle() {
        // Test order with multiline items list
        val itemsList = """
            Пепперони x2
            Маргарита x1
            Кола x3
        """.trimIndent()

        val order = Order(1, "Customer", itemsList, 500.0, "2024-01-01", "новый")

        assertTrue(order.itemsList.contains("\n"))
        assertTrue(order.itemsList.split("\n").size >= 3)
    }
}
