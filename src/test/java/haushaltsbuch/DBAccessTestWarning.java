package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dbaccess.DBAccessCategory;
import dbaccess.DBAccessTransaction;
import dbaccess.DBAccessUser;
import dbaccess.DBAccessWarning;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import jakarta.transaction.Transactional;
import model.Category;
import model.User;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
public class DBAccessTestWarning {

    @Autowired
    private DBAccessWarning dbAccess;

    @Autowired
    private DBAccessCategory dbAccessCategory;

    @Autowired
    private DBAccessTransaction dbAccessTransaction;

    @Autowired
    private DBAccessUser dbAccessUser;

    // Testet, dass checkNetBalanceNegative false zurückgibt, wenn keine Transaktionen vorhanden sind
    @Test
    void testCheckNetBalanceNegativeNoTransactions() {
        boolean result = dbAccess.checkNetBalanceNegative();
        assertFalse(result);
    }

    // Testet, dass checkNetBalanceNegative false zurückgibt, wenn nur Einnahmen vorhanden sind
    @Test
    void testCheckNetBalanceNegativeOnlyIncome() {
        User user = dbAccessUser.createUser("testUserIncome", "testPassword123", "testincome@test.com");
        Category category = dbAccessCategory.createCategory("testIncomeCategory", "testIncomeDescription", "green", 1000.0);
        dbAccessTransaction.createTransaction(user.getUserId(), category.getCategoryId(), 500.0, "2026-04-06", "income", "testIncomeTransaction", "once");

        boolean result = dbAccess.checkNetBalanceNegative();
        assertFalse(result);
    }

    // Testet, dass checkNetBalanceNegative true zurückgibt, wenn nur Ausgaben vorhanden sind
    @Test
    void testCheckNetBalanceNegativeOnlySpending() {
        User user = dbAccessUser.createUser("testUserSpending", "testPassword123", "testspending@test.com");
        Category category = dbAccessCategory.createCategory("testSpendingCategory", "testSpendingDescription", "red", 1000.0);
        dbAccessTransaction.createTransaction(user.getUserId(), category.getCategoryId(), 500.0, "2026-04-06", "spending", "testSpendingTransaction", "once");

        boolean result = dbAccess.checkNetBalanceNegative();
        assertTrue(result);
    }

    // Testet, dass checkNetBalanceNegative true zurückgibt, wenn Ausgaben größer als Einnahmen sind
    @Test
    void testCheckNetBalanceNegativeMoreSpendingThanIncome() {
        User user = dbAccessUser.createUser("testUserBalance1", "testPassword123", "testbalance1@test.com");
        Category category1 = dbAccessCategory.createCategory("testIncomeCategory2", "testIncomeDescription2", "green", 1000.0);
        Category category2 = dbAccessCategory.createCategory("testSpendingCategory2", "testSpendingDescription2", "red", 1000.0);

        dbAccessTransaction.createTransaction(user.getUserId(), category1.getCategoryId(), 100.0, "2026-04-06", "income", "testIncomeTransaction2", "once");
        dbAccessTransaction.createTransaction(user.getUserId(), category2.getCategoryId(), 500.0, "2026-04-06", "spending", "testSpendingTransaction2", "once");

        boolean result = dbAccess.checkNetBalanceNegative();
        assertTrue(result);
    }

    // Testet, dass checkNetBalanceNegative false zurückgibt, wenn Einnahmen größer als Ausgaben sind
    @Test
    void testCheckNetBalanceNegativeMoreIncomeThanSpending() {
        User user = dbAccessUser.createUser("testUserBalance2", "testPassword123", "testbalance2@test.com");
        Category category1 = dbAccessCategory.createCategory("testIncomeCategory3", "testIncomeDescription3", "green", 1000.0);
        Category category2 = dbAccessCategory.createCategory("testSpendingCategory3", "testSpendingDescription3", "red", 1000.0);

        dbAccessTransaction.createTransaction(user.getUserId(), category1.getCategoryId(), 500.0, "2026-04-06", "income", "testIncomeTransaction3", "once");
        dbAccessTransaction.createTransaction(user.getUserId(), category2.getCategoryId(), 100.0, "2026-04-06", "spending", "testSpendingTransaction3", "once");

        boolean result = dbAccess.checkNetBalanceNegative();
        assertFalse(result);
    }
}
