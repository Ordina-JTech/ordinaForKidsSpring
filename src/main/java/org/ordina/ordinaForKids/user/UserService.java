package org.ordina.ordinaForKids.user;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	
	public UserSessionToken login(String email, String password) {
		
		// check the validity of the login in the repository
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		Optional<User> foundUser = userRepository.findOne(
				Example.of(
						user, 
						ExampleMatcher.matchingAll()
							.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact())
							.withMatcher("password", ExampleMatcher.GenericPropertyMatchers.exact())
						)
		);
		
		if(foundUser.isEmpty()) {
			return null;
		}
		else {
			UserSessionToken userSessionToken = new UserSessionToken();
			userSessionToken.setUsername(email);
			userSessionToken.setSessionToken(generateNewToken());
			
			return userSessionToken;
		}
	}
	
	
	private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

	public static String generateNewToken() {
	    byte[] randomBytes = new byte[24];
	    secureRandom.nextBytes(randomBytes);
	    return base64Encoder.encodeToString(randomBytes);
	}
}
