package dbaccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.Category;
import model.Transaction;
import model.User;

@Repository
public class DBAccessTransaction {
    @PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	public DBAccessTransaction(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

	// Methode zum Erstellen einer neuen Transaktion
	public Transaction createTransaction(int userId, int categoryId, Double transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {
		User user = entityManager.find(User.class, userId);
		Category category = entityManager.find(Category.class, categoryId);

		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setCategory(category);
		transaction.setTransactionAmount(transactionAmount);
		transaction.setTransactionDate(transactionDate);
		transaction.setTransactionType(transactionType);
		transaction.setTransactionDescription(transactionDescription);
		transaction.setTransactionFrequency(transactionFrequency);

		entityManager.persist(transaction);
		entityManager.flush();

		return transaction;
	}

	// Methode um alle Transaktionen aus der Datenbank zu holen
	public List<Transaction> getAllTransactions() {
        TypedQuery<Transaction> query = entityManager.createNamedQuery("getAllTransactions", Transaction.class);
        List<Transaction> result = query.getResultList();

        return result;
    }

	// Methode zum Filtern der Transaktionen
	public List<Transaction> getFilteredTransactions(
		Integer transactionId,
		Integer userId,
		Integer categoryId,
		Double amountMin,
		Double amountMax,
		String transactionDateFrom,
		String transactionDateTo,
		String transactionType,
		String keyword,
		String transactionFrequency) {

		StringBuilder query = new StringBuilder("SELECT t FROM Transaction t WHERE 1=1");

		if (transactionId != null) query.append(" AND t.transactionId = :transactionId");
		if (userId != null) query.append(" AND t.user.userId = :userId");
		if (categoryId != null) query.append(" AND t.category.categoryId = :categoryId");
		if (amountMin != null) query.append(" AND t.transactionAmount >= :amountMin");
		if (amountMax != null) query.append(" AND t.transactionAmount <= :amountMax");
		if (transactionDateFrom != null) query.append(" AND t.transactionDate >= :transactionDateFrom");
		if (transactionDateTo != null) query.append(" AND t.transactionDate <= :transactionDateTo");
		if (transactionType != null) query.append(" AND t.transactionType = :transactionType");
		if (keyword != null) query.append(" AND (t.transactionDescription LIKE :keyword OR t.transactionType LIKE :keyword)");
		if (transactionFrequency != null) query.append(" AND t.transactionFrequency = :transactionFrequency");

		TypedQuery<Transaction> q = entityManager.createQuery(query.toString(), Transaction.class);

		if (transactionId != null) q.setParameter("transactionId", transactionId);
		if (userId != null) q.setParameter("userId", userId);
		if (categoryId != null) q.setParameter("categoryId", categoryId);
		if (amountMin != null) q.setParameter("amountMin", amountMin);
		if (amountMax != null) q.setParameter("amountMax", amountMax);
		if (transactionDateFrom != null) q.setParameter("transactionDateFrom", transactionDateFrom);
		if (transactionDateTo != null) q.setParameter("transactionDateTo", transactionDateTo);
		if (transactionType != null) q.setParameter("transactionType", transactionType);
		if (keyword != null) q.setParameter("keyword", "%" + keyword + "%");
		if (transactionFrequency != null) q.setParameter("transactionFrequency", transactionFrequency);

		return q.getResultList();
	}

	// Methode zum Ändern einer Transaktion
	public boolean updateTransaction(int transactionId, Integer userId, Integer categoryId, Double transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {
		Transaction transaction = entityManager.find(Transaction.class, transactionId);
		if (transaction == null) {
			return false;
		}

		if (userId != null) {
			User user = entityManager.find(User.class, userId);
			transaction.setUser(user);
		}
		if (categoryId != null) {
			Category category = entityManager.find(Category.class, categoryId);
			transaction.setCategory(category);
		}
		if (transactionAmount != null) {
			transaction.setTransactionAmount(transactionAmount);
		}
		if (transactionDate != null) {
			transaction.setTransactionDate(transactionDate);
		}
		if (transactionType != null) {
			transaction.setTransactionType(transactionType);
		}
		if (transactionDescription != null) {
			transaction.setTransactionDescription(transactionDescription);
		}
		if (transactionFrequency != null) {
			transaction.setTransactionFrequency(transactionFrequency);
		}

		entityManager.flush();

		return true;
	}

	// Methode um eine Transaktion zu finden, anhand ihrer Id
	public Transaction getTransactionById(int transactionId) {
		return entityManager.find(Transaction.class, transactionId);
	}

	// Methode zum Löschen einer Transaktion
	public boolean deleteTransaction(int transactionId) {
        Transaction transaction = entityManager.find(Transaction.class, transactionId);
        if (transaction == null) {
            return false;
        }
        entityManager.remove(transaction);
        entityManager.flush();

        return true;
    }

	// Methode zum Summieren aller Transaktionen, die eine Ausgabe sind
	public Double sumTransactionsSpendings() {
		TypedQuery<Double> query = entityManager.createNamedQuery("sumTransactionsSpendings", Double.class);
		List<Double> result = query.getResultList();

		return result.get(0);
	}

	// Methode zum Summieren aller Transaktionen, die eine Einnahme sind
	public Double sumTransactionsIncome() {
		TypedQuery<Double> query = entityManager.createNamedQuery("sumTransactionsIncome", Double.class);
		return query.getSingleResult();
	}

	// Methode zum Summieren aller Ausgaben einer Kategorie
	public Double sumCategorySpending(int categoryId) {
		TypedQuery<Double> query = entityManager.createNamedQuery("sumCategorySpending", Double.class);
		query.setParameter("categoryId", categoryId);
		return query.getSingleResult();
	}

	// Methode zum Summieren aller Einnahmen einer Kategorie
	public Double sumCategoryIncome(int categoryId) {
		TypedQuery<Double> query = entityManager.createNamedQuery("sumCategoryIncome", Double.class);
		query.setParameter("categoryId", categoryId);
		return query.getSingleResult();
	}
}
