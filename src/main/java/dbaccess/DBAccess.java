package dbaccess;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
}
