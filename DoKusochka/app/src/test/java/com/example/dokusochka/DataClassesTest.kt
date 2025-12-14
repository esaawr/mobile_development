package com.example.dokusochka

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for data classes
 */
class DataClassesTest {

    @Test
    fun cartItem_creation_shouldSetPropertiesCorrectly() {
        // Act
        val item = CartItem(
            id = 1,
            name = "Пепперони",
            price = 289.0,
            quantity = 2,
            type = "pizza"
        )

        // Assert
        assertEquals(1, item.id)
        assertEquals("Пепперони", item.name)
        assertEquals(289.0, item.price, 0.01)
        assertEquals(2, item.quantity)
        assertEquals("pizza", item.type)
    }

    @Test
    fun cartItem_copy_shouldCreateNewInstance() {
        // Arrange
        val original = CartItem(1, "Пепперони", 289.0, 2, "pizza")

        // Act
        val copy = original.copy()

        // Assert
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }

    @Test
    fun cartItem_copyWithChanges_shouldModifyOnlySpecifiedFields() {
        // Arrange
        val original = CartItem(1, "Пепперони", 289.0, 2, "pizza")

        // Act
        val modified = original.copy(quantity = 5)

        // Assert
        assertEquals(5, modified.quantity)
        assertEquals(original.id, modified.id)
        assertEquals(original.name, modified.name)
        assertEquals(original.price, modified.price, 0.01)
        assertEquals(original.type, modified.type)
    }

    @Test
    fun pizza_creation_shouldSetPropertiesCorrectly() {
        // Act
        val pizza = Pizza(
            id = 1,
            name = "4 СЫРА",
            price = 289.0,
            description = "МОЦАРЕЛЛА, СЫР ЧЕДДЕР И ПАРМЕЗАН",
            imagePath = "pizza_4_cheese",
            category = "pizza"
        )

        // Assert
        assertEquals(1, pizza.id)
        assertEquals("4 СЫРА", pizza.name)
        assertEquals(289.0, pizza.price, 0.01)
        assertEquals("МОЦАРЕЛЛА, СЫР ЧЕДДЕР И ПАРМЕЗАН", pizza.description)
        assertEquals("pizza_4_cheese", pizza.imagePath)
        assertEquals("pizza", pizza.category)
    }

    @Test
    fun pizza_defaultCategory_shouldBePizza() {
        // Act
        val pizza = Pizza(
            id = 1,
            name = "Тест",
            price = 100.0,
            description = "Описание",
            imagePath = "test"
        )

        // Assert
        assertEquals("pizza", pizza.category)
    }

    @Test
    fun pizza_equality_shouldWorkCorrectly() {
        // Arrange
        val pizza1 = Pizza(1, "Пепперони", 289.0, "Описание", "path", "pizza")
        val pizza2 = Pizza(1, "Пепперони", 289.0, "Описание", "path", "pizza")

        // Assert
        assertEquals(pizza1, pizza2)
    }

    @Test
    fun order_creation_shouldSetPropertiesCorrectly() {
        // Act
        val order = Order(
            id = 1,
            customerName = "Иван Иванов",
            itemsList = "Пепперони x2, Кола x1",
            totalPrice = 677.0,
            orderDate = "2024-01-01 12:00:00",
            status = "новый"
        )

        // Assert
        assertEquals(1, order.id)
        assertEquals("Иван Иванов", order.customerName)
        assertEquals("Пепперони x2, Кола x1", order.itemsList)
        assertEquals(677.0, order.totalPrice, 0.01)
        assertEquals("2024-01-01 12:00:00", order.orderDate)
        assertEquals("новый", order.status)
    }

    @Test
    fun order_copy_shouldCreateNewInstance() {
        // Arrange
        val original = Order(1, "Иван", "items", 500.0, "2024-01-01", "новый")

        // Act
        val copy = original.copy()

        // Assert
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }

    @Test
    fun order_copyWithStatusChange_shouldModifyStatus() {
        // Arrange
        val original = Order(1, "Иван", "items", 500.0, "2024-01-01", "новый")

        // Act
        val updated = original.copy(status = "выполнен")

        // Assert
        assertEquals("выполнен", updated.status)
        assertEquals(original.id, updated.id)
        assertEquals(original.customerName, updated.customerName)
    }

    @Test
    fun client_creation_shouldSetPropertiesCorrectly() {
        // Act
        val client = Client(
            id = 1,
            name = "Иван Иванов",
            email = "ivan@example.com",
            phone = "+7 900 123-45-67",
            registrationDate = "2024-01-01"
        )

        // Assert
        assertEquals(1, client.id)
        assertEquals("Иван Иванов", client.name)
        assertEquals("ivan@example.com", client.email)
        assertEquals("+7 900 123-45-67", client.phone)
        assertEquals("2024-01-01", client.registrationDate)
    }

    @Test
    fun clientStatistics_creation_shouldSetPropertiesCorrectly() {
        // Act
        val stats = ClientStatistics(
            totalClients = 100,
            clientsWithOrders = 45
        )

        // Assert
        assertEquals(100, stats.totalClients)
        assertEquals(45, stats.clientsWithOrders)
    }

    @Test
    fun clientStatistics_copy_shouldWorkCorrectly() {
        // Arrange
        val original = ClientStatistics(100, 45)

        // Act
        val modified = original.copy(totalClients = 150)

        // Assert
        assertEquals(150, modified.totalClients)
        assertEquals(45, modified.clientsWithOrders)
    }

    @Test
    fun cartItem_toString_shouldContainAllFields() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 2, "pizza")

        // Act
        val str = item.toString()

        // Assert
        assertTrue(str.contains("1"))
        assertTrue(str.contains("Пепперони"))
        assertTrue(str.contains("289.0"))
        assertTrue(str.contains("2"))
        assertTrue(str.contains("pizza"))
    }

    @Test
    fun pizza_toString_shouldContainAllFields() {
        // Arrange
        val pizza = Pizza(1, "Пепперони", 289.0, "Вкусная", "path", "pizza")

        // Act
        val str = pizza.toString()

        // Assert
        assertTrue(str.contains("1"))
        assertTrue(str.contains("Пепперони"))
        assertTrue(str.contains("289.0"))
    }

    @Test
    fun multipleCartItems_withDifferentTypes_shouldBeDistinct() {
        // Arrange
        val pizza = CartItem(1, "Пепперони", 289.0, 1, "pizza")
        val drink = CartItem(1, "Пепперони", 289.0, 1, "drink")
        val snack = CartItem(1, "Пепперони", 289.0, 1, "snack")

        // Assert
        assertNotEquals(pizza, drink)
        assertNotEquals(pizza, snack)
        assertNotEquals(drink, snack)
    }

    @Test
    fun order_withLargePrice_shouldHandleCorrectly() {
        // Act
        val order = Order(1, "Test", "items", 999999.99, "2024-01-01", "новый")

        // Assert
        assertEquals(999999.99, order.totalPrice, 0.01)
    }

    @Test
    fun client_withEmptyFields_shouldBeValid() {
        // Act
        val client = Client(1, "", "", "", "")

        // Assert
        assertEquals("", client.name)
        assertEquals("", client.email)
        assertEquals("", client.phone)
    }
}
