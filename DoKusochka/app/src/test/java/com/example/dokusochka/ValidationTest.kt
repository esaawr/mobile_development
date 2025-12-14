package com.example.dokusochka

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for utility methods and validation logic
 */
class ValidationTest {

    @Test
    fun password_validation_minimumLength() {
        // Test password length validation
        val shortPassword = "123"
        val validPassword = "admin123"

        assertTrue(validPassword.length >= 6)
        assertFalse(shortPassword.length >= 6)
    }

    @Test
    fun email_validation_format() {
        // Basic email format validation
        val validEmail = "user@example.com"
        val invalidEmail1 = "userexample.com"
        val invalidEmail2 = "@example.com"
        val invalidEmail3 = "user@"

        assertTrue(validEmail.contains("@") && validEmail.contains("."))
        assertFalse(invalidEmail1.contains("@"))
        assertFalse(invalidEmail2.indexOf("@") > 0)
        assertFalse(invalidEmail3.substring(invalidEmail3.indexOf("@")).contains("."))
    }

    @Test
    fun phone_validation_format() {
        // Phone number validation
        val validPhone1 = "+7 900 123-45-67"
        val validPhone2 = "89001234567"
        val invalidPhone = "abc"

        assertTrue(validPhone1.contains("+") || validPhone1.contains("8"))
        assertTrue(validPhone2.startsWith("8") || validPhone2.startsWith("+7"))
        assertFalse(invalidPhone.all { it.isDigit() || it in "+- ()" })
    }

    @Test
    fun price_validation_positive() {
        // Price should be positive
        val validPrice = 289.0
        val invalidPrice = -100.0
        val zeroPrice = 0.0

        assertTrue(validPrice > 0)
        assertFalse(invalidPrice > 0)
        assertFalse(zeroPrice > 0)
    }

    @Test
    fun quantity_validation_positive() {
        // Quantity should be positive integer
        val validQuantity = 5
        val invalidQuantity = -1
        val zeroQuantity = 0

        assertTrue(validQuantity > 0)
        assertFalse(invalidQuantity > 0)
        assertFalse(zeroQuantity > 0)
    }

    @Test
    fun name_validation_notEmpty() {
        // Name should not be empty
        val validName = "Иван"
        val emptyName = ""
        val blankName = "   "

        assertTrue(validName.isNotBlank())
        assertFalse(emptyName.isNotBlank())
        assertFalse(blankName.isNotBlank())
    }

    @Test
    fun orderStatus_validation_allowedValues() {
        // Order status should be one of allowed values
        val allowedStatuses = listOf("новый", "в обработке", "выполнен", "отменен")
        val validStatus = "новый"
        val invalidStatus = "unknown"

        assertTrue(allowedStatuses.contains(validStatus))
        assertFalse(allowedStatuses.contains(invalidStatus))
    }

    @Test
    fun totalPrice_calculation_accuracy() {
        // Test price calculation accuracy
        val price1 = 289.0
        val quantity1 = 2
        val price2 = 99.0
        val quantity2 = 3

        val total = (price1 * quantity1) + (price2 * quantity2)

        assertEquals(875.0, total, 0.01)
    }

    @Test
    fun itemsList_formatting_correctFormat() {
        // Test items list string formatting
        val items = listOf(
            "Пепперони x2",
            "Маргарита x1",
            "Кола x3"
        )

        val itemsString = items.joinToString(", ")

        assertTrue(itemsString.contains("Пепперони x2"))
        assertTrue(itemsString.contains(", "))
        assertEquals(3, itemsString.split(", ").size)
    }

    @Test
    fun dateFormat_validation_correctPattern() {
        // Test date format pattern
        val validDate = "2024-01-01 12:00:00"
        val pattern = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""".toRegex()

        assertTrue(pattern.matches(validDate))
    }

    @Test
    fun cartItem_totalPrice_calculation() {
        // Test individual cart item total price
        val item = CartItem(1, "Пепперони", 289.0, 3, "pizza")
        val total = item.price * item.quantity

        assertEquals(867.0, total, 0.01)
    }

    @Test
    fun discount_calculation_percentage() {
        // Test discount calculation
        val originalPrice = 1000.0
        val discountPercent = 10
        val discountedPrice = originalPrice * (100 - discountPercent) / 100

        assertEquals(900.0, discountedPrice, 0.01)
    }

    @Test
    fun category_validation_allowedTypes() {
        // Test category types
        val allowedCategories = listOf("pizza", "drink", "snack", "dessert")
        val validCategory = "pizza"
        val invalidCategory = "unknown"

        assertTrue(allowedCategories.contains(validCategory))
        assertFalse(allowedCategories.contains(invalidCategory))
    }

    @Test
    fun string_trimming_whitespace() {
        // Test string trimming
        val input = "  test  "
        val trimmed = input.trim()

        assertEquals("test", trimmed)
        assertNotEquals(input, trimmed)
    }

    @Test
    fun nullSafety_handling() {
        // Test null safety handling
        val nullableString: String? = null
        val defaultValue = "default"

        val result = nullableString ?: defaultValue

        assertEquals(defaultValue, result)
    }

    @Test
    fun list_filtering_byType() {
        // Test filtering cart items by type
        val items = listOf(
            CartItem(1, "Пепперони", 289.0, 1, "pizza"),
            CartItem(2, "Кола", 99.0, 1, "drink"),
            CartItem(3, "Маргарита", 259.0, 1, "pizza")
        )

        val pizzas = items.filter { it.type == "pizza" }

        assertEquals(2, pizzas.size)
        assertTrue(pizzas.all { it.type == "pizza" })
    }

    @Test
    fun list_summing_quantities() {
        // Test summing quantities
        val items = listOf(
            CartItem(1, "Item1", 100.0, 2, "pizza"),
            CartItem(2, "Item2", 100.0, 3, "pizza"),
            CartItem(3, "Item3", 100.0, 5, "pizza")
        )

        val total = items.sumOf { it.quantity }

        assertEquals(10, total)
    }

    @Test
    fun list_summing_prices() {
        // Test summing total prices
        val items = listOf(
            CartItem(1, "Item1", 100.0, 2, "pizza"),
            CartItem(2, "Item2", 50.0, 3, "pizza")
        )

        val total = items.sumOf { it.price * it.quantity }

        assertEquals(350.0, total, 0.01)
    }

    @Test
    fun string_comparison_caseInsensitive() {
        // Test case-insensitive comparison
        val str1 = "PIZZA"
        val str2 = "pizza"

        assertTrue(str1.equals(str2, ignoreCase = true))
        assertFalse(str1 == str2)
    }

    @Test
    fun range_validation_minMax() {
        // Test value range validation
        val value = 5
        val min = 1
        val max = 10

        assertTrue(value in min..max)
        assertFalse(0 in min..max)
        assertFalse(11 in min..max)
    }
}
