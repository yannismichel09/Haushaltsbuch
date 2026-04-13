package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
import model.TransactionFrequency;
import model.TransactionType;
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
        dbAccessTransaction.createTransaction(user.getUserId(), category.getCategoryId(), 500.0, "2026-04-06", TransactionType.income, "testIncomeTransaction", TransactionFrequency.once);

        boolean result = dbAccess.checkNetBalanceNegative();
        assertFalse(result);
    }

    // Testet, dass checkNetBalanceNegative true zurückgibt, wenn nur Ausgaben vorhanden sind
    @Test
    void testCheckNetBalanceNegativeOnlySpending() {
        User user = dbAccessUser.createUser("testUserSpending", "testPassword123", "testspending@test.com");
        Category category = dbAccessCategory.createCategory("testSpendingCategory", "testSpendingDescription", "red", 1000.0);
        dbAccessTransaction.createTransaction(user.getUserId(), category.getCategoryId(), 500.0, "2026-04-06", TransactionType.spending, "testSpendingTransaction", TransactionFrequency.once);

        boolean result = dbAccess.checkNetBalanceNegative();
        assertTrue(result);
    }

    // Testet, dass checkNetBalanceNegative true zurückgibt, wenn Ausgaben größer als Einnahmen sind
    @Test
    void testCheckNetBalanceNegativeMoreSpendingThanIncome() {
        User user = dbAccessUser.createUser("testUserBalance1", "testPassword123", "testbalance1@test.com");
        Category category1 = dbAccessCategory.createCategory("testIncomeCategory2", "testIncomeDescription2", "green", 1000.0);
        Category category2 = dbAccessCategory.createCategory("testSpendingCategory2", "testSpendingDescription2", "red", 1000.0);

        dbAccessTransaction.createTransaction(user.getUserId(), category1.getCategoryId(), 100.0, "2026-04-06", TransactionType.income, "testIncomeTransaction2", TransactionFrequency.once);
        dbAccessTransaction.createTransaction(user.getUserId(), category2.getCategoryId(), 500.0, "2026-04-06", TransactionType.spending, "testSpendingTransaction2", TransactionFrequency.once);

        boolean result = dbAccess.checkNetBalanceNegative();
        assertTrue(result);
    }

    // Testet, dass checkNetBalanceNegative false zurückgibt, wenn Einnahmen größer als Ausgaben sind
    @Test
    void testCheckNetBalanceNegativeMoreIncomeThanSpending() {
        User user = dbAccessUser.createUser("testUserBalance2", "testPassword123", "testbalance2@test.com");
        Category category1 = dbAccessCategory.createCategory("testIncomeCategory3", "testIncomeDescription3", "green", 1000.0);
        Category category2 = dbAccessCategory.createCategory("testSpendingCategory3", "testSpendingDescription3", "red", 1000.0);

        dbAccessTransaction.createTransaction(user.getUserId(), category1.getCategoryId(), 500.0, "2026-04-06", TransactionType.income, "testIncomeTransaction3", TransactionFrequency.once);
        dbAccessTransaction.createTransaction(user.getUserId(), category2.getCategoryId(), 100.0, "2026-04-06", TransactionType.spending, "testSpendingTransaction3", TransactionFrequency.once);

        boolean result = dbAccess.checkNetBalanceNegative();
        assertFalse(result);
    }

    // Testet, dass checkBudgetLimit nur Kategorien zurückgibt, die den Schwellwert überschreiten
    @Test
    void testCheckBudgetLimit() {
        User user = dbAccessUser.createUser("testUserBudgetLimit", "testPassword123", "testbudgetlimit@test.com");
        Category overLimitCategory = dbAccessCategory.createCategory("testBudgetOver", "over", "red", 100.0);
        Category belowLimitCategory = dbAccessCategory.createCategory("testBudgetBelow", "below", "blue", 100.0);

        dbAccessTransaction.createTransaction(user.getUserId(), overLimitCategory.getCategoryId(), 85.0,
                "2026-04-07", TransactionType.spending, "over limit spending", TransactionFrequency.once);
        dbAccessTransaction.createTransaction(user.getUserId(), belowLimitCategory.getCategoryId(), 40.0,
                "2026-04-07", TransactionType.spending, "below limit spending", TransactionFrequency.once);

        List<Category> result = dbAccess.checkBudgetLimit(0.8);

        assertEquals(1, result.size());
        assertEquals(overLimitCategory.getCategoryId(), result.get(0).getCategoryId());
    }

    // Testet dass checkBudgetLimit bei genauem Erreichen des Schwellwerts keine Kategorie zurückgibt
    @Test
    void testCheckBudgetLimitAtThreshold() {
        User user = dbAccessUser.createUser("testUserBudgetThreshold", "testPassword123", "testbudgetthreshold@test.com");
        Category thresholdCategory = dbAccessCategory.createCategory("testBudgetThreshold", "threshold", "gray", 100.0);

        dbAccessTransaction.createTransaction(user.getUserId(), thresholdCategory.getCategoryId(), 80.0,
                "2026-04-07", TransactionType.spending, "exact threshold spending", TransactionFrequency.once);

        List<Category> result = dbAccess.checkBudgetLimit(0.8);

        assertTrue(result.isEmpty());
    }
}
