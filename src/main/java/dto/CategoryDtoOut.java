package dto;

import model.Category;

public record CategoryDtoOut(Integer categoryId, String categoryName, String categoryDescription, String categoryColor, Double categoryLimit) {
    public CategoryDtoOut(Category category) {
        this(category.getCategoryId(), category.getCategoryName(), category.getCategoryDescription(), category.getCategoryColor(), category.getCategoryLimit());
    }
}
