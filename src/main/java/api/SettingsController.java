package api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbaccess.DBAccessUser;
import dto.UserDtoOut;
import dto.UserUpdateEmailDtoIn;
import dto.UserUpdatePasswordDtoIn;
import dto.UserUpdateProfilePictureDtoIn;
import dto.UserUpdateSecurityDtoIn;
import dto.UserUpdateUsernameDtoIn;
import model.User;
import util.ControllerTools;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    private final DBAccessUser dbAccessUser;
    private final ControllerTools controllerTools;

    public SettingsController(DBAccessUser dbAccessUser, ControllerTools controllerTools) {
        this.dbAccessUser = dbAccessUser;
        this.controllerTools = controllerTools;
    }

    private void authorizeRequest(String token, int userId) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
    }

    private ResponseEntity<UserDtoOut> currentUserResponse(int userId) {
        User user = dbAccessUser.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(new UserDtoOut(user));
    }

    // Aktuelle Benutzereinstellungen abrufen
    @GetMapping("/{userId}")
    public ResponseEntity<UserDtoOut> getUserSettings(@RequestHeader("Authorization") String token,
            @PathVariable int userId) {
        authorizeRequest(token, userId);

        return currentUserResponse(userId);
    }

    // E-Mail aktualisieren
    @PutMapping("/{userId}/email")
    public ResponseEntity<UserDtoOut> updateEmail(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateEmailDtoIn emailDtoIn) {
        authorizeRequest(token, userId);

        if (emailDtoIn.email() == null || emailDtoIn.email().trim().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean updated = dbAccessUser.updateEmail(userId, emailDtoIn.email().trim());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return currentUserResponse(userId);
    }

    // Passwort aktualisieren
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdatePasswordDtoIn passwordDtoIn) {
        authorizeRequest(token, userId);
        
        boolean updated = dbAccessUser.updatePassword(userId, passwordDtoIn.password());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok().build();
    }

    // Benutzername aktualisieren
    @PutMapping("/{userId}/username")
    public ResponseEntity<UserDtoOut> updateUsername(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateUsernameDtoIn usernameDtoIn) {
        authorizeRequest(token, userId);
        
        boolean updated = dbAccessUser.updateUsername(userId, usernameDtoIn.username());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return currentUserResponse(userId);
    }

    // Profilbild aktualisieren
    @PutMapping("/{userId}/profile-picture")
    public ResponseEntity<UserDtoOut> updateProfilePicture(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateProfilePictureDtoIn profilePictureDtoIn) {
        authorizeRequest(token, userId);
        
        boolean updated = dbAccessUser.updateProfilePicture(userId, profilePictureDtoIn.profilePicture());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return currentUserResponse(userId);
    }

    // E-Mail und Passwort in einem gemeinsamen Save aktualisieren
    @PutMapping("/{userId}/security")
    public ResponseEntity<UserDtoOut> updateSecurity(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateSecurityDtoIn securityDtoIn) {
        authorizeRequest(token, userId);

        User currentUser = dbAccessUser.getUserById(userId);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        String requestedEmail = securityDtoIn.email() == null ? "" : securityDtoIn.email().trim();
        String oldPassword = securityDtoIn.oldPassword() == null ? "" : securityDtoIn.oldPassword();
        String newPassword = securityDtoIn.newPassword() == null ? "" : securityDtoIn.newPassword();
        String confirmPassword = securityDtoIn.confirmPassword() == null ? "" : securityDtoIn.confirmPassword();

        if (requestedEmail.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        boolean shouldUpdateEmail = !requestedEmail.isBlank() && !requestedEmail.equals(currentUser.getUserEmail());
        boolean passwordInputProvided = !oldPassword.isBlank() || !newPassword.isBlank() || !confirmPassword.isBlank();

        if (passwordInputProvided) {
            if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().build();
            }

            if (!dbAccessUser.isPasswordCorrect(userId, oldPassword)) {
                return ResponseEntity.badRequest().build();
            }

            dbAccessUser.updatePassword(userId, newPassword);
        }

        if (shouldUpdateEmail) {
            dbAccessUser.updateEmail(userId, requestedEmail);
        }

        return currentUserResponse(userId);
    }
}
