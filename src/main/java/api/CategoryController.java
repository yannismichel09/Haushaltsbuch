package api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessCategory;
import dto.CategoryCreateDtoIn;
import dto.CategoryDtoOut;
import dto.CategoryFilterDtoIn;
import dto.CategoryUpdateDtoIn;
import model.Category;
import util.ControllerTools;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final DBAccessCategory dbAccessCategory;
    private final ControllerTools controllerTools;

    @Autowired
    public CategoryController(DBAccessCategory dbAccessCategory, ControllerTools controllerTools) {
        this.dbAccessCategory = dbAccessCategory;
        this.controllerTools = controllerTools;
    }

    // Post-Mapping

    @PostMapping
    public ResponseEntity<CategoryDtoOut> createCategory(@RequestHeader("Authorization") String token,
            @RequestBody CategoryCreateDtoIn categoryDtoIn) {
        controllerTools.checkIsAccepted(token);
        Category category = dbAccessCategory.createCategory(categoryDtoIn.categoryName(),
                categoryDtoIn.categoryDescription(), categoryDtoIn.categoryColor(), categoryDtoIn.categoryLimit());

        return ResponseEntity.ok()
                .body(new CategoryDtoOut(category.getCategoryName(), category.getCategoryDescription(),
                        category.getCategoryColor(), category.getCategoryLimit()));
    }

    // Get-Mapping

    @GetMapping
    public ResponseEntity<Collection<CategoryDtoOut>> getAllCategories(@RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        Collection<CategoryDtoOut> categories = dbAccessCategory.getAllCategories().stream().map(CategoryDtoOut::new)
                .toList();

        return ResponseEntity.ok()
                .body(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDtoOut> getCategoryById(@PathVariable int categoryId,
            @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        Category category = dbAccessCategory.getCategoryById(categoryId);

        return ResponseEntity.ok()
                .body(new CategoryDtoOut(category));
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<CategoryDtoOut>> getFilteredCategories(
            @RequestHeader("Authorization") String token, @RequestBody CategoryFilterDtoIn categoryFilterDtoIn) {
        controllerTools.checkIsAccepted(token);
        Collection<CategoryDtoOut> categories = dbAccessCategory.getFilteredCategories(categoryFilterDtoIn.categoryId(),
                categoryFilterDtoIn.keyword(), categoryFilterDtoIn.categoryColor(), categoryFilterDtoIn.amountMin(),
                categoryFilterDtoIn.amountMax()).stream().map(CategoryDtoOut::new).toList();

        return ResponseEntity.ok()
                .body(categories);
    }

    // Delete-Mapping

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable int categoryId,
            @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        boolean result = dbAccessCategory.deleteCategory(categoryId);

        return ResponseEntity.ok()
                .body(result);
    }

    // Put-Mapping

    @PutMapping("/{categoryId}")
    public ResponseEntity<Boolean> updateCategory(@PathVariable int categoryId,
            @RequestHeader("Authorization") String token, @RequestBody CategoryUpdateDtoIn categoryUpdateDtoIn) {
        controllerTools.checkIsAccepted(token);
        boolean result = dbAccessCategory.updateCategory(categoryId, categoryUpdateDtoIn.categoryName(),
                categoryUpdateDtoIn.categoryDescription(), categoryUpdateDtoIn.categoryColor(),
                categoryUpdateDtoIn.categoryLimit());

        return ResponseEntity.ok()
                .body(result);
    }

}
