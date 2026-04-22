package haushaltsbuch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
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
import model.TransactionFrequency;
import model.TransactionType;
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

    private int u1, u2, u3, u5, u9;
    private int c1, c3, c4;

    // Für jede Testmethode eine neue Datenbank erstellen
    @BeforeEach
    public void setUp() {
        u1 = dbAccessUser.createUser("User1", "pw", "u1@test.com").getUserId(); 
        u2 = dbAccessUser.createUser("User2", "pw", "u2@test.com").getUserId(); 
        u3 = dbAccessUser.createUser("User3", "pw", "u3@test.com").getUserId(); 
        dbAccessUser.createUser("User4", "pw", "u4@test.com"); 
        u5 = dbAccessUser.createUser("User5", "pw", "u5@test.com").getUserId(); 
        u9 = dbAccessUser.createUser("User9", "pw", "u9@test.com").getUserId(); 

        c1 = dbAccessCategory.createCategory("Cat1", "Desc", "color", 100.0).getCategoryId();
        c3 = dbAccessCategory.createCategory("Cat3", "Desc", "color", 100.0).getCategoryId();
        c4 = dbAccessCategory.createCategory("Cat4", "Desc", "color", 100.0).getCategoryId();
    }

    // Testet das Abrufen aller Transaktionen aus der Datenbank
    @Test
    void testGetAllTransactions() {
        Transaction transaction1 = dbAccess.createTransaction(u1, c1, 100.0, "2026-05-05", 
                                                              TransactionType.spending, "Test Transaction", TransactionFrequency.monthly);
        Transaction transaction2 = dbAccess.createTransaction(u2, c1, 300.0, "2026-05-07", 
                                                              TransactionType.spending, "Test Transaction2", TransactionFrequency.monthly);
        List<Transaction> transactions = dbAccess.getAllTransactions();
        assertNotNull(transaction1);
        assertNotNull(transaction2);
        assertEquals(2, transactions.size());
    }

    // Testet das Abrufen einer vorhandenen Transaktion anhand ihrer Id
    @Test
    void testGetTransactionById() {
        Transaction transaction = dbAccess.createTransaction(u1, c1, 100.0, "2026-05-05", 
                                                             TransactionType.spending, "Test Transaction", TransactionFrequency.monthly);
        Transaction transaction2 = dbAccess.getTransactionById(transaction.getTransactionId());
        assertNotNull(transaction2);
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
            "2026-10-15", TransactionType.spending, "Monthly grocery shopping", TransactionFrequency.monthly);
        
        dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 80.0,
            "2026-10-15", TransactionType.income, "Monthly grocery refund", TransactionFrequency.monthly);
        
        List<Transaction> filteredTransactions = dbAccess.getFilteredTransactions(
            matchingTransaction.getTransactionId(),
            targetUser.getUserId(),
            targetCategory.getCategoryId(),
            120.0,
            130.0,
            "2026-10-01",
            "2026-10-31",
            TransactionType.spending,
            "grocery",
            TransactionFrequency.monthly);

        assertNotNull(otherUser);
        assertNotNull(otherCategory);
        assertNotNull(filteredTransactions);
        assertEquals(1, filteredTransactions.size());
        assertEquals(matchingTransaction.getTransactionId(), filteredTransactions.get(0).getTransactionId());
    }

    // Testet das Filtern von Transaktionen mit mehreren Treffern
    @Test
    void testGetFilteredTransactionsMultipleResults() {
        User targetUser = dbAccessUser.createUser("testUserFilterMulti", "1234", "test@filter-multi.com");
        Category targetCategory = dbAccessCategory.createCategory("testCategoryFilterMulti", "Filter Multi Category", "#ABCDEF", 700.0);

        Transaction matchingTransaction1 = dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 90.0,
            "2026-12-01", TransactionType.spending, "Bus ticket work", TransactionFrequency.monthly);
        Transaction matchingTransaction2 = dbAccess.createTransaction(targetUser.getUserId(), targetCategory.getCategoryId(), 110.0,
            "2026-12-12", TransactionType.spending, "Fuel for work", TransactionFrequency.monthly);

        List<Transaction> filteredTransactions = dbAccess.getFilteredTransactions(
            null,
            targetUser.getUserId(),
            targetCategory.getCategoryId(),
            80.0,
            120.0,
            "2026-12-01",
            "2026-12-31",
            TransactionType.spending,
            "work",
            TransactionFrequency.monthly);

        assertNotNull(matchingTransaction1);
        assertNotNull(matchingTransaction2);
        assertNotNull(filteredTransactions);
        assertEquals(2, filteredTransactions.size());
    }

    // Testet das Löschen einer vorhandenen Transaktion
    @Test
    void testDeleteTransaction() {
        Transaction transaction = dbAccess.createTransaction(u3, c3, 499.0, "2026-06-08", 
                                                             TransactionType.spending, "Test Delete Transaction", TransactionFrequency.monthly);
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
        dbAccess.createTransaction(u5, c4, 140.0, "2026-08-08", 
                                   TransactionType.spending, "Spending 1", TransactionFrequency.monthly);
        dbAccess.createTransaction(u9, c4, 30.0, "2026-08-08", 
                                   TransactionType.spending, "Spending 2", TransactionFrequency.monthly);
        
        Double sum = dbAccess.sumTransactionsSpendings();
        assertEquals(170.0, sum);
    }

    // Testet das Summieren aller Transaktionen, die eine Einnahme sind
    @Test
    void testSumTransactionsIncome() {
        dbAccess.createTransaction(u5, c4, 250.0, "2026-08-08",
                TransactionType.income, "Income 1", TransactionFrequency.monthly);
        dbAccess.createTransaction(u9, c4, 50.0, "2026-08-08",
                TransactionType.income, "Income 2", TransactionFrequency.monthly);
        
        Double sum = dbAccess.sumTransactionsIncome();
        assertEquals(300.0, sum);
    }

    // Testet das Summieren aller Ausgaben einer Kategorie
    @Test
    void testSumCategorySpending() {
        User user = dbAccessUser.createUser("testUserSumCategory", "pw123", "testsum@test.com");
        Category targetCategory = dbAccessCategory.createCategory("testCatSum", "Target", "blue", 500.0);

        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 80.0, "2026-08-10",
                TransactionType.spending, "Target spending", TransactionFrequency.monthly);

        Double sum = dbAccess.sumCategorySpendings(targetCategory.getCategoryId());
        assertEquals(80.0, sum);
    }

    // Testet das Summieren der Ausgaben einer Kategorie ohne passende Transaktionen
    @Test
    void testSumCategorySpendingNoTransactions() {
        Category category = dbAccessCategory.createCategory("EmptyCat", "Empty", "gray", 200.0);
        Double sum = dbAccess.sumCategorySpendings(category.getCategoryId());
        assertEquals(0.0, sum);
    }

    // Testet das Summieren aller Einnahmen einer Kategorie
    @Test
    void testSumCategoryIncome() {
        User user = dbAccessUser.createUser("testUserInc", "pw123", "testinc@test.com");
        Category targetCategory = dbAccessCategory.createCategory("testCatInc", "Target", "green", 500.0);

        dbAccess.createTransaction(user.getUserId(), targetCategory.getCategoryId(), 100.0, "2026-08-13",
            TransactionType.income, "Target income", TransactionFrequency.monthly);

        Double sum = dbAccess.sumCategoryIncome(targetCategory.getCategoryId());
        assertEquals(100.0, sum);
    }

    // Testet das Erstellen einer neuen Transaktion
    @Test
    void testCreateTransaction() {
        User user = dbAccessUser.createUser("testUserNew", "1234", "new@test.com");
        Category category = dbAccessCategory.createCategory("testCatNew", "Test", "#FF0000", 500.0);
        
        Transaction transaction = dbAccess.createTransaction(user.getUserId(), category.getCategoryId(), 150.0, "2026-07-10", 
                                                             TransactionType.spending, "Test Create", TransactionFrequency.monthly);
        assertNotNull(transaction);
        assertEquals(user.getUserId(), transaction.getUser().getUserId());
    }

    // Testet das erfolgreiche Aktualisieren einer vorhandenen Transaktion
    @Test
    void testUpdateTransaction() {
        Transaction transaction = dbAccess.createTransaction(u1, c1, 200.0, "2026-09-01",
                TransactionType.spending, "Original", TransactionFrequency.monthly);

        boolean updated = dbAccess.updateTransaction(transaction.getTransactionId(), null, null, 350.0, "2026-09-15",
                TransactionType.income, "Updated", TransactionFrequency.weekly);

        assertTrue(updated);
        Transaction updatedTransaction = dbAccess.getTransactionById(transaction.getTransactionId());
        assertEquals(350.0, updatedTransaction.getTransactionAmount());
    }

    // Testet das Abrufen aller Transaktionen, die eine bestimmte Häufigkeit haben
    @Test
    void testGetTransactionsByFrequency() {
        dbAccess.createTransaction(u1, c1, 277.0, "2026-09-04",
                TransactionType.spending, "Freq1", TransactionFrequency.monthly);
        
        List<Transaction> transactions = dbAccess.getTransactionsByFrequency(TransactionFrequency.monthly);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
    }
}