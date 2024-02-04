package persistence;

import model.Expense;
import model.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkExpense(String date, String description, double amount, Expense expense) {
        assertEquals(date, expense.getDate());
        assertEquals(description, expense.getDescription());
        assertEquals(amount, expense.getAmount()); // Adjust the delta value as needed
    }

    protected void checkMonth(String expectedMonth, Month month) {
        assertEquals(expectedMonth, month.name());
    }
}

