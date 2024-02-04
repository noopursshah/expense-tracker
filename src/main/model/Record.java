package model;

import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

// Represents a record having a list of Expenses in a particular month
public class Record implements Writable {
    private Month month;
    private ArrayList<Expense> expenses;

    // EFFECTS: constructs a record with a month and list of expenses
    public Record(Month month, ArrayList<Expense> expenses) {
        this.month = month;
        this.expenses = expenses;
    }

    // Getter method
    public Month getMonth() {
        return month;
    }

    // MODIFIES: this
    // EFFECTS: adds Expense to this record
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    // EFFECTS: returns a list of expenses in this record
    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("month", month);
        json.put("expenses", expenses);
        return json;
    }

}
