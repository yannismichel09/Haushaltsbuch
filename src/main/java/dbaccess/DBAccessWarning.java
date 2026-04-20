package dbaccess;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import model.Category;
import model.TransactionType;

@Repository
@Transactional
public class DBAccessWarning {
	@PersistenceContext
	private final EntityManager entityManager;

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
		query.setParameter("spendingType", TransactionType.spending);
		List<Category> result = query.getResultList();
		return result;
	}

	// Methode zur Prüfung, ob die Ausgaben höher sind als die Einnahmen
	public boolean checkNetBalanceNegative() {
		TypedQuery<Double> query = entityManager.createNamedQuery("checkNetBalance", Double.class);
		query.setParameter("spendingType", TransactionType.spending);
		query.setParameter("incomeType", TransactionType.income);
		Double result = query.getSingleResult();
		return result != null && result > 0;
	}
}
