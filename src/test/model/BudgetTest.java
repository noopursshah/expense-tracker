package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {
    private Budget testLimit;
    private Budget testLimit2;

    @BeforeEach
    void runBefore() {
        testLimit = new Budget(Month.JAN,600.0);
        testLimit2 = new Budget(Month.APR,-200.0);
    }

    @Test
    void testConstructor() {
        assertEquals(600, testLimit.getLimit());
        assertTrue(testLimit.getLimit() > 0);
        assertEquals(Month.JAN, testLimit.getMonth());
    }

    @Test
    void testNegativeBudget() {
        assertEquals(0,testLimit2.getLimit());
    }

    @Test
    void testToString() {
        assertTrue(testLimit.toString().contains("Budget limit= $600.00"));
    }
}