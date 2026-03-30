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

	// Category-Methoden

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

	// Transaction-Methoden

	// Methode um alle Transaktionen aus der Datenbank zu holen
	public List<Transaction> getAllTransactions() {
        TypedQuery<Transaction> query = entityManager.createNamedQuery("getAllTransactions", Transaction.class);
        List<Transaction> result = query.getResultList();

        return result;
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
