package persistence;

import model.Budget;
import model.Month;
import model.Expense;
import model.Record;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.json.*;

// Represents a reader that reads records from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads records from file and returns it;
    // throws IOException if an error occurs reading data from file
    public List<Record> readRecords() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("records");
        return parseRecordList(jsonArray);
    }

    // EFFECTS: reads budgets from file and returns it;
    // throws IOException if an error occurs reading data from file
    public List<Budget> readBudgets() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray budgetArray = jsonObject.getJSONArray("Budget limits");
        return parseBudgetList(budgetArray);
    }

    // EFFECTS: reads Balance from file and returns it
    // throws IOException if an error occurs reading data from file
    public double readBalance() throws IOException {
        String jsonData = readFile(source);
        //JSONArray jsonArray = new JSONArray(jsonData);
        JSONObject jsonObject = new JSONObject(jsonData);
        double balance = jsonObject.getDouble("Account balance");
        return balance;
    }


    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();

    }

    // EFFECTS: parses BudgetList and returns a list of Budgets
    private List<Budget> parseBudgetList(JSONArray jsonArray) {
        List<Budget> budgetList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Budget budget = parseBudget(jsonObject);
            budgetList.add(budget);
        }
        return budgetList;
    }

    // EFFECTS: returns a Budget
    private Budget parseBudget(JSONObject jsonObject) {
        Month month = Month.valueOf(jsonObject.getString("month"));
        double limit = jsonObject.getDouble("Budget limit");
        return new Budget(month, limit);
    }

    // EFFECTS: parses a JSON array of records and returns a list of Record objects
    private ArrayList<Record> parseRecordList(JSONArray jsonArray) {
        ArrayList<Record> recordList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Record record = parseRecord(jsonObject);
            recordList.add(record);
        }
        return recordList;
    }

    // EFFECTS: parses a single Record object from JSON
    private Record parseRecord(JSONObject jsonObject) {
        Month month = Month.valueOf(jsonObject.getString("month"));
        JSONArray expensesArray = jsonObject.getJSONArray("expenses");
        ArrayList<Expense> expenses = parseExpenses(expensesArray);
        return new Record(month, expenses);
    }

    // EFFECTS: parses a JSON array of expenses and returns a list of Expense objects
    private ArrayList<Expense> parseExpenses(JSONArray jsonArray) {
        ArrayList<Expense> expenseList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Expense expense = parseExpense(jsonObject);
            expenseList.add(expense);
        }
        return expenseList;
    }

    // EFFECTS: parses a single Expense object from JSON
    private Expense parseExpense(JSONObject jsonObject) {
        String date = jsonObject.getString("date");
        double amount = jsonObject.getDouble("amount");
        String description = jsonObject.getString("description");
        return new Expense(date, description, amount);
    }

}
