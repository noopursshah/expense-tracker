package persistence;

import model.Expense;
import model.Month;
import model.Record;
import model.Budget;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;



class JsonReaderTest extends JsonTest {
    private JsonReader jsonReader;
    private File testFile;
    private String sourceFilePath = "testData/testRecords.json";

    @Test
    void testReadValidFile() {
        try {
            jsonReader = new JsonReader(sourceFilePath);

            List<Record> records = jsonReader.readRecords();
            List<Budget> budgets = jsonReader.readBudgets();
            assertNotNull(records);
            assertNotNull(budgets);
            assertEquals(2, records.size());
            assertEquals(2, budgets.size());

            Budget budget1 = budgets.get(0);
            assertEquals(Month.JAN, budget1.getMonth());
            assertEquals(200.0, budget1.getLimit());

            Budget budget2 = budgets.get(1);
            assertEquals(Month.FEB, budget2.getMonth());
            assertEquals(150.0, budget2.getLimit());

            // Check the first record of JAN
            Record janRecord = records.get(0);
            assertEquals(Month.JAN, janRecord.getMonth());
            assertEquals(2, janRecord.getExpenses().size());

            // Check the second record of FEB
            Record febRecord = records.get(1);
            assertEquals(Month.FEB, febRecord.getMonth());
            assertEquals(1, febRecord.getExpenses().size());

            // Check the expenses for JAN
            Expense janExpense1 = janRecord.getExpenses().get(0);
            assertEquals("2023-01-15", janExpense1.getDate());
            assertEquals("Groceries", janExpense1.getDescription());
            assertEquals(50.0, janExpense1.getAmount());

            Expense janExpense2 = janRecord.getExpenses().get(1);
            assertEquals("2023-01-20", janExpense2.getDate());
            assertEquals("Dining out", janExpense2.getDescription());
            assertEquals(30.0, janExpense2.getAmount());

            // Check the expenses for FEB
            Expense febExpense = febRecord.getExpenses().get(0);
            assertEquals("2023-02-10", febExpense.getDate());
            assertEquals("Shopping", febExpense.getDescription());
            assertEquals(75.0, febExpense.getAmount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadInvalidFile() {
        // Modify the JSON data to make it invalid
        try {
            String json = "{\"records\":[{\"month\":\"JANUARY\",\"expenses\":[{\"date\":\"2023-01-15\",\"details\":\"Groceries\",\"amount\":50.0},"
                    + "{\"date\":\"2023-01-20\",\"details\":\"Dining out\",\"amount\":\"invalidAmount\"}]}," +
                    "{\"month\":\"FEBRUARY\",\"expenses\":[{\"date\":\"2023-02-10\",\"details\":\"Shopping\",\"amount\":75.0}]}]}";
            Files.write(Paths.get(sourceFilePath), json.getBytes());

            jsonReader = new JsonReader(sourceFilePath);

            List<Record> records = jsonReader.readRecords();
            assertNotNull(records);
            assertEquals(2, records.size());

            // The second expense in the JAN record should fail to parse, but the first one should be added.
            assertEquals(1, records.get(0).getExpenses().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadNonExistentFile() {
        // Provide a path to a non-existent file
        JsonReader nonExistentJsonReader = new JsonReader("non_existent_file.json");

        try {
            nonExistentJsonReader.readRecords();
            fail("Exception expected.");
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Passed.");
        }
    }
}
