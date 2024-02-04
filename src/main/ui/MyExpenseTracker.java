package ui;

import model.Expense;
import model.Account;
import model.Budget;
import model.Month;
import model.Record;

import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ExpenseTracker Application
public class MyExpenseTracker {
    private static final String JSON_STORE = "./data/records.json";
    private Account pers;
    private Scanner input;
    private ArrayList<Record> records = new ArrayList<>();
    private ArrayList<Budget> budgets = new ArrayList<>();
    private Budget budget;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private double balance;

    // EFFECTS: runs the  application
    public MyExpenseTracker() throws FileNotFoundException {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runTracker();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runTracker() {
        boolean keepGoing = true;
        String command = null;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nTHE END");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        if (command.equals("a")) {
            addExpense();
        } else if (command.equals("b")) {
            setBudget();
        } else if (command.equals("s")) {
            saveExpenses();
        } else if (command.equals("l")) {
            loadExpenses();
        } else {
            System.out.println("Selection not valid...");
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes accounts
    private void init() {
        pers = new Account("Shah", 6000.00);
        //bus = new Account("Shah", 9000.0);
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        System.out.println("\nExpense Tracker Menu:");
        System.out.println("\nSelect from:");
        //System.out.println("\tv -> View Expenses for a month");
        System.out.println("\ta -> Add Expenses for a Month");
        System.out.println("\tb -> Set Budget for a Month");
        System.out.println("\ts -> Save All Data");
        System.out.println("\tl -> Load Data for a Month");
        System.out.println("\tq -> quit");
    }

    // EFFECTS: prints all the expense records for the particular month
    /*public void viewExpenses() {
        System.out.print("Enter the month (e.g., JAN): ");
        input.nextLine();
        String monthStr = input.nextLine();
        Month month = Month.valueOf(monthStr);

        Record currentRecord = null;
        for (Record record : records) {
            if (record.getMonth() == month) {
                currentRecord = record;
                break;
            }
        }
        if (currentRecord == null) {
            System.out.println("No expenses recorded for " + month + ".");
        } else {
            displayExpenses(currentRecord);
        }
        System.out.println("Total Expense = $" + calculateTotalExpense(month));
        if (budget != null) {
            if (calculateTotalExpense(month) > budget.getLimit()) {
                System.out.println("WARNING! Total expenses exceed the budget of $" + budget.getLimit());
            }
        }
    }*/

    // EFFECTS: parses a list of records and returns the record for the given method
    private Record findRecordForMonth(Month month) {
        for (Record record : records) {
            if (record.getMonth() == month) {
                return record;
            }
        }
        return null;
    }

    // Displays/prints all expenses in a given record
    private void displayExpenses(Record record) {
        ArrayList<Expense> expenses = record.getExpenses();
        System.out.println("Expenses for " + record.getMonth() + ":");

        for (Expense expense : expenses) {
            System.out.println("Date: " + expense.getDate() + ", Description: " + expense.getDescription()
                    + ", Amount: " + expense.getAmount());
        }
    }



    // MODIFIES : this
    // EFFECTS : Add an expense to the list of expenses for that month and deduct that amount from the account balance.
    //           also return the account balance
    public void addExpense() {
        Account selected = selectAccount();
        System.out.print("Enter the month (e.g., JAN): ");
        input.nextLine();
        String monthStr = input.nextLine();
        Month month = Month.valueOf(monthStr);

        Record currentRecord = findOrCreateRecord(month);

        if (currentRecord != null) {
            System.out.print("Enter the date (e.g., 2023-01-01): ");
            String date = input.nextLine();
            System.out.print("Enter expense description: ");
            String description = input.nextLine();
            System.out.print("Enter expense amount: ");
            double amount = input.nextDouble();
            Expense expense = new Expense(date, description, amount);
            /*if (amount >= 0.0) {
                selected.withdraw(amount);
                this.balance = selected.getBalance();
                currentRecord.addExpense(expense);
                System.out.println("Expense recorded successfully");
            } else {
                System.out.println("Cannot record negative amount...\n");
            } */
            selected.withdraw(amount);
            this.balance = selected.getBalance();
            currentRecord.addExpense(expense);
            System.out.println("Expense recorded successfully");
            printBalance(selected);
        }
    }

    // EFFECT : returns record for a given month if present. Else, creates a new record.
    private Record findOrCreateRecord(Month month) {
        Record currentRecord = findRecordForMonth(month);
        if (currentRecord == null) {
            ArrayList<Expense> transactions = new ArrayList<>();
            currentRecord = new Record(month, transactions);
            records.add(currentRecord);
        }
        return currentRecord;
    }

    // MODIFIES : this
    // EFFECT : assign a budget limit as an amount for a particular month.
    public void setBudget() {
        System.out.print("Enter the month (e.g., JAN): ");
        input.nextLine();
        String monthStr = input.nextLine();
        Month month = Month.valueOf(monthStr);

        System.out.println("Set your budget/limit: $");
        double amount = input.nextDouble();
        if (!budgets.isEmpty()) {
            for (Budget b: budgets) {
                if (b.getMonth() == month) {
                    budgets.remove(b);
                    budgets.add(new Budget(month,amount));
                }
            }
        } else {
            budgets.add(new Budget(month,amount));
        }
        System.out.println("Budget limit set for " + month + ": " + amount);

    }

    // EFFECT : Calculates the total expense in a given month.
    private double calculateTotalExpense(Month month) {
        double totalExpenses = 0.0;
        Record currentRecord = null;

        for (Record r : records) {
            if (r.getMonth() == month) {
                currentRecord = r;
                break;
            }
        }

        if (currentRecord != null) {
            ArrayList<Expense> expenses = currentRecord.getExpenses();

            for (Expense e : expenses) {
                totalExpenses += e.getAmount();
            }

            return totalExpenses;
        } else {
            return 0;
        }
    }

    // EFFECTS: prompts user to select personal or business account and returns it
    private Account selectAccount() {
        /*String sel = "";  // force entry into loop

        while (!(sel.equals("p") || sel.equals("b"))) {
            System.out.println("p for personal");
            System.out.println("b for business");
            sel = input.next();
            sel = sel.toLowerCase();
        }

        if (sel.equals("p")) {
            return pers;
        } else {
            return bus;
        }*/
        return pers;
    }

    // EFFECTS: prints balance of account to the screen
    private void printBalance(Account sel) {
        System.out.printf("Balance: $%.2f\n", sel.getBalance());
    }


    // EFFECT: saves Record of a particular month to the JSON file.
    public void saveExpenses() {



        //
//        System.out.print("Enter the month (e.g., JAN): ");
//        input.nextLine();
//        String monthStr = input.nextLine();
//        Month month = Month.valueOf(monthStr);
//        Record currentRecord = findOrCreateRecord(month);

        try {
            jsonWriter.open();
            jsonWriter.write(records,budgets,balance);
            jsonWriter.close();
            System.out.println("Saved to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads Record from file
    public void loadExpenses() {
        System.out.print("Enter the month (e.g., JAN): ");
        input.nextLine();
        String monthStr = input.nextLine();
        Month month = Month.valueOf(monthStr);

        /*try {
            records = (ArrayList<Record>) jsonReader.read();
            Record currentRecord = null;
            Budget loadedBudget = null;
            for (Record r: records) {
                if (r.getMonth() == month) {
                    currentRecord = r;
                    break;
                }
            }
            if (currentRecord == null) {
                System.out.println("No record found for this month");
            } else {
                displayExpenses(currentRecord);
                System.out.println("Loaded from " + JSON_STORE);
            }

        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }*/

        try {
            List<Record> loadedRecords = jsonReader.readRecords();
            List<Budget> loadedBudgets = jsonReader.readBudgets();

            Record currentRecord = null;
            Budget loadedBudget = null;


//            for (Record record : loadedRecords) {
//                if (record.getMonth() == month) {
//                    currentRecord = record;
//                    break;
//                }
//            }
            parser(month, loadedRecords, currentRecord);

//            for (Budget budget : loadedBudgets) {
//                if (budget.getMonth() == month) {
//                    loadedBudget = budget;
//                    break;
//                }
//            }
            parserb(month,loadedBudgets,loadedBudget);

            if (currentRecord == null) {
                System.out.println("No record found for this month");
            } else {
                displayExpenses(currentRecord);
                if (loadedBudget != null) {
                    System.out.println("Loaded budget for " + month + ": " + loadedBudget.getLimit());
                }
                // System.out.println("Loaded from " + JSON_STORE);
            }
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    private void parser(Month month, List<Record> loadedRecords, Record currentRecord) {
        for (Record record : loadedRecords) {
            if (record.getMonth() == month) {
                currentRecord = record;
                break;
            }
        }
    }

    private void parserb(Month month, List<Budget> loadedBudgets, Budget loadedBudget) {
        for (Budget budget : loadedBudgets) {
            if (budget.getMonth() == month) {
                loadedBudget = budget;
                break;
            }
        }
    }


}
