package dbaccess;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Category;
import model.Transaction;
import model.TransactionFrequency;
import model.TransactionType;

@Repository
@Transactional
public class DBAccessCSV {
    @PersistenceContext
	private final EntityManager entityManager;

	public DBAccessCSV(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

	// Methode zum Exportieren aller Kategorien in eine CSV-Datei
    public String exportFilteredCategoriesToCsv(Integer categoryId, String keyword,
            List<String> categoryColors, Double amountMin, Double amountMax) {

        DBAccessCategory dbAccessCategory = new DBAccessCategory(entityManager);
        List<Category> categories = dbAccessCategory.getFilteredCategories(categoryId, keyword, categoryColors,
                amountMin, amountMax);

        StringBuilder sb = new StringBuilder();

        sb.append("ID,Name,Description,Color,Limit\n");

        for (Category c : categories) {
           sb.append(escapeCsv(c.getCategoryId())).append(",");
           sb.append(escapeCsv(c.getCategoryName())).append(",");
           sb.append(escapeCsv(c.getCategoryDescription())).append(",");
           sb.append(escapeCsv(c.getCategoryColor())).append(",");
           sb.append(escapeCsv(c.getCategoryLimit())).append("\n");
        }

        return sb.toString();
    }

	// Methode zum Exportieren gefilterter Transaktionen in eine CSV-Datei
    public String exportFilteredTransactionsToCsv(Integer transactionId, Integer userId, Integer categoryId, Double amountMin, Double amountMax, String transactionDateFrom, String transactionDateTo, TransactionType transactionType, String keyword, TransactionFrequency transactionFrequency) {

        DBAccessTransaction dbAccessTransaction = new DBAccessTransaction(entityManager);
        List<Transaction> transactions = dbAccessTransaction.getFilteredTransactions(transactionId, userId, categoryId, amountMin, amountMax, transactionDateFrom, transactionDateTo, transactionType, keyword, transactionFrequency);

        StringBuilder sb = new StringBuilder();

        sb.append("ID,UserID,CategoryID,Amount,Date,Type,Description,Frequency\n");

        for (Transaction t : transactions) {
           sb.append(escapeCsv(t.getTransactionId())).append(",");
           sb.append(escapeCsv(t.getUser() != null ? t.getUser().getUserId() : "")).append(",");
           sb.append(escapeCsv(t.getCategory() != null ? t.getCategory().getCategoryId() : "")).append(",");
           sb.append(escapeCsv(t.getTransactionAmount())).append(",");
           sb.append(escapeCsv(t.getTransactionDate())).append(",");
           sb.append(escapeCsv(t.getTransactionType())).append(",");
           sb.append(escapeCsv(t.getTransactionDescription())).append(",");
           sb.append(escapeCsv(t.getTransactionFrequency())).append("\n");
        }

        return sb.toString();
    }

    // Hilfsmethode zum Escapen von CSV-Werten
    private static String escapeCsv(Object value) {

        String strValue = value != null ? value.toString() : "";
    
        if (strValue.contains(",") || strValue.contains("\"") || strValue.contains("\n") || strValue.contains("\r")) {
            strValue = strValue.replace("\"", "\"\"");
            return "\"" + strValue + "\"";
        }
        return strValue;
    }
}
