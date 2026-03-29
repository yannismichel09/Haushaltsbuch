package security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import model.User;

// Zur späterern Absicherung der Controller-Methoden

@Service
public class SecurityManager {

	private final Map<String, User> accessList= new ConcurrentHashMap<>();
	
	public String createBenutzerToken(User user) {
		
		String accessToken=UUID.randomUUID().toString();
		accessList.put(accessToken, user);
		
		return accessToken;
		
	}
	
	public boolean removeToken(String accessToken) {
		
		return accessList.remove(accessToken)!=null;
		
	}
	
	public boolean isValid(String accessToken) {
		
		return accessList.containsKey(accessToken);
		
	}
	
	public User getUser(String accessToken) {
		
		return accessList.get(accessToken);
		
	}
	
}
