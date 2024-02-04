package model;

import org.json.JSONObject;
import org.json.JSONArray;
import persistence.Writable;

// Represents a Budget limit set by the user in dollars
public class Budget implements Writable {
    private Month month;
    private double limit;

    /*
    * REQUIRES : budget is a non-negative and non-zero number
    * EFFECTS : if budget >= 0 , then the limit is set to the budget
    *           else the limit will be set to 0.
    */
    public Budget(Month month, double budget) {
        this.month = month;
        if (budget >= 0) {
            limit = budget;
        } else {
            limit = 0;
        }
    }

    //Getter methods
    public double getLimit() {
        return limit;
    }

    public Month getMonth() {
        return this.month;
    }

    /*
     * EFFECTS: returns a string representation of Budget
     */
    @Override
    public String toString() {
        return "[Budget limit= $" + String.format("%.2f", limit) + "]";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("month", month);
        json.put("Budget limit", limit);
        return json;
    }

}

