package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import dbaccess.DBAccessCSV;
import dbaccess.DBAccessCategory;
import dbaccess.DBAccessTransaction;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import model.Category;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
class DBAccessTestCSV {

    @Autowired
    private DBAccessCSV dbAccessCSV;

    @Autowired
    private DBAccessTransaction dbAccessTransaction;

    @Autowired
    private DBAccessCategory dbAccessCategory;

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

    // Testet das Exportieren gefilterter Kategorien in CSV
    @Test
    void testExportFilteredCategoriesToCsv() {

        Category category = dbAccessCategory.createCategory("testExportCategory", "testExportDescription", "red", 100.0);

        String csv = dbAccessCSV.exportFilteredCategoriesToCsv(category.getCategoryId(), null, null, 0.0, Double.MAX_VALUE);

        assertNotNull(csv);
        assertTrue(csv.contains("ID,Name,Description,Color,Limit"));
        assertTrue(csv.contains("testExportCategory"));
        assertTrue(csv.contains("testExportDescription"));
        assertTrue(csv.contains("red"));
        assertTrue(csv.contains("100.0"));
  
        assertFalse(csv.contains("blub"));
        assertFalse(csv.contains("999.0"));
    }
}