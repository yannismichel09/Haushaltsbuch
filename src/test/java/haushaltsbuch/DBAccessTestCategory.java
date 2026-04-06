package haushaltsbuch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dbaccess.DBAccessCategory;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import jakarta.transaction.Transactional;
import model.Category;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
public class DBAccessTestCategory {

    @Autowired
    private DBAccessCategory dbAccess;

    // Testet das Erstellen einer neuen Kategorie und validiert die gespeicherten Werte
    @Test
    void testCreateCategory() {
        String name = "testCreateCategory";
        String description = "testCreateCategoryDescription";
        String color = "testCreateCategoryColor";
        Double limit = 200.0;

        Category category = dbAccess.createCategory(name, description, color, limit);

        assertNotNull(category);
        assertNotNull(category.getCategoryId());
        assertEquals(name, category.getCategoryName());
        assertEquals(description, category.getCategoryDescription());
        assertEquals(color, category.getCategoryColor());
        assertEquals(limit, category.getCategoryLimit());

        Category persistedCategory = dbAccess.getCategoryById(category.getCategoryId());
        assertNotNull(persistedCategory);
        assertEquals(category, persistedCategory);
    }

    // Testet das Abrufen einer vorhandenen Kategorie anhand ihrer Id
    @Test
    void testGetCategoryById() {
        Category category = dbAccess.createCategory("testCategoryById", "testCategoryDescription", "testCategoryColor", 100.0);
        Category category2 = dbAccess.getCategoryById(category.getCategoryId());
        assertNotNull(category2);
        assertEquals(category, category2);
        assertEquals(category.getCategoryId(), category2.getCategoryId());
    }

    // Testet das Abrufen einer nicht vorhandenen Kategorie anhand ihrer Id
    @Test
    void testGetCategoryByIdNotFound() {
        Category category = dbAccess.getCategoryById(13431);
        assertNull(category);
    }

    // Testet das Updaten einer vorhandenen Kategorie
    @Test
    void testUpdateCategory() {
        Category category = dbAccess.createCategory("testUpdateCategory", "testUpdateCategoryDescription", "testUpdateCategoryColor", 333.0);
        boolean result = dbAccess.updateCategory(category.getCategoryId(), "testUpdateCategory2", "testUpdateCategoryDescription2", null, null);
        assertTrue(result);

        Category updated = dbAccess.getCategoryById(category.getCategoryId());
        assertEquals("testUpdateCategory2", updated.getCategoryName());
        assertEquals("testUpdateCategoryDescription2", updated.getCategoryDescription());
        assertEquals("testUpdateCategoryColor", updated.getCategoryColor()); 
        assertEquals(333.0, updated.getCategoryLimit());                 

        boolean result2 = dbAccess.updateCategory(category.getCategoryId(), null, null, "testUpdateCategoryColor2", 666.0);
        assertTrue(result2);

        Category updated2 = dbAccess.getCategoryById(category.getCategoryId());
        assertEquals("testUpdateCategory2", updated2.getCategoryName());       
        assertEquals("testUpdateCategoryDescription2", updated2.getCategoryDescription()); 
        assertEquals("testUpdateCategoryColor2", updated2.getCategoryColor());
        assertEquals(666.0, updated2.getCategoryLimit());
    }

    // Testet das Updaten einer nicht vorhandenen Kategorie
    @Test
    void testUpdateCategoryNotFound() {
        boolean result = dbAccess.updateCategory(17477, "testUpdateCategory3", "testUpdateCategoryDescription3", null, 11.6);
        assertFalse(result);
    }

    // Testet das Löschen einer vorhandenen Kategorie
    @Test
    void testDeleteCategory() {
        Category category = dbAccess.createCategory("testDeleteCategory", "testDeleteCategoryDescription", "testDeleteCategoryColor", 85.4);
        boolean deleted = dbAccess.deleteCategory(category.getCategoryId());
        assertTrue(deleted);
        assertNull(dbAccess.getCategoryById(category.getCategoryId()));
    }

    // Testet das Löschen einer nicht vorhandenen Kategorie
    @Test
    void testDeleteCategoryNotFound() {
        boolean deleted = dbAccess.deleteCategory(22222);
        assertFalse(deleted);
    }

    // Testet das Abrufen aller gefilterten Kategorien
    @Test
    void testGetFilteredCategories() {
        Category category = dbAccess.createCategory("testGetFilteredCategories", "testGetFilteredCategoriesDescription", "testGetFilteredCategoriesColor", 222.2);
        Category category2 = dbAccess.createCategory("testGetFilteredCategories2", "testGetFilteredCategoriesDescription2", "testGetFilteredCategoriesColor2", 23.9);
        Category category3 = dbAccess.createCategory("testGetFilteredCategories3", "testGetFilteredCategoriesDescription3", "testGetFilteredCategoriesColor3", 95.2);
        assertNotNull(category);
        assertNotNull(category2);
        assertNotNull(category3);
        List<Category> categories = dbAccess.getFilteredCategories(null, "testGetFilteredCategories", null, 20.0, 180.4);
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertTrue(categories.contains(category2));
        assertTrue(categories.contains(category3));
        assertFalse(categories.contains(category));
    }
}
