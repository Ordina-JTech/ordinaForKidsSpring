package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	private ModelMapper modelMapper = new ModelMapper();
	
	/**
	 * Simple authentication, using Basic authentication to create a session token
	 */
	@GetMapping("/login")
	public UserSessionToken login(HttpServletRequest request, HttpServletResponse response)
	{
		
		UserSessionToken userSessionToken = new UserSessionToken();
		userSessionToken.setUsername(request.getUserPrincipal().getName());
		userSessionToken.setSessionToken("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		
		System.out.println("returing token");
		return userSessionToken;
		
	}
	
	@PostMapping("/user") 
	public String createUser(HttpServletRequest request, @Valid @RequestBody UserDTO userDTO)
	{
		
		User user = modelMapper.map(userDTO, User.class);
		user.setUserrole(UserRole.valueOf(userDTO.getUserrole()));
		
		Boolean userExists = userRepository.exists(
				Example.of(user, 
						ExampleMatcher.matchingAny().withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact())));
		if(userExists) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User with email '" + user.getEmail() + "' already exists");
		}
				
		try {
			userRepository.save(user);
		}
		catch(DataIntegrityViolationException exception) {
			
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.getCause().getCause().getMessage());
		}
		return "Saved successfully";
		
	}
	
}
