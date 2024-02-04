package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account testAccount;

    @BeforeEach
    void runBefore() {
        testAccount = new Account("Shiv", 350.0);
    }

    @Test
    void testConstructor() {
        assertEquals("Shiv", testAccount.getName());
        assertEquals(350.0, testAccount.getBalance());
        assertTrue(testAccount.getId() > 0);
        assertFalse(testAccount.getName() == "Priya");
    }

    @Test
    void testNegativeBalance() {
        testAccount = new Account("Lucas", -450.0);
        assertEquals("Lucas", testAccount.getName());
        assertEquals(0, testAccount.getBalance());
        assertTrue(testAccount.getId() > 0);
        assertFalse(testAccount.getBalance() < 0);
    }

    @Test
    void testWithdraw() {
        testAccount.withdraw(150.50);
        assertEquals(199.50, testAccount.getBalance());
    }

    @Test
    void testMultipleWithdrawals() {
        testAccount.withdraw(150.0);
        testAccount.withdraw(22.0);
        assertEquals(178.0, testAccount.getBalance());
    }

    @Test
    void testMultipleWithdrawalswithAmountmorethanBalance() {
        testAccount.withdraw(150.0);
        testAccount.withdraw(220.0);
        assertEquals(200.0, testAccount.getBalance());
    }

    @Test
    void testToString() {
        assertTrue(testAccount.toString().contains("name = Shiv, balance = $350.00"));
    }
}