package com.example.dokusochka

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for CartManager
 */
class CartManagerTest {

    @Before
    fun setUp() {
        // Clear cart before each test
        CartManager.clearCart()
    }

    @After
    fun tearDown() {
        // Clear cart after each test
        CartManager.clearCart()
    }

    @Test
    fun addItem_newItem_shouldAddToCart() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 1, "pizza")

        // Act
        CartManager.addItem(item)

        // Assert
        assertEquals(1, CartManager.getItems().size)
        assertEquals(1, CartManager.getTotalCount())
    }

    @Test
    fun addItem_duplicateItem_shouldIncreaseQuantity() {
        // Arrange
        val item1 = CartItem(1, "Пепперони", 289.0, 1, "pizza")
        val item2 = CartItem(1, "Пепперони", 289.0, 2, "pizza")

        // Act
        CartManager.addItem(item1)
        CartManager.addItem(item2)

        // Assert
        assertEquals(1, CartManager.getItems().size)
        assertEquals(3, CartManager.getTotalCount())
        assertEquals(3, CartManager.getItemQuantity(1, "pizza"))
    }

    @Test
    fun addItem_differentTypes_shouldAddSeparately() {
        // Arrange
        val pizza = CartItem(1, "Пепперони", 289.0, 1, "pizza")
        val drink = CartItem(1, "Кола", 99.0, 1, "drink")

        // Act
        CartManager.addItem(pizza)
        CartManager.addItem(drink)

        // Assert
        assertEquals(2, CartManager.getItems().size)
        assertEquals(2, CartManager.getTotalCount())
    }

    @Test
    fun removeItem_existingItem_shouldRemoveFromCart() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 2, "pizza")
        CartManager.addItem(item)

        // Act
        CartManager.removeItem(1, "pizza")

        // Assert
        assertEquals(0, CartManager.getItems().size)
        assertEquals(0, CartManager.getTotalCount())
    }

    @Test
    fun removeItem_nonExistingItem_shouldNotCrash() {
        // Act
        CartManager.removeItem(999, "pizza")

        // Assert
        assertEquals(0, CartManager.getItems().size)
    }

    @Test
    fun updateQuantity_positiveValue_shouldUpdateQuantity() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 1, "pizza")
        CartManager.addItem(item)

        // Act
        CartManager.updateQuantity(1, "pizza", 5)

        // Assert
        assertEquals(5, CartManager.getItemQuantity(1, "pizza"))
        assertEquals(5, CartManager.getTotalCount())
    }

    @Test
    fun updateQuantity_zeroValue_shouldRemoveItem() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 3, "pizza")
        CartManager.addItem(item)

        // Act
        CartManager.updateQuantity(1, "pizza", 0)

        // Assert
        assertEquals(0, CartManager.getItems().size)
        assertEquals(0, CartManager.getTotalCount())
    }

    @Test
    fun updateQuantity_negativeValue_shouldRemoveItem() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 2, "pizza")
        CartManager.addItem(item)

        // Act
        CartManager.updateQuantity(1, "pizza", -1)

        // Assert
        assertEquals(0, CartManager.getItems().size)
    }

    @Test
    fun getItemQuantity_existingItem_shouldReturnCorrectQuantity() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 3, "pizza")
        CartManager.addItem(item)

        // Act
        val quantity = CartManager.getItemQuantity(1, "pizza")

        // Assert
        assertEquals(3, quantity)
    }

    @Test
    fun getItemQuantity_nonExistingItem_shouldReturnZero() {
        // Act
        val quantity = CartManager.getItemQuantity(999, "pizza")

        // Assert
        assertEquals(0, quantity)
    }

    @Test
    fun getItems_shouldReturnCopyOfList() {
        // Arrange
        val item1 = CartItem(1, "Пепперони", 289.0, 1, "pizza")
        val item2 = CartItem(2, "Маргарита", 259.0, 2, "pizza")
        CartManager.addItem(item1)
        CartManager.addItem(item2)

        // Act
        val items = CartManager.getItems()

        // Assert
        assertEquals(2, items.size)
        assertTrue(items is List<CartItem>)
    }

    @Test
    fun getTotalCount_emptyCart_shouldReturnZero() {
        // Act
        val count = CartManager.getTotalCount()

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun getTotalCount_multipleItems_shouldReturnCorrectSum() {
        // Arrange
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza"))
        CartManager.addItem(CartItem(2, "Маргарита", 259.0, 3, "pizza"))
        CartManager.addItem(CartItem(3, "Кола", 99.0, 1, "drink"))

        // Act
        val count = CartManager.getTotalCount()

        // Assert
        assertEquals(6, count)
    }

    @Test
    fun getTotalPrice_emptyCart_shouldReturnZero() {
        // Act
        val price = CartManager.getTotalPrice()

        // Assert
        assertEquals(0.0, price, 0.01)
    }

    @Test
    fun getTotalPrice_singleItem_shouldCalculateCorrectly() {
        // Arrange
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza"))

        // Act
        val price = CartManager.getTotalPrice()

        // Assert
        assertEquals(578.0, price, 0.01)
    }

    @Test
    fun getTotalPrice_multipleItems_shouldCalculateCorrectly() {
        // Arrange
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza")) // 578
        CartManager.addItem(CartItem(2, "Маргарита", 259.0, 1, "pizza")) // 259
        CartManager.addItem(CartItem(3, "Кола", 99.0, 3, "drink"))       // 297

        // Act
        val price = CartManager.getTotalPrice()

        // Assert
        assertEquals(1134.0, price, 0.01)
    }

    @Test
    fun clearCart_shouldRemoveAllItems() {
        // Arrange
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza"))
        CartManager.addItem(CartItem(2, "Маргарита", 259.0, 1, "pizza"))

        // Act
        CartManager.clearCart()

        // Assert
        assertEquals(0, CartManager.getItems().size)
        assertEquals(0, CartManager.getTotalCount())
        assertEquals(0.0, CartManager.getTotalPrice(), 0.01)
    }

    @Test
    fun cartItem_equality_shouldWorkCorrectly() {
        // Arrange
        val item1 = CartItem(1, "Пепперони", 289.0, 2, "pizza")
        val item2 = CartItem(1, "Пепперони", 289.0, 2, "pizza")

        // Assert
        assertEquals(item1, item2)
    }

    @Test
    fun cartItem_modification_shouldUpdateQuantity() {
        // Arrange
        val item = CartItem(1, "Пепперони", 289.0, 1, "pizza")

        // Act
        item.quantity = 5

        // Assert
        assertEquals(5, item.quantity)
    }

    @Test
    fun printCartContents_shouldNotCrash() {
        // Arrange
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza"))

        // Act & Assert - should not throw exception
        CartManager.printCartContents()
    }

    @Test
    fun complexScenario_addUpdateRemove_shouldWorkCorrectly() {
        // Scenario: User adds items, updates quantities, and removes some
        
        // Add first item
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 1, "pizza"))
        assertEquals(1, CartManager.getTotalCount())
        assertEquals(289.0, CartManager.getTotalPrice(), 0.01)
        
        // Add more of the same item
        CartManager.addItem(CartItem(1, "Пепперони", 289.0, 2, "pizza"))
        assertEquals(3, CartManager.getTotalCount())
        assertEquals(867.0, CartManager.getTotalPrice(), 0.01)
        
        // Update quantity
        CartManager.updateQuantity(1, "pizza", 5)
        assertEquals(5, CartManager.getTotalCount())
        assertEquals(1445.0, CartManager.getTotalPrice(), 0.01)
        
        // Add different item
        CartManager.addItem(CartItem(2, "Маргарита", 259.0, 1, "pizza"))
        assertEquals(6, CartManager.getTotalCount())
        assertEquals(1704.0, CartManager.getTotalPrice(), 0.01)
        
        // Remove first item
        CartManager.removeItem(1, "pizza")
        assertEquals(1, CartManager.getTotalCount())
        assertEquals(259.0, CartManager.getTotalPrice(), 0.01)
    }
}
