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

    // Aktuelle Benutzereinstellungen abrufen
    @GetMapping("/{userId}")
    public ResponseEntity<UserDtoOut> getUserSettings(@RequestHeader("Authorization") String token,
            @PathVariable int userId) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
        
        User user = dbAccessUser.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok().body(new UserDtoOut(user));
    }

    // E-Mail aktualisieren
    @PutMapping("/{userId}/email")
    public ResponseEntity<UserDtoOut> updateEmail(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateEmailDtoIn emailDtoIn) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
        
        boolean updated = dbAccessUser.updateEmail(userId, emailDtoIn.email());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        User user = dbAccessUser.getUserById(userId);
        return ResponseEntity.ok().body(new UserDtoOut(user));
    }

    // Passwort aktualisieren
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdatePasswordDtoIn passwordDtoIn) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
        
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
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
        
        boolean updated = dbAccessUser.updateUsername(userId, usernameDtoIn.username());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        User user = dbAccessUser.getUserById(userId);
        return ResponseEntity.ok().body(new UserDtoOut(user));
    }

    // Profilbild aktualisieren
    @PutMapping("/{userId}/profile-picture")
    public ResponseEntity<UserDtoOut> updateProfilePicture(@RequestHeader("Authorization") String token,
            @PathVariable int userId, @RequestBody UserUpdateProfilePictureDtoIn profilePictureDtoIn) {
        controllerTools.checkIsAccepted(token);
        controllerTools.checkIsAuthorized(token, userId);
        
        boolean updated = dbAccessUser.updateProfilePicture(userId, profilePictureDtoIn.profilePicture());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        User user = dbAccessUser.getUserById(userId);
        return ResponseEntity.ok().body(new UserDtoOut(user));
    }
}
