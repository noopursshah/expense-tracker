package model;

//Represents an account created for a user
public class Account {
    private String name;                   // the account owner's name
    private int id;                        // account id
    private double balance;                // the balance amount of the account
    private static int nextaccId = 1;      // tracks id of next account created

    /*
     * REQUIRES: accName has string length > 0
     * EFFECTS: name on account is set to accName;
     *          account id is a unique number assigned to the account;
     *          if initialB >= 0 then balance = initialB else balance = 0.
     */
    public Account(String accName, double initialB) {
        id = nextaccId++;
        name = accName;
        if (initialB >= 0) {
            balance = initialB;
        } else {
            balance = 0;
        }
    }

    /*
     * REQUIRES: amount >= 0 and amount <= getBalance()
     * MODIFIES: this
     * EFFECTS: A withdrawal is made from the account and the balance is updated
     */
    public double withdraw(double amount) {
        if (amount < balance) {
            balance = balance - amount;
            return balance;
        } else {
            return balance;
        }
    }

    //Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }


    /*
     * EFFECTS: returns a string representation of account
     */
    @Override
    public String toString() {
        String balanceStr = String.format("%.2f", balance);  // get balance to 2 decimal places as a string
        return "[ id = " + id + ", name = " + name + ", "
                + "balance = $" + balanceStr + "]";
    }
}

