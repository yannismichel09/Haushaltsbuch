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

import dbaccess.DBAccessTransaction;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import jakarta.transaction.Transactional;
import model.Transaction;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
public class DBAccessTestTransaction {

    @Autowired
    private DBAccessTransaction dbAccess;

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
}
