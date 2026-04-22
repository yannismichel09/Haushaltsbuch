package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import dbaccess.DBAccessCSV;
import dbaccess.DBAccessCategory;
import dbaccess.DBAccessTransaction;
import dbaccess.DBAccessUser;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import model.Category;
import model.TransactionFrequency;
import model.TransactionType;
import model.User;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
class DBAccessTestCSV {

    @Autowired
    private DBAccessCSV dbAccessCSV;

    @Autowired
    private DBAccessTransaction dbAccessTransaction;

    @Autowired
    private DBAccessCategory dbAccessCategory;

    @Autowired
    private DBAccessUser dbAccessUser;

    // Testet das Exportieren aller gefilterten Transaktionen in CSV
    @Test
    void testExportFilteredTransactionsToCsv() {
        User user = dbAccessUser.createUser("CsvUser", "pw", "csv@test.com");
        Category category = dbAccessCategory.createCategory("CsvCat", "Desc", "blue", 500.0);

        dbAccessTransaction.createTransaction(user.getUserId(), category.getCategoryId(), 100.0, "2026-05-05", 
                                              TransactionType.spending, "Test Transaction", TransactionFrequency.monthly);

        String csv = dbAccessCSV.exportFilteredTransactionsToCsv(null, null, null, null, null, null, null, null, null, null);

        assertNotNull(csv);
        assertTrue(csv.contains("Test Transaction"));
        assertTrue(csv.contains("100"));
    }

    // Testet das Exportieren aller gefilterten Kategorien in CSV
    @Test
    void testExportFilteredCategoriesToCsv() {
        Category category = dbAccessCategory.createCategory("testExport", "desc", "red", 123.45);
        String csv = dbAccessCSV.exportFilteredCategoriesToCsv(category.getCategoryId(), null, null, 0.0, Double.MAX_VALUE);

        assertNotNull(csv);
        assertTrue(csv.contains("testExport"));
        assertTrue(csv.contains("123.45"));
    }
}