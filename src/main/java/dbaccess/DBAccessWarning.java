package dbaccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.Category;

public class DBAccessWarning {
	@PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	public DBAccessWarning(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

    }

	// Methode um Kategorien zu erhalten, bei denen das Budget über einen bestimmten Prozentsatz liegt
	public List<Category> checkBudgetLimit(double percent) {
		TypedQuery<Category> query = entityManager.createNamedQuery("checkBudgetLimit", Category.class);
		query.setParameter("percent", percent);
		List<Category> result = query.getResultList();
		return result;
	}

	// Methode zur Prüfung, ob die Ausgaben höher sind als die Einnahmen
	public boolean checkNetBalanceNegative() {
		TypedQuery<Long> query = entityManager.createNamedQuery("checkNetBalance", Long.class);
		Long result = query.getSingleResult();
		return result != null && result > 0;
	}
}
