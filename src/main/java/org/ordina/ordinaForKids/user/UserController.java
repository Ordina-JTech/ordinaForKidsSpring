package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	
	/**
	 * Simple authentication, using Basic authentication to create a session token
	 */
	@GetMapping("/login")
	public ResponseEntity<Object> login(HttpServletRequest request, HttpServletResponse response)
	{
		Optional<User> user = userService.getUser(request.getUserPrincipal().getName());
		if(user.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot find user with email: " + request.getUserPrincipal().getName());
		}
		return new ResponseEntity<Object>(user.get(), HttpStatus.OK);
	}
	
	@PostMapping("/user") 
	public ResponseEntity<Object> createUser(HttpServletRequest request, @Valid @RequestBody User user)
	{
		userService.createUser(user);
		user.setPassword(null);
		return new ResponseEntity<Object>(user, HttpStatus.OK);
		
	}
	
}
