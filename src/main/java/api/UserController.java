package api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessUser;
import dto.UserAuthDtoOut;
import dto.UserDtoOut;
import dto.UserLoginDtoIn;
import dto.UserRegisterDtoIn;
import model.User;
import security.SecurityManager;
import util.ControllerTools;

@RestController
@RequestMapping("/users")
public class UserController {
    private final DBAccessUser dbAccessUser;
    private final SecurityManager securityManager;
    private final ControllerTools controllerTools;

    public UserController(DBAccessUser dbAccessUser, SecurityManager securityManager, ControllerTools controllerTools) {
        this.dbAccessUser = dbAccessUser;
        this.securityManager = securityManager;
        this.controllerTools = controllerTools;
    }

    // Registriert einen neuen Benutzer und gibt einen Zugriffstoken zurück.
    @PostMapping("/register")
        public ResponseEntity<UserAuthDtoOut> registerUser(@RequestBody UserRegisterDtoIn userRegisterDtoIn) {
        User user = dbAccessUser.createUser(userRegisterDtoIn.username(), userRegisterDtoIn.password(),
                userRegisterDtoIn.email());
        String token = securityManager.createBenutzerToken(user);

        return ResponseEntity.ok()
            .body(new UserAuthDtoOut(token, new UserDtoOut(user)));
    }

    // Meldet einen Benutzer an und gibt Zugriffstoken und Benutzerdaten zurück.
    @PostMapping("/login")
    public ResponseEntity<UserAuthDtoOut> loginUser(@RequestBody UserLoginDtoIn userLoginDtoIn) {
        User user = dbAccessUser.getUserByUsernameAndPassword(userLoginDtoIn.username(), userLoginDtoIn.password());

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        String token = securityManager.createBenutzerToken(user);
        return ResponseEntity.ok()
            .body(new UserAuthDtoOut(token, new UserDtoOut(user)));
    }

    // Meldet den aktuellen Benutzer ab und entfernt das Token.
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logoutUser(@RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        boolean result = securityManager.removeToken(token);

        return ResponseEntity.ok()
                .body(result);
    }

    // Loescht einen Benutzer nach erfolgreicher Authentifizierung und Autorisierung.
    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);

        boolean result = dbAccessUser.deleteUser(userId);
        securityManager.removeToken(token);

        return ResponseEntity.ok()
                .body(result);
    }
}
