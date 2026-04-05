package haushaltsbuch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Transaction transaction1 = dbAccess.createTransaction(1, 1, 100, "2026-05-05", 
                                                              "spending", "Test Transaction", "monthly");
        Transaction transaction2 = dbAccess.createTransaction(2, 1, 300, "2026-05-07", 
                                                              "spending", "Test Transaction2", "monthly");
        List<Transaction> transactions = dbAccess.getAllTransactions();
        assertNotNull(transaction1);
        assertNotNull(transaction2);
        assertNotNull(transactions.size());
        assertEquals(2, transactions.size());
    }
}
