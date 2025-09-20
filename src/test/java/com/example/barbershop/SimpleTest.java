package com.example.barbershop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test class to ensure basic test functionality works
 */
class SimpleTest {

    @Test
    void testBasicFunctionality() {
        // Simple test to ensure JUnit is working
        assertTrue(true, "Basic test should pass");
    }

    @Test
    void testStringOperations() {
        String testString = "Hello, BarberShop!";
        assertNotNull(testString);
        assertFalse(testString.isEmpty());
        assertEquals("Hello, BarberShop!", testString);
    }

    @Test
    void testMathOperations() {
        int result = 2 + 2;
        assertEquals(4, result);
        assertNotEquals(5, result);
    }
}
