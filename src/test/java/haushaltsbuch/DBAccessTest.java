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

    @Test
    void testCreateUser() {
        User user = dbAccess.createUser("testUser1", "1234", "test@User1.com");
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals("testUser1", user.getUserName());
        assertEquals("test@User1.com", user.getUserEmail());
    }
}
