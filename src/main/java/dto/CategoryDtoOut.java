package dto;

import model.Category;

public record CategoryDtoOut(String categoryName, String categoryDescription, String categoryColor, Double categoryLimit) {
    public CategoryDtoOut(Category category) {
        this(category.getCategoryName(), category.getCategoryDescription(), category.getCategoryColor(), category.getCategoryLimit());
    }
}
