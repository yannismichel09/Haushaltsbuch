package dbaccess;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.Category;
import model.Transaction;
import model.User;
import util.PasswordTools;

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
}
