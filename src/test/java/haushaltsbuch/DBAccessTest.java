package haushaltsbuch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import dbaccess.DBAccess;
import haushaltsbuch.Studienprojekt.StudienprojektApplication;
import model.User;

@Transactional
@SpringBootTest(classes = StudienprojektApplication.class)
class DBAccessTest {

    @Autowired
    private DBAccess dbAccess;

    // User Tests

    @Test
    void testCreateUser() {
        User user = dbAccess.createUser("testUser1", "1234", "test@User1.com");
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals("testUser1", user.getUserName());
        assertEquals("test@User1.com", user.getUserEmail());
    }

    @Test
    void testGetUserById() {
        User user = dbAccess.createUser("testUser2", "1234", "test@User2.com");
        User user2 = dbAccess.getUserById(user.getUserId());
        assertNotNull(user2);
        assertEquals("testUser2", user2.getUserName());
        assertEquals("test@User2.com", user2.getUserEmail());
    }

    @Test
    void testGetUserByUsernameAndPassword() {
        User user1 = dbAccess.createUser("testUser3", "1234", "test@User3.com");
        User user2 = dbAccess.getUserByUsernameAndPassword("testUser3", "1234");
        assertNotNull(user2);
        assertEquals(user1,user2);
        assertEquals(user1.getUserName(), user2.getUserName());
        assertEquals(user1.getUserPasswordHash(), user2.getUserPasswordHash());
        assertEquals(user1.getUserPasswordSalt(), user2.getUserPasswordSalt());
    }
}
