package dbaccess;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import model.User;
import util.PasswordTools;

@Repository
@Transactional
public class DBAccessUser {

	@PersistenceContext
	private final EntityManager entityManager;

	public DBAccessUser(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

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

}