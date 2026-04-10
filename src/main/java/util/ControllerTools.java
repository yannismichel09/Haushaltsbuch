package util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import security.SecurityManager;

@Component
public class ControllerTools {
    private final SecurityManager securityManager;

    public ControllerTools(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public void checkIsAccepted(String token) {

        if (!securityManager.isValid(token)) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        }

    }

    public void checkIsAuthorized(String token, int userId){

        if( userId != securityManager.getUser(token).getUserId() ){

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        }

    }
}
