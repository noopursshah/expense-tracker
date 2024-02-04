package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;


import model.Budget;
import model.Month;
import model.Record;
import model.Account;
import model.Expense;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.awt.event.WindowAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


// GUI
public class MyAppGUI extends JFrame implements ActionListener {
    private static final String JSON_STORE = "./data/records.json";
    private JLabel titleLabel;
    private JLabel amountLabel;
    private JLabel descriptionLabel;
    private JLabel dateLabel;
    private JLabel balanceLabel;
    private JLabel imageLabel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JTextField dateField;
    private JTextField balanceField;
    private JButton addButton;
    private JButton removeButton;
    private JButton clearButton;
    private JTextArea expenseList;
    private JTextArea dataTextArea;
    private JComboBox<String> monthComboBox;
    private JMenuItem setBudget;
    private JMenuItem loadData;
    private JMenuItem sortExpenses;
    private JMenuItem undoExpense;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private List<Record> records;
    private List<Budget> budgets;
    private double balance;
    private Account personal;
    private WindowAdapter windowAdapter;
    private Stack<String> addedExpenses;

    // EFFECTS: Creates a GUI
    public MyAppGUI() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        /*try {
            records = jsonReader.readRecords();
        } catch (IOException e) {
            records = new ArrayList<>();
        }

        try {
            budgets = jsonReader.readBudgets();
        } catch (IOException e) {
            budgets = new ArrayList<>();
        }*/
        reading();

        personal = new Account("Shah",6000.0);
        addedExpenses = new Stack<>();
        try {
            this.balance = jsonReader.readBalance();
        } catch (IOException e) {
            this.balance = personal.getBalance();
        }

        jframing();

        inputscene();

        setImage();

        options();

        // Set the size and visibility of the window
        setSize(500, 500);
        setVisible(true);

