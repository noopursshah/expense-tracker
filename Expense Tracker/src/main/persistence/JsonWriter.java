package persistence;

import model.Budget;
import model.Record;
import model.Expense;

import java.io.IOException;
import java.util.ArrayList;

import org.json.*;

import java.io.*;
import java.util.List;

// Writes data to a JSON file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to the destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // EFFECTS: returns the destination file path
    public String getDestination() {
        return destination;
    }

    // MODIFIES: this
    // EFFECTS: opens the writer; throws FileNotFoundException if the destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }


    // MODIFIES: this
    // EFFECTS: writes JSON representation of a list of records to the file
    public void write(List<Record> rlist, List<Budget> blist, double balance) {
        JSONObject js = new JSONObject();

        int lenB = blist.size();
        js.put("BudgetListLength",lenB);
        JSONArray budgetListJson = new JSONArray();
        for (Budget b: blist) {
            JSONObject bjson = b.toJson();
            budgetListJson.put(bjson);
        }
        js.put("Budget limits",budgetListJson);

        int len = rlist.size();
        js.put("recordLength",len);
        JSONArray recordListJson = new JSONArray();
        for (Record r: rlist) {
            JSONObject rjson = r.toJson();
            recordListJson.put(rjson);
        }
        js.put("records",recordListJson);
        js.put("Account balance",balance);

        saveToFile(js.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }


    // MODIFIES: this
    // EFFECTS: writes the JSON string to the file
    private void saveToFile(String json) {
        if (writer != null) {
            writer.print(json);
        }
    }
}