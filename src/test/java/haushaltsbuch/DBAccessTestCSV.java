package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import dbaccess.DBAccessCSV;
import dbaccess.DBAccessTransaction;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
class DBAccessTestCSV {

    @Autowired
    private DBAccessCSV dbAccessCSV;

    @Autowired
    private DBAccessTransaction dbAccessTransaction;

    // Testet das Exportieren gefilterter Transaktionen in CSV
    @Test
    void testExportFilteredTransactionsToCsv() {
        dbAccessTransaction.createTransaction(1, 1, 100, "2026-05-05", "spending", "Test Transaction", "monthly");

        String csv = dbAccessCSV.exportFilteredTransactionsToCsv(null, null, null, null, null, null, null, null, null, null);

        assertNotNull(csv);
        assertTrue(csv.contains("ID,UserID,CategoryID,Amount,Date,Type,Description,Frequency"));
        assertTrue(csv.contains("Test Transaction"));
        assertTrue(csv.contains("100"));
        
        assertFalse(csv.contains("101"));
        assertFalse(csv.contains("blub"));
    }
}