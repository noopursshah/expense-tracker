package model;

import model.Expense;
import model.Month;
import model.Record;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {
    private Record record;
    private Expense expense1;
    private Expense expense2;

    @BeforeEach
    void runBefore() {
        ArrayList<Expense> transactions = new ArrayList<>();
        record = new Record(Month.JAN,transactions);
        expense1 = new Expense("2023-01-15", "Groceries", 50.0);
        expense2 = new Expense("2023-01-20", "Dining out", 30.0);
    }

    @Test
    public void testAddExpense() {
        record.addExpense(expense1);
        ArrayList<Expense> expenses = record.getExpenses();
        assertEquals(1, expenses.size());
        assertEquals(expense1, expenses.get(0));
    }

    @Test
    public void testGetMonth() {
        assertEquals(Month.JAN, record.getMonth());
    }
}
