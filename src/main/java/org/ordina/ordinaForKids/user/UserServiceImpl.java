package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventRepository;
import org.ordina.ordinaForKids.validation.ExceptionDigger;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.ordina.ordinaForKids.validation.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private CalendarEventRepository calendarEventRepository;

	@Override
	@Transactional()
	public void createUser(User user)
			throws UserAlreadyExistsException, SQLIntegrityConstraintViolationException {
		// TODO Auto-generated method stub

		if (!userRepository.findOneByEmail(user.getEmail()).isEmpty()) {
			throw new UserAlreadyExistsException(user.getEmail());
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		try {
			userRepository.save(user);
		} catch (Exception exception) {
			// try to get the SQLIntegrityContraintViolationException which is nested
			// somewhere in there:
			ExceptionDigger exceptionDigger = new ExceptionDigger();
			SQLIntegrityConstraintViolationException eSQLIntegrityContraintViolationException = exceptionDigger
					.digUntilException(exception, SQLIntegrityConstraintViolationException.class);
			if (eSQLIntegrityContraintViolationException != null) {
				throw eSQLIntegrityContraintViolationException;
			}

			throw exception;
		}

	}

	@Override
	@Transactional
	public Optional<User> updateUser(String email, User user) throws UserNotFoundException {
		// TODO Auto-generated method stub

		Optional<User> existingUser = userRepository.findOneByEmail(email);
		if (existingUser.isEmpty()) {
			throw new UserNotFoundException(email);
		} else {
			if (user.getPassword() == null || user.getPassword().isEmpty()) {
				user.setPassword(existingUser.get().getPassword());
			} else {
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}

			userRepository.save(user);
			existingUser = getUser(user.getEmail());
			existingUser.get().setPassword(null);
			return existingUser;
		}
	}

	@Override
	@Transactional
	public void deleteUser(String email) throws UserNotFoundException {
		if(userRepository.findOneByEmail(email).isEmpty()) {
			throw new UsernameNotFoundException(email);
		}
		calendarEventRepository.findAllByOwner(email).stream()
				.forEach(calendarEvent -> calendarEventRepository.delete(calendarEvent));
		userRepository.deleteByEmail(email);
	}

	/**
	 * Returns the users, always hiding the password
	 */
	@Override
	public Collection<User> getUsers() {

		List<User> users = userRepository.findAll();

		for (User user : users) {
			user.setPassword(null);
		}

		return users;
	}

	/**
	 * Returns the user
	 * @throws UserNotFoundException 
	 */
	@Override
	public Optional<User> getUser(String email) throws UserNotFoundException {
		// TODO Auto-generated method stub
		Optional<User> user = userRepository.findOneByEmail(email);
		if (user.isPresent()) {
			user.get().setPassword(null);
		} else {
			if (email.equals("admin")) {
				User _user = new User();
				_user.setEmail("admin");
				_user.setUserrole(UserRole.Administrator);
				return Optional.of(_user);
			}
			else {
				throw new UserNotFoundException(email);
			}
		}
		return user;
	}

}
