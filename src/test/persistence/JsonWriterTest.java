package persistence;

import model.Record;
import model.Expense;
import model.Month;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonWriter;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;

public class JsonWriterTest {
    private JsonWriter jsonWriter;
    private ArrayList<Record> testRecords;

    @BeforeEach
    void runBefore() {
        jsonWriter = new JsonWriter("./data/testWriter.json");
        testRecords = new ArrayList<>();
        Record record1 = new Record(Month.JAN, new ArrayList<>());
        Record record2 = new Record(Month.FEB, new ArrayList<>());
        testRecords.add(record1);
        testRecords.add(record2);
    }

    @Test
    void testConstructor() {
        assertEquals("./data/testWriter.json", jsonWriter.getDestination());
    }

    @Test
    void testWriterInvalidFile() {
        try {
            Record record4 = new Record(Month.APR, new ArrayList<>());
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriteRecords() {
        try {
            Record record5 = new Record(Month.AUG, new ArrayList<>());
            record5.addExpense(new Expense("2023-10-02", "Movie",23.0));
            record5.addExpense(new Expense("2023-10-15", "Dining",31.0));
            JsonWriter writer = new JsonWriter("./data/testWriterRecords.json");
            writer.open();
            writer.write(testRecords,new ArrayList<>(), 500.0);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterRecords.json");
            //record5 = reader.read().get(0);
            List<Record> readRecords = reader.readRecords(); // Updated here
            Record retrievedRecord = readRecords.get(0); // Assuming one record was written
            //assertEquals(Month.AUG, record5.getMonth());
            assertEquals(Month.JAN, retrievedRecord.getMonth());
            assertEquals(0, retrievedRecord.getExpenses().size()); // No expenses added
            //ArrayList<Expense> transactions = record5.getExpenses();
            //assertEquals(2, transactions.size());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

/*
    @Test
    void testWriteEmptyRecords() {
        try {
            Record record3 = new Record(Month.JUN, new ArrayList<>());
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyRecord.json");
            writer.open();
            writer.write(record3);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyRecord.json");
            record3 = reader.read();
            assertEquals(Month.JUN, record3.getMonth());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    } *//*

}

*/
}
