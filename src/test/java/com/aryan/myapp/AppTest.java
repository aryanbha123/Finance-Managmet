package com.aryan.myapp;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;


class App {
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public boolean isValidAmount(double amount) {
        return amount >= 0;
    }

    public String greetUser(String name) {
        return "Hello, " + name + "!";
    }
}

/**
 * Unit test for App class.
 */
public class AppTest extends TestCase {

    private App app;

    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    @Override
    protected void setUp() {
        app = new App(); // Initialize before every test
    }

    public void testAddition() {
        int result = app.add(10, 20);
        assertEquals(30, result);
    }

    public void testSubtraction() {
        int result = app.subtract(50, 20);
        assertEquals(30, result);
    }

    public void testValidAmount() {
        assertTrue(app.isValidAmount(100.0));
    }

    public void testInvalidAmount() {
        assertFalse(app.isValidAmount(-50.5));
    }

    public void testGreeting() {
        String greeting = app.greetUser("Aryan");
        assertEquals("Hello, Aryan!", greeting);
    }
}
