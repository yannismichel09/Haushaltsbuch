package haushaltsbuch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dbaccess.DBAccessCategory;
import dbaccess.DBAccessTransaction;
import dbaccess.DBAccessUser;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import jakarta.transaction.Transactional;
import model.Category;
import model.Transaction;
import model.User;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
public class DBAccessTestTransaction {

    @Autowired
    private DBAccessTransaction dbAccess;
    
    @Autowired
    private DBAccessUser dbAccessUser;
    
    @Autowired
    private DBAccessCategory dbAccessCategory;

    // Testet das Abrufen aller Transaktionen aus der Datenbank
    @Test
    void testGetAllTransactions() {
        Transaction transaction1 = dbAccess.createTransaction(1, 1, 100.0, "2026-05-05", 
                                                              "spending", "Test Transaction", "monthly");
        Transaction transaction2 = dbAccess.createTransaction(2, 1, 300.0, "2026-05-07", 
                                                              "spending", "Test Transaction2", "monthly");
        List<Transaction> transactions = dbAccess.getAllTransactions();
        assertNotNull(transaction1);
        assertNotNull(transaction2);
        assertNotNull(transactions.size());
        assertEquals(2, transactions.size());
    }

    // Testet das Abrufen einer vorhandenen Transaktion anhand ihrer Id
    @Test
    void testGetTransactionById() {
        Transaction transaction = dbAccess.createTransaction(1, 1, 100.0, "2026-05-05", 
                                                             "spending", "Test Transaction", "monthly");
        Transaction transaction2 = dbAccess.getTransactionById(transaction.getTransactionId());
        assertNotNull(transaction2);
        assertEquals(transaction, transaction2);
        assertEquals(transaction.getTransactionId(), transaction2.getTransactionId());
    }

    // Testet das Abrufen einer nicht vorhandenen Transaktion anhand ihrer Id
    @Test
    void testGetTransactionByIdNotFound() {
        Transaction transaction = dbAccess.getTransactionById(99999);
        assertNull(transaction);
    }

