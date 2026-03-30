package model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Category-Entität, die der Datenbanktabelle "categories" entspricht
@Entity
@Table(name = "categories")
public class Category {

    // Primärschlüssel für die Category-Entität
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, unique = true)
    private Integer categoryId;

    // Kategoriename, Beschreibung, Farbe und Limit der Kategorie
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "category_description")
    private String categoryDescription;

    @Column(name = "category_color")
    private String categoryColor;

    @Column(name = "category_limit")
    private Double categoryLimit;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;

    // Konstruktoren
    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, String categoryDescription, String categoryColor, Double categoryLimit) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.categoryColor = categoryColor;
        this.categoryLimit = categoryLimit;
    }

    // Getters und Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public Double getCategoryLimit() {
        return categoryLimit;
    }

    public void setCategoryLimit(Double categoryLimit) {
        this.categoryLimit = categoryLimit;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
