package dbaccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.Category;

public class DBAccessCSV {
    @PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	public DBAccessCSV(EntityManager entityManager) {

		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {

		return entityManager;

	}

	// Methode zum Exportieren aller Kategorien in eine CSV-Datei
	public String exportFilteredCategoriesToCsv(int categoryId, String keyword,String categoryColor, double amountMin, double amountMax) {

        DBAccessCategory dbAccessCategory = new DBAccessCategory(entityManager);
        List<Category> categories = dbAccessCategory.getFilteredCategories(categoryId, keyword, categoryColor, amountMin, amountMax);

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
