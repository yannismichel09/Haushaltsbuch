package dbaccess;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.Category;
import model.Transaction;
import model.User;
import util.PasswordTools;

@Repository
public class DBAccess {

	@PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	public DBAccess(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

	// User-Methoden

	// Methode zum Erstellen eines neuen Benutzers
	public User createUser(String username, String password, String email) {
		User user = new User();

		user.setUserEmail(email);
		user.setUserName(username);

		byte[] salt = PasswordTools.generateSalt();
		byte[] password_Hash = PasswordTools.generatePasswordHash(password, salt);
		user.setUserPasswordSalt(salt);
		user.setUserPasswordHash(password_Hash);

		entityManager.persist(user);
		entityManager.flush();

		return user;
	}

	// Methode um einen Benutzer zu finden, anhand seiner Id
	public User getUserById(int userId) {
		return entityManager.find(User.class, userId);
	}

	// Methode um einen Benutzer zu finden, anhand seines Nutzernamens und seines Passworts
	public User getUserByUsernameAndPassword(String username, String password) {
		TypedQuery<User> query = entityManager.createNamedQuery("getUserByUsername", User.class);
		query.setParameter("userName", username);
		List<User> result = query.getResultList();

		if (result.isEmpty()==true) {
			return null;
		}

		User user = result.get(0);
		byte[] salt = user.getUserPasswordSalt();
		byte[] hash = PasswordTools.generatePasswordHash(password, salt);

		if (Arrays.equals(hash, user.getUserPasswordHash())) {
			return user;
		}

		return null;
	}

	// Methode zum Ändern der Email-Adresse eines Benutzers
	public boolean updateEmail(int userId, String email) {
		User user = entityManager.find(User.class, userId);
		if (user == null) {
			return false;
		}
		user.setUserEmail(email);
		entityManager.flush();

		return true;
	}

	// Methode zum Ändern des Passworts eines Benutzers
	public boolean updatePassword(int userId, String password) {
		User user = entityManager.find(User.class, userId);
		if (user == null) {
			return false;
		}

		byte[] salt = PasswordTools.generateSalt();
		byte[] password_Hash = PasswordTools.generatePasswordHash(password, salt);
		user.setUserPasswordSalt(salt);
		user.setUserPasswordHash(password_Hash);
		entityManager.flush();

		return true;
	}

	// Methode zum Ändern des Benutzernamens eines Benutzers
	public boolean updateUsername(int userId, String username) {
		User user = entityManager.find(User.class, userId);
		if (user == null) {
			return false;
		}
		user.setUserName(username);
		entityManager.flush();

		return true;
	}

	// Methode zum Ändern des Profilbildes eines Benutzers
	public boolean updateProfilePicture(int userId, byte[] profilePicture) {
		User user = entityManager.find(User.class, userId);
		if (user == null) {
			return false;
		}
		user.setUserProfilePicture(profilePicture);
		entityManager.flush();

		return true;
	}

	// Methode zum Löschen eines Benutzers
	public boolean deleteUser(int userId) {
		User user = entityManager.find(User.class, userId);
		if (user == null) {
			return false;
		}
		entityManager.remove(user);
		entityManager.flush();

		return true;
	}

	// Category-Methoden

	// Methode zum Erstellen einer neuen Kategorie
	public Category createCategory(String categoryName, String categoryDescription, String categoryColor, Double categoryLimit) {
		Category category = new Category();
		category.setCategoryName(categoryName);
		category.setCategoryDescription(categoryDescription);
		category.setCategoryColor(categoryColor);
		category.setCategoryLimit(categoryLimit);

		entityManager.persist(category);
		entityManager.flush();

		return category;
	}

	// Methode um alle Kategorien aus der Datenbank zu holen
	public List<Category> getAllCategories() {
        TypedQuery<Category> query = entityManager.createNamedQuery("getAllCategories", Category.class);
        List<Category> result = query.getResultList();

        return result;
    }

	// Methode um eine Kategorie zu finden, anhand ihrer Id
	public Category getCategoryById(int categoryId) {
		return entityManager.find(Category.class, categoryId);
	}

	// Methode zum Löschen einer Kategorie
	public boolean deleteCategory(int categoryId) {
		Category category = entityManager.find(Category.class, categoryId);
		if (category == null) {
			return false;
		}
		entityManager.remove(category);
		entityManager.flush();

		return true;
	}

	// Methode zum Ändern des Namens, Beschreibungstextes, Farbe und Limits einer Kategorie
	public boolean updateCategory(int categoryId, String categoryName, String categoryDescription, String categoryColor, Double categoryLimit) {
		Category category = entityManager.find(Category.class, categoryId);
		if (category == null) {
			return false;
		}

		if (categoryName != null) {
			category.setCategoryName(categoryName);
		}
		if (categoryDescription != null) {
			category.setCategoryDescription(categoryDescription);
		}
		if (categoryColor != null) {
			category.setCategoryColor(categoryColor);
		}
		if (categoryLimit != null) {
			category.setCategoryLimit(categoryLimit);
		}
		
		entityManager.flush();

		return true;
	}

	// Methode zum Filtern der Kategorien
    public List<Category> getFilteredCategories(
        Integer categoryId,
        String keyword,
		String categoryColor,
        Double amountMin,
        Double amountMax) {

        StringBuilder query = new StringBuilder("SELECT c FROM Category c");

        if (categoryId != null) query.append(" AND c.categoryId = :categoryId");
        if (keyword != null) query.append(" AND (c.categoryName LIKE :keyword OR c.categoryDescription LIKE :keyword)");
		if (categoryColor != null) query.append(" AND c.categoryColor = :categoryColor");
        if (amountMin != null) query.append(" AND c.categoryLimit >= :amountMin");
        if (amountMax != null) query.append(" AND c.categoryLimit <= :amountMax");

        TypedQuery<Category> q = entityManager.createQuery(query.toString(), Category.class);

        if (categoryId != null) q.setParameter("categoryId", categoryId);
        if (keyword != null) q.setParameter("keyword", "%" + keyword + "%");
		if (categoryColor != null) q.setParameter("categoryColor", categoryColor);
        if (amountMin != null) q.setParameter("amountMin", amountMin);
        if (amountMax != null) q.setParameter("amountMax", amountMax);

        return q.getResultList();
    }

	// Transaction-Methoden

	// Methode zum Erstellen einer neuen Transaktion
	public Transaction createTransaction(int userId, int categoryId, Integer transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {
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
		Integer amountMin,
		Integer amountMax,
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
	public boolean updateTransaction(int transactionId, Integer userId, Integer categoryId, Integer transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {
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

	// Warnings-Methoden

	// Methode um die Kategorien zurückzugeben, bei denen das BudgetLimit überschritten wurden
    public List<Category> checkBudgetExceeded() {
	    TypedQuery<Category> query = entityManager.createNamedQuery("checkBudgetExceeded", Category.class);

	    return query.getResultList();
	}

	// CSV-Methoden

	// Methode zum Exportieren aller Kategorien in eine CSV-Datei
	public String exportFilteredCategoriesToCsv(int categoryId, String keyword,String categoryColor, double amountMin, double amountMax) {

        List<Category> categories = getFilteredCategories(categoryId, keyword, categoryColor, amountMin, amountMax);

        StringBuilder sb = new StringBuilder();

        sb.append("ID,Name,Description,Color,Limit\n");

        for (Category c : categories) {
           sb.append(c.getCategoryId()).append(",");
           sb.append(c.getCategoryName()).append(",");
           sb.append(c.getCategoryDescription()).append(",");
           sb.append(c.getCategoryColor()).append(",");
           sb.append(c.getCategoryLimit()).append("\n");
        }

        return sb.toString();
    }
}