        // Register the window listener
        windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closingfunction();
            }
        };

        addWindowListener(windowAdapter);
    }

    // Creates a new instance of the app
    public static void main(String[] args) {
        new MyAppGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addExpensekaro();
        } else if (e.getSource() == clearButton) {
            clearFields();
        } else if (e.getSource() == removeButton) {
            removing();
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
        // System.out.println("Expenses for " + record.getMonth() + ":");

        for (Expense expense : expenses) {
            dataTextArea.append(String.format("%s - %s - $%.2f\n", expense.getDate(), expense.getDescription(),
                    expense.getAmount()));
        }
    }

    //
    private void setBudgetDialog() {
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        model.Month month = model.Month.valueOf(selectedMonth);
        JFrame dialogFrame = new JFrame("Set Budget for " + selectedMonth);
        dialogFrame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter budget amount for " + selectedMonth + ": ");
        JTextField budgetField = new JTextField(10);

        JButton setButton = new JButton("Set");
        setButton.addActionListener(a -> {
            setButton(month, budgetField, dialogFrame);
        });


        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(budgetField);
        panel.add(setButton);

        dialogFrame.add(panel, BorderLayout.CENTER);
        dialogFrame.pack();
        dialogFrame.setLocationRelativeTo(null);
        dialogFrame.setVisible(true);
    }

    private void sortExpensesbyAmount() {
        String text = expenseList.getText();
        String[] expenses = text.split("\n");

        ArrayList<String> stringArrayList = new ArrayList<>();
        for (String expense : expenses) {
            if (!expense.trim().isEmpty()) {
                stringArrayList.add(expense);
            }
        }

        stringArrayList.sort(Comparator.comparingDouble(expense -> {
            String amountStr = expense.substring(expense.lastIndexOf('$') + 1);
            return Double.parseDouble(amountStr);
        }));

        StringBuilder sortedExpenses = new StringBuilder();
        for (String expense : stringArrayList) {
            sortedExpenses.append(expense).append("\n");
        }

        expenseList.setText(sortedExpenses.toString());
    }

    private void undoAction() {
        if (!addedExpenses.isEmpty()) {
            addedExpenses.pop(); // Remove the latest added expense
            expenseList.setText(""); // Clear the JTextArea
            for (String expense : addedExpenses) {
                expenseList.append(expense + "\n");
            }
        }
    }

    private void closingfunction() {
        int confirm = JOptionPane.showConfirmDialog(MyAppGUI.this, "Save data before closing?",
                "Save", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                jsonWriter.open();
                jsonWriter.write(records, budgets, balance);
                jsonWriter.close();
                model.EventLog.getInstance().logEvent(new model.Event("Saved all data"));

                printAllLoggedEvents();

                System.exit(0);
            } catch (FileNotFoundException f) {
                System.out.println("Unable to write to file: " + JSON_STORE);
            }
        } else {
            System.exit(0);
        }
    }

    private void addExpensekaro() {
        // Get the selected month from the combo box
        getComboMonth();

        // Get the input values
        String amount = amountField.getText();
        String description = descriptionField.getText();
        String date = dateField.getText();

        Record currentRecord = findOrCreateRecord(getComboMonth());

        // Validate the input values
        if (amount.isEmpty() || description.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (currentRecord != null) {
                try {
                    trying(amount, description, date, currentRecord);
                    model.EventLog.getInstance().logEvent(new model.Event("Expense added"));
                    clearFields();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadingData() {
        getComboMonth();

        JFrame dataFrame = new JFrame("Data");
        dataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dataFrame.setSize(400, 300);

        int confirm = JOptionPane.showConfirmDialog(MyAppGUI.this, "Load Data for "
                + getComboMonth() + "?", "Load", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            trycatch(dataFrame);
            model.EventLog.getInstance().logEvent(new model.Event("Data has been loaded for " + getComboMonth()));
        }

    }

    private void jframing() {
        // Set the title of the window
        setTitle("Expense Tracker");

        // Create and set the layout of the window
        setLayout(new BorderLayout());

        // Create and add the title label to the top of the window
        titleLabel = new JLabel("Expense Tracker", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Create and add the expense list to the center of the window
        expenseList = new JTextArea();
        expenseList.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(expenseList);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void inputscene() {
        // Create and add the input fields and buttons to the bottom of the window
        JPanel inputPanel = new JPanel(new GridLayout(2, 4));

        balanceLabel = new JLabel("Balance: $ " + this.balance);
        balanceLabel.setBounds(10, 260, 100, 25);
        add(balanceLabel, BorderLayout.NORTH);

        addtoInput(inputPanel);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void setImage() {
        // Load the image (change 'path/to/image.png' to your image file path)
        ImageIcon imageIcon = new ImageIcon("./data/tick.jpeg");

        // Create a JLabel to display the image
        imageLabel = new JLabel(imageIcon);
        // Add the JLabel to your window at a suitable position
        imageLabel.setPreferredSize(new Dimension(300, 200));
    }

    private void options() {
        creating();

        loadData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingData();
            }
        });


        // Create "Sort Expenses" menu item
        sortExpenses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortExpensesbyAmount();
                model.EventLog.getInstance().logEvent(new model.Event("Expenses sorted by Amount"));
            }
        });



        // Create "Undo expense" menu item
        undoExpense.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undoAction();
                model.EventLog.getInstance().logEvent(new model.Event("Undo add expense"));
            }
        });


    }

    private void clearFields() {
        // Clear the input fields
        amountField.setText("");
        descriptionField.setText("");
        dateField.setText("");
    }

    private void removing() {

        getComboMonth();

        // Get the input values
        String amount = amountField.getText();
        String description = descriptionField.getText();
        String date = dateField.getText();

        Record currentRecord = findRecordForMonth(getComboMonth());


        // Validate the input values
        if (amount.isEmpty() || description.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (currentRecord == null) {
            JOptionPane.showMessageDialog(this, "Please enter an existing record",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (currentRecord != null) {
                trycatchagain(amount, description, date, currentRecord);
                model.EventLog.getInstance().logEvent(new model.Event("Expense removed"));
            }
        }
    }

    private void setButton(Month month, JTextField budgetField, JFrame dialogFrame) {
        JButton setButton = new JButton("Set");
        setButton.addActionListener(a -> {
            String budgetAmountStr = budgetField.getText();
            if (!budgetAmountStr.isEmpty()) {
                double budgetAmount = Double.parseDouble(budgetAmountStr);
                if (!budgets.isEmpty()) {
                    for (Budget b: budgets) {
                        if (b.getMonth() == month) {
                            budgets.remove(b);
                            budgets.add(new Budget(month,budgetAmount));
                        }
                    }
                } else {
                    budgets.add(new Budget(month,budgetAmount));
                }
            }
            dialogFrame.dispose();
        });
    }

    private Month getComboMonth() {
        // Get the selected month from the combo box
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        model.Month month = model.Month.valueOf(selectedMonth);
        return month;
    }

    private void addtoInput(JPanel inputPanel) {

        addComboBoxtoPanel(inputPanel);

        amountLabel = new JLabel("Amount:");
        inputPanel.add(amountLabel);

        amountField = new JTextField();
        inputPanel.add(amountField);

        descriptionLabel = new JLabel("Description:");
        inputPanel.add(descriptionLabel);

        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        dateLabel = new JLabel("Date:");
        inputPanel.add(dateLabel);

        dateField = new JTextField();
        inputPanel.add(dateField);

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        inputPanel.add(addButton);

        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        inputPanel.add(removeButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        inputPanel.add(clearButton);
    }

    private void addComboBoxtoPanel(JPanel inputPanel) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
                "NOV", "DEC"};
        monthComboBox = new JComboBox<>(months);
        inputPanel.add(monthComboBox);
    }

    private void creating() {
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Create "Options" menu
        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        // Create "Set Budget" menu item
        setBudget = new JMenuItem("Set Budget");
        setBudget.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                setBudgetDialog();
                model.EventLog.getInstance().logEvent(new model.Event("Budget limit set for " + getComboMonth()));

            }
        });
        optionsMenu.add(setBudget);

        loadData = new JMenuItem("Load Data For Month");
        optionsMenu.add(loadData);

        sortExpenses = new JMenuItem("Sort Expenses by Amount");
        optionsMenu.add(sortExpenses);

        undoExpense = new JMenuItem("Undo expense");
        optionsMenu.add(undoExpense);

    }

    private void trying(String amount, String date, String description, Record currentRecord) {
        double amountValue = Double.parseDouble(amount);
        Expense expense = new Expense(date, description, amountValue);
        this.balance -= amountValue;
        currentRecord.addExpense(expense);
        personal.withdraw(-amountValue);
        for (Record record : records) {
            System.out.println(record.toJson().toString());
        }
        //records.add(currentRecord);

        // Add the expense to the list
        expenseList.append(String.format("%s - %s - %s - $%.2f\n", getComboMonth(), date, description,
                amountValue));
        addedExpenses.push(String.format("%s - %s - %s - $%.2f\n", getComboMonth(), date, description,
                amountValue));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        // Add the panel to the existing JFrame
        add(panel);

        JOptionPane.showMessageDialog(
                MyAppGUI.this,
                panel,
                "Successfully Added",
                JOptionPane.PLAIN_MESSAGE);
        //add(imageLabel, BorderLayout.CENTER); // Example: Add it to the left side
        balanceLabel.setText("Balance: $" + this.balance); // Update the balanceField

    }

    private void trycatch(JFrame dataFrame) {
        try {
            List<Record> loadedRecords = jsonReader.readRecords();
            List<Budget> loadedBudgets = jsonReader.readBudgets();

            dataTextArea = new JTextArea();
            dataTextArea.setEditable(false);
            dataTextArea.append("Data for " + getComboMonth() + "\n");
            dataFrame.add(new JScrollPane(dataTextArea), BorderLayout.CENTER);
            dataFrame.setVisible(true);

            Record currentRecord = null;
            Budget loadedBudget = null;


            //parser(loadedRecords,currentRecord);
            currentRecord = parser(loadedRecords,currentRecord);


            loadedBudget = parserb(loadedBudgets,loadedBudget);

            if (currentRecord == null) {
                dataTextArea.append("No record found for this month");
            } else {
                displayExpenses(currentRecord);
                checkNull(loadedBudget,dataTextArea);
            }
        } catch (IOException i) {
            dataTextArea.append("Unable to read from file: " + JSON_STORE);
        }
    }

    private void trycatchagain(String amount, String date, String description, Record currentRecord) {
        try {
            // Parse the amount as a double
            double amountValue = Double.parseDouble(amount);
            Expense expense = new Expense(date, description, amountValue);
            this.balance += amountValue;
            currentRecord.addExpense(expense);
            personal.withdraw(amountValue);

            // Remove the expense from the list
            expenseList.setText(expenseList.getText().replaceFirst(Pattern.quote(
                    String.format("%s - %s - %s - $%.2f\n", getComboMonth(), date, description,
                            amountValue)), ""));
            ArrayList<String> addedExpensesList = new ArrayList<>(addedExpenses);
            addedExpensesList.remove(addedExpensesList.indexOf(String.format(
                    "%s - %s - %s - $%.2f\n", getComboMonth(), date, description,
                    amountValue)));
            addedExpenses = new Stack<>();
            addedExpenses.addAll(addedExpensesList);
            addtheImage();
            balanceLabel.setText("Balance: $" + this.balance); // Update the balanceField


            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Record parser(List<Record> loadedRecords, Record currentRecord) {
        for (Record record : loadedRecords) {
            if (record.getMonth() == getComboMonth()) {
                currentRecord = record;
                // break;
            }
        }
        return currentRecord;
    }

    private Budget parserb(List<Budget> loadedBudgets, Budget loadedBudget) {
        for (Budget budget : loadedBudgets) {
            if (budget.getMonth() == getComboMonth()) {
                loadedBudget = budget;

                // break;
            }
        }
        return loadedBudget;
    }

    private void checkNull(Budget loadedBudget, JTextArea dataTextArea) {
        if (loadedBudget != null) {
            dataTextArea.append("Loaded budget for " + getComboMonth() + ": "
                    + loadedBudget.getLimit());
        }

    }

    private void addtheImage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        // Add the panel to the existing JFrame
        add(panel);

        JOptionPane.showMessageDialog(
                MyAppGUI.this,
                panel,
                "Successfully removed",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void printAllLoggedEvents() {
        model.EventLog eventLog = model.EventLog.getInstance();
        System.out.println("All logged events:");
        for (model.Event event : eventLog) {
            System.out.println(event.getDescription() + " at " + event.getDate());
        }
    }

    private void reading() {
        try {
            records = jsonReader.readRecords();
        } catch (IOException e) {
            records = new ArrayList<>();
        }

        try {
            budgets = jsonReader.readBudgets();
        } catch (IOException e) {
            budgets = new ArrayList<>();
        }
    }


}
