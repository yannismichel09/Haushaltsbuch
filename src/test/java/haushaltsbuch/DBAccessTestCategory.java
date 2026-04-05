package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