    // Testet das Filtern von Transaktionen
    @Test
    void testGetFilteredTransactions() {
        User targetUser = dbAccessUser.createUser("testUserFilterTransaction", "1234", "test@filter-transaction.com");
        User otherUser = dbAccessUser.createUser("testUserFilterTransactionOther", "1234", "test@filter-transaction-other.com");
        Category targetCategory = dbAccessCategory.createCategory("testCategoryFilterTransaction", "Filter Category", "#123456", 400.0);
        Category otherCategory = dbAccessCategory.createCategory("testCategoryFilterTransactionOther", "Other Filter Category", "#654321", 400.0);

        Transaction matchingTransaction = dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 125.0,
            "2026-10-15", "spending", "Monthly grocery shopping", "monthly");
        dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 80.0,
            "2026-10-15", "income", "Monthly grocery refund", "monthly");
        dbAccess.createTransaction(targetUser.getUserId(), otherCategory.getCategoryId(), 125.0,
            "2026-10-15", "spending", "Monthly grocery shopping", "monthly");
        dbAccess.createTransaction(otherUser.getUserId(), targetCategory.getCategoryId(), 125.0,
            "2026-10-15", "spending", "Monthly grocery shopping", "monthly");
        dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 125.0,
            "2026-11-15", "spending", "Monthly grocery shopping", "weekly");

        List<Transaction> filteredTransactions = dbAccess.getFilteredTransactions(
            matchingTransaction.getTransactionId(),
            targetUser.getUserId(),
            targetCategory.getCategoryId(),
            120.0,
            130.0,
            "2026-10-01",
            "2026-10-31",
            "spending",
            "grocery",
            "monthly");

        assertNotNull(filteredTransactions);
        assertEquals(1, filteredTransactions.size());
        assertEquals(matchingTransaction.getTransactionId(), filteredTransactions.get(0).getTransactionId());
        assertEquals("Monthly grocery shopping", filteredTransactions.get(0).getTransactionDescription());
    }

    // Testet das Filtern von Transaktionen mit mehreren Treffern
    @Test
    void testGetFilteredTransactionsMultipleResults() {
        User targetUser = dbAccessUser.createUser("testUserFilterMulti", "1234", "test@filter-multi.com");
        User otherUser = dbAccessUser.createUser("testUserFilterMultiOther", "1234", "test@filter-multi-other.com");
        Category targetCategory = dbAccessCategory.createCategory("testCategoryFilterMulti", "Filter Multi Category", "#ABCDEF", 700.0);

        Transaction matchingTransaction1 = dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 90.0,
            "2026-12-01", "spending", "Bus ticket work", "monthly");
        Transaction matchingTransaction2 = dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 110.0,
            "2026-12-12", "spending", "Fuel for work", "monthly");

        dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 70.0,
            "2026-12-08", "spending", "Groceries", "monthly");
        dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 95.0,
            "2026-12-10", "income", "Work refund", "monthly");
        dbAccess.createTransaction(otherUser.getUserId(), targetCategory.getCategoryId(), 100.0,
            "2026-12-15", "spending", "Fuel for work", "monthly");

        List<Transaction> filteredTransactions = dbAccess.getFilteredTransactions(
            null,
            targetUser.getUserId(),
            targetCategory.getCategoryId(),
            80.0,
            120.0,
            "2026-12-01",
            "2026-12-31",
            "spending",
            "work",
            "monthly");

        assertNotNull(filteredTransactions);
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.stream()
            .anyMatch(transaction -> transaction.getTransactionId().equals(matchingTransaction1.getTransactionId())));
        assertTrue(filteredTransactions.stream()
            .anyMatch(transaction -> transaction.getTransactionId().equals(matchingTransaction2.getTransactionId())));
    }

    // Testet das Löschen einer vorhandenen Transaktion
    @Test
    void testDeleteTransaction() {
        Transaction transaction = dbAccess.createTransaction(3, 3, 499.0, "2026-06-08", 
                                                             "spending", "Test Delete Transaction", "monthly");
        boolean deleted = dbAccess.deleteTransaction(transaction.getTransactionId());
        assertTrue(deleted);
        assertNull(dbAccess.getTransactionById(transaction.getTransactionId()));
    }

    // Testet das Löschen einer nicht vorhandenen Transaktion
    @Test
    void testDeleteTransactionNotFound() {
        boolean deleted = dbAccess.deleteTransaction(88888);
        assertFalse(deleted);
    }

    // Testet das Summieren aller Transaktionen, die eine Ausgabe sind
    @Test
    void testSumTransactionsSpendings() {
        Transaction transaction = dbAccess.createTransaction(5, 4, 140.0, "2026-08-08", 
                                                             "spending", "Test Sum Transactions Spendings", "monthly");
        Transaction transaction2 = dbAccess.createTransaction(9, 4, 30.0, "2026-08-08", 
                                                             "spending", "Test Sum Transactions Spendings2", "monthly");
        assertNotNull(transaction);
        assertNotNull(transaction2);
        Double sum = dbAccess.sumTransactionsSpendings();
        assertEquals(170.0, sum);
    }

    // Testet das Summieren aller Transaktionen, die eine Einnahme sind
    @Test
    void testSumTransactionsIncome() {
        Transaction transaction = dbAccess.createTransaction(5, 4, 250.0, "2026-08-08",
                "income", "Test Sum Transactions Income", "monthly");
        Transaction transaction2 = dbAccess.createTransaction(9, 4, 50.0, "2026-08-08",
                "income", "Test Sum Transactions Income2", "monthly");
        Transaction transaction3 = dbAccess.createTransaction(9, 4, 10.0, "2026-08-08",
                "spending", "Test Sum Transactions Income3", "monthly");
        assertNotNull(transaction);
        assertNotNull(transaction2);
        assertNotNull(transaction3);
        Double sum = dbAccess.sumTransactionsIncome();
        assertEquals(300.0, sum);
    }

    // Testet das Summieren aller Ausgaben einer Kategorie
    @Test
        void testSumCategorySpending() {
        User user = dbAccessUser.createUser("testUserSumCategory", "testPassword123", "testsumcategory@test.com");
        Category targetCategory = dbAccessCategory.createCategory("testCategorySumTarget", "Target Category", "blue", 500.0);
        Category otherCategory = dbAccessCategory.createCategory("testCategorySumOther", "Other Category", "yellow", 500.0);

        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 80.0, "2026-08-10",
                "spending", "Target spending", "monthly");
        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 20.0, "2026-08-11",
                "income", "Target income", "monthly");
        dbAccess.createTransaction(user.getUserId(), otherCategory.getCategoryId(), 999.0, "2026-08-12",
                "spending", "Other spending", "monthly");

        Double sum = dbAccess.sumCategorySpendings(targetCategory.getCategoryId());
        assertEquals(80.0, sum);
    }

    // Testet das Summieren der Ausgaben einer Kategorie ohne passende Transaktionen
    @Test
    void testSumCategorySpendingNoTransactions() {
        Category category = dbAccessCategory.createCategory("testCategorySpendingEmpty", "Empty Spending Category", "gray", 200.0);

        Double sum = dbAccess.sumCategorySpendings(category.getCategoryId());
        assertEquals(0.0, sum);
    }

    // Testet das Summieren aller Einnahmen einer Kategorie
    @Test
        void testSumCategoryIncome() {
        User user = dbAccessUser.createUser("testUserSumCategoryIncome", "testPassword123", "testsumcategoryincome@test.com");
        Category targetCategory = dbAccessCategory.createCategory("testCategoryIncomeTarget", "Income Target Category", "green", 500.0);
        Category otherCategory = dbAccessCategory.createCategory("testCategoryIncomeOther", "Income Other Category", "orange", 500.0);

        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 55.0, "2026-08-13",
            "income", "Target income 1", "monthly");
        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 45.0, "2026-08-14",
            "income", "Target income 2", "monthly");
        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 12.0, "2026-08-15",
            "spending", "Target spending", "monthly");
        dbAccess.createTransaction(user.getUserId(), otherCategory.getCategoryId(), 999.0, "2026-08-16",
            "income", "Other income", "monthly");

        Double sum = dbAccess.sumCategoryIncome(targetCategory.getCategoryId());
        assertEquals(100.0, sum);
    }

    // Testet das Summieren der Einnahmen einer Kategorie ohne passende Transaktionen
    @Test
    void testSumCategoryIncomeNoTransactions() {
        Category category = dbAccessCategory.createCategory("testCategoryIncomeEmpty", "Empty Income Category", "black", 200.0);

        Double sum = dbAccess.sumCategoryIncome(category.getCategoryId());
        assertEquals(0.0, sum);
    }

    // Testet das Erstellen einer neuen Transaktion
    @Test
    void testCreateTransaction() {
        User user = dbAccessUser.createUser("testUserForTransaction", "1234", "test@transaction.com");
        Category category = dbAccessCategory.createCategory("testCategoryForTransaction", "Test Category for Transaction", "#FF0000", 500.0);
        
        Transaction transaction = dbAccess.createTransaction(user.getUserId(), category.getCategoryId(), 150.0, "2026-07-10", 
                                                             "spending", "Test Create Transaction", "monthly");
        assertNotNull(transaction);
        assertNotNull(transaction.getTransactionId());
        assertEquals(user.getUserId(), transaction.getUser().getUserId());
        assertEquals(category.getCategoryId(), transaction.getCategory().getCategoryId());
        assertEquals(150.0, transaction.getTransactionAmount());
        assertEquals("2026-07-10", transaction.getTransactionDate());
        assertEquals("spending", transaction.getTransactionType());
        assertEquals("Test Create Transaction", transaction.getTransactionDescription());
        assertEquals("monthly", transaction.getTransactionFrequency());
    }

    // Testet das erfolgreiche Aktualisieren einer vorhandenen Transaktion
    @Test
    void testUpdateTransaction() {
        User user = dbAccessUser.createUser("testUserForUpdate", "1234", "test@update.com");
        Category category = dbAccessCategory.createCategory("testCategoryForUpdate", "Test Category for Update", "#00FF00", 300.0);

        Transaction transaction = dbAccess.createTransaction(user.getUserId(), category.getCategoryId(), 200.0, "2026-09-01",
                "spending", "Original Description", "monthly");

        boolean updated = dbAccess.updateTransaction(transaction.getTransactionId(), null, null, 350.0, "2026-09-15",
                "income", "Updated Description", "weekly");

        assertTrue(updated);
        Transaction updatedTransaction = dbAccess.getTransactionById(transaction.getTransactionId());
        assertNotNull(updatedTransaction);
        assertEquals(350.0, updatedTransaction.getTransactionAmount());
        assertEquals("2026-09-15", updatedTransaction.getTransactionDate());
        assertEquals("income", updatedTransaction.getTransactionType());
        assertEquals("Updated Description", updatedTransaction.getTransactionDescription());
        assertEquals("weekly", updatedTransaction.getTransactionFrequency());
    }

    // Testet das Aktualisieren einer nicht vorhandenen Transaktion
    @Test
    void testUpdateTransactionNotFound() {
        boolean updated = dbAccess.updateTransaction(99999, null, null, 100.0, null, null, null, null);
        assertFalse(updated);
    }

    // Testet das Abrufen aller Transaktionen, die eine bestimmte Häufigkeit haben
    @Test
    void testGetTransactionsByFrequency() {
        User user = dbAccessUser.createUser("testUserForFrequency", "1234", "test@frequency.com");
        Category category = dbAccessCategory.createCategory("testCategoryForFrequency", "Test Category for Frequency", "#00FF00", 222.2);

        Transaction transaction = dbAccess.createTransaction(user.getUserId(), category.getCategoryId(), 277.0, "2026-09-04",
                "spending", "Frequency Description", "monthly");
        Transaction transaction2 = dbAccess.createTransaction(user.getUserId(), category.getCategoryId(), 11.5, "2026-09-05",
                "income", "Frequency Description2", "monthly");

        assertNotNull(transaction);
        assertNotNull(transaction2);

        List<Transaction> transactions = dbAccess.getTransactionsByFrequency("monthly");
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(transaction));
        assertTrue(transactions.contains(transaction2));

        List<Transaction> transactions2 = dbAccess.getTransactionsByFrequency("weekly");
        assertNotNull(transactions2);
        assertTrue(transactions2.isEmpty());
    }
}
