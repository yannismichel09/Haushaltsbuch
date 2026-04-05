package dbaccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.Category;

@Repository
public class DBAccessCategory {
    @PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	public DBAccessCategory(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

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
}
