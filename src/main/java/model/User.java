package model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


// User-Entität, die der Datenbanktabelle "users" entspricht
@Entity
@Table(name = "users")
@NamedQueries({
@NamedQuery(name="getUserByUsername", query="SELECT user FROM User user WHERE user.userName = :userName"),
@NamedQuery(name="getAllUsers", query="SELECT user FROM User user")
})
public class User {


    // Primärschlüssel für die User-Entität
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    // Benutzername, E-Mail, Passwort-Hash, Passwort-Salt und Profilbild des Benutzers
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "user_password_hash", nullable = false)
    private byte[] userPasswordHash;

    @Column(name = "user_password_salt", nullable = false)
    private byte[] userPasswordSalt;

    @Column(name = "user_profile_picture")
    private byte[] userProfilePicture;

    // Beziehung zu Transaktionen: Ein Benutzer kann mehrere Transaktionen haben
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    // Konstruktoren
    public User() {
    }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    // Getters und Setters
    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public byte[] getUserPasswordHash() {
        return userPasswordHash;
    }

    public void setUserPasswordHash(byte[] userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }

    public byte[] getUserPasswordSalt() {
        return userPasswordSalt;
    }

    public void setUserPasswordSalt(byte[] userPasswordSalt) {
        this.userPasswordSalt = userPasswordSalt;
    }

    public byte[] getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(byte[] userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
