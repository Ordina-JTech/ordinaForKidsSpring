package org.ordina.ordinaForKids.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
		
		
		return null;
		
	}
	
}
