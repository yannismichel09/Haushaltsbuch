package haushaltsbuch.Studienprojekt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import dbaccess.DBAccessUser;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import model.User;

@Transactional
@SpringBootTest
public class DBAccessTestUser {

    @Autowired
    private DBAccessUser dbAccess;

    // Testet das Erstellen eines neuen Benutzers
    @Test
    void testCreateUser() {
        User user = dbAccess.createUser("testUserCreate", "1234", "test@UserCreate.com");
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals("testUserCreate", user.getUserName());
        assertEquals("test@UserCreate.com", user.getUserEmail());
    }

    // Testet das Abrufen eines vorhandenenBenutzers anhand seiner ID
    @Test
    void testGetUserById() {
        User user = dbAccess.createUser("testUserById", "1234", "test@UserById.com");
        User user2 = dbAccess.getUserById(user.getUserId());
        assertNotNull(user2);
        assertEquals("testUserById", user2.getUserName());
        assertEquals("test@UserById.com", user2.getUserEmail());
    }

    // Testet das Abrufen eines nicht vorhandenen Benutzers anhand seiner ID
    @Test
    void testGetUserByIdNotFound() {
        User user = dbAccess.getUserById(10499);
        assertNull(user);
    }

    // Testet das Abrufen eines vorhandenen Benutzers anhand seines Benutzernamens und Passworts
    @Test
    void testGetUserByUsernameAndPassword() {
        User user1 = dbAccess.createUser("testUserByUsernameAndPassword", "1234", "test@UserByUsernameAndPassword.com");
        User user2 = dbAccess.getUserByUsernameAndPassword("testUserByUsernameAndPassword", "1234");
        assertNotNull(user2);
        assertEquals(user1,user2);
        assertEquals(user1.getUserName(), user2.getUserName());
        assertEquals(user1.getUserPasswordHash(), user2.getUserPasswordHash());
        assertEquals(user1.getUserPasswordSalt(), user2.getUserPasswordSalt());
    }

    // Testet das Abrufen eines vorhandenen Benutzers anhand eines falschen Benutzernamens und/oder falschen Passworts
    @Test
    void testGetUserByUsernameAndPasswordNotFound() {
        User user = dbAccess.getUserByUsernameAndPassword("testUserByUsernameAndPasswordNotFound", "1234");
        User user2 = dbAccess.getUserByUsernameAndPassword("testUserByUsernameAndPassword", "4567");
        assertNull(user);
        assertNull(user2);
    }

    // Testet das Löschen eines vorhandenen Benutzers
    @Test
    void testDeleteUser() {
        User user = dbAccess.createUser("testUserDelete", "1234", "test@delete.com");
        boolean result = dbAccess.deleteUser(user.getUserId());
        assertTrue(result);
        User deletedUser = dbAccess.getUserById(user.getUserId());
        assertNull(deletedUser);
    }

    // Testet das Löschen eines nicht vorhandenen Benutzers
    @Test
    void testDeleteUserNotFound() {
        boolean result = dbAccess.deleteUser(-1);
        assertFalse(result);
    }

    // Testet das Aktualisieren der E-Mail eines vorhandenen Benutzers
    @Test
    void testUpdateEmail() {
        User user = dbAccess.createUser("testUserUpdateEmail", "1234", "test@update.com");
        dbAccess.updateEmail(user.getUserId(), "test@updatenew.com");
        assertNotEquals(user.getUserEmail(), "test@update.com");
        assertEquals(user.getUserEmail(), "test@updatenew.com");
    }

    // Testet das Aktualisieren der E-Mail eines nicht vorhandenen Benutzers
    @Test
    void testUpdateEmailNotFound() {
        boolean result = dbAccess.updateEmail(-1, "test@updatenew.com");
        assertFalse(result);
    }
}
