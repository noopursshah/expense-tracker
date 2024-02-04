package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {
    private Expense testExpense;

    @BeforeEach
    void runBefore() {
        testExpense = new Expense("13/10/2023", "UBC Navratri", 23.0);
    }

    @Test
    void testConstructor() {
        assertEquals("13/10/2023", testExpense.getDate());
        assertEquals(23.0, testExpense.getAmount());
        assertEquals("UBC Navratri",testExpense.getDescription());
    }


    @Test
    void testToString() {
        assertTrue(testExpense.toString().contains("Date=13/10/2023, Description=UBC Navratri, Amount= $23.0"));
    }
}
