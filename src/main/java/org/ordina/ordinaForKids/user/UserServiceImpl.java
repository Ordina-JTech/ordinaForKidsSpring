package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.ordina.ordinaForKids.validation.ExceptionDigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void createUser(User user) {
		// TODO Auto-generated method stub
		
		
		
		if(!userRepository.findOneByEmail(user.getEmail()).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User with email '" + user.getEmail() + "' already exists");
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		try {
			userRepository.save(user);
		}
		catch(Exception exception) {
			// try to get the SQLIntegrityContraintViolationException which is nested somewhere in there:
			ExceptionDigger exceptionDigger = new ExceptionDigger();
			Exception eSQLIntegrityContraintViolationException = exceptionDigger.digUntilException(exception, SQLIntegrityConstraintViolationException.class);
			if(eSQLIntegrityContraintViolationException != null) {
				throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, eSQLIntegrityContraintViolationException.getMessage());
			}
			else {
				throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
			}
		}
		
	}

	@Override
	public void updateUser(String email, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the ROLE_ of the user 
	 */
	@Override
	public Optional<User> getUser(String email) {
		// TODO Auto-generated method stub
		Optional<User> user = userRepository.findOneByEmail(email);
		if(user.isPresent()) { 
			user.get().setPassword(null);
		}
		else {
			if(email.equals("admin")) {
				User _user = new User();
				_user.setEmail("admin");
				_user.setUserrole(UserRole.Administrator);
				return Optional.of(_user);
			}
		}
		return user;
	}

}
