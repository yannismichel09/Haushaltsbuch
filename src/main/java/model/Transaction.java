package model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

// Transaction-Entität, die der Datenbanktabelle "transactions" entspricht
@Entity
@Table(name = "transactions")
@NamedQueries({
    @NamedQuery(name="getAllTransactions", query="SELECT transaction FROM Transaction transaction"),
    @NamedQuery(name="checkNetBalance", query="SELECT SUM(CASE WHEN t.transactionType = 'spending' THEN t.transactionAmount ELSE 0 END) - SUM(CASE WHEN t.transactionType = 'income' THEN t.transactionAmount ELSE 0 END) FROM Transaction t"),
    @NamedQuery(name="sumTransactionsSpendings", query="SELECT SUM(CASE WHEN t.transactionType = 'spending' THEN t.transactionAmount ELSE 0 END) FROM Transaction t")
})
public class Transaction {

    // Primärschlüssel für die Transaction-Entität
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false, unique = true)
    private Integer transactionId;

    // Beziehung zu User: Eine Transaktion gehört zu einem Benutzer
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    // Beziehung zu Category: Eine Transaktion gehört zu einer Kategorie
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    // Betrag, Datum, Typ, Beschreibung und Frequenz der Transaktion
    @Column(name = "transaction_amount", nullable = false)
    private Integer transactionAmount;

    @Column(name = "transaction_date", nullable = false)
    private String transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "transaction_description")
    private String transactionDescription;

    @Column(name = "transaction_frequency", nullable = false)
    private String transactionFrequency;

    // Konstruktoren
    public Transaction() {
    }

    public Transaction(User user, Category category, Integer transactionAmount, String transactionDate, String transactionType) {
        this.user = user;
        this.category = category;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
    }

    // Getters und Setters
    public Integer getTransactionId() {
        return transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Integer transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getTransactionFrequency() {
        return transactionFrequency;
    }

    public void setTransactionFrequency(String transactionFrequency) {
        this.transactionFrequency = transactionFrequency;
    }
}
