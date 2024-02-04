package model;

import org.json.JSONObject;
import persistence.Writable;

// Represents an Expense with date, description and amount transacted.
public class Expense implements Writable {
    private String date;            // Date of transaction
    private String description;     // Transaction description
    private double amount;          // Amount spent

    /*
     * REQUIRES : amount >= 0
     * EFFECTS : assigns date, description and amount
     */
    public Expense(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // Getter methods
    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    /*
     * EFFECTS: returns a string representation of the Expense
     */
    @Override
    public String toString() {
        return "[Date=" + date + ", Description=" + description + ", Amount= $" + String.format("%.2f", amount) + "]";
    }



    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("date", date);
        json.put("description", description);
        json.put("amount", amount);
        return json;
    }

}