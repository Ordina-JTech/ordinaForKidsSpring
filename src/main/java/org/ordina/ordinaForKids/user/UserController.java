package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventDTO;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.ordina.ordinaForKids.validation.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
    private ModelMapper modelMapper;
	
	/**
	 * Simple authentication, using Basic authentication
	 */
	@GetMapping("/login")
	public ResponseEntity<Object> login(HttpServletRequest request, HttpServletResponse response)
	{
		Optional<User> user;
		try {
			user = userService.getUser(request.getUserPrincipal().getName());
			return new ResponseEntity<Object>(user.get(), HttpStatus.OK);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}
	
	@GetMapping("/user")
	public ResponseEntity<Object> getUser() {
		return new ResponseEntity<Object>(userService.getUsers(), HttpStatus.OK);
		
	}
	
	@PostMapping("/user") 
	public ResponseEntity<Object> createUser(@Valid @RequestBody UserDTO userDTO)
	{
		
		User user = modelMapper.map(userDTO, User.class);
		
		try {
			userService.createUser(user);
		} catch (SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "One or more fields contains incorrect data: " + sqlIntegrityConstraintViolationException.getMessage());
		} catch (UserAlreadyExistsException userAlreadyExistsException) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, userAlreadyExistsException.getMessage());
		}
		
		return new ResponseEntity<Object>(mapToUserDTO(user), HttpStatus.OK);
		
	}
	
	@PutMapping("/user")
	public ResponseEntity<Object> setUser(@Valid @RequestBody UserDTO userDTO)
	{
		User user = modelMapper.map(userDTO, User.class);
		
		Optional<User> updatedUser;
		try {
			updatedUser = userService.updateUser(user.getEmail(), user);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		
		// return the user as DTO
		return new ResponseEntity<Object>(mapToUserDTO(updatedUser.get()), HttpStatus.OK);
	}
	
	@DeleteMapping("/user/{email}")
	public ResponseEntity<Object> deleteUser(@PathVariable String email)
	{
		try {
			userService.deleteUser(email);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,  e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private UserDTO mapToUserDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setPassword(null);
		return userDTO;
	}
}
