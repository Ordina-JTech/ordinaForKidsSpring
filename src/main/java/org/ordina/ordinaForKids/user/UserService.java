package org.ordina.ordinaForKids.user;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Optional;

import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.ordina.ordinaForKids.validation.UserNotFoundException;

public interface UserService {

   public abstract void createUser(User user) throws UserAlreadyExistsException, SQLIntegrityConstraintViolationException;
   public abstract Optional<User> updateUser(String email, User user) throws UserNotFoundException;
   public abstract void deleteUser(String email) throws UserNotFoundException;
   public abstract Optional<User> getUser(String email) throws UserNotFoundException;
   public abstract Collection<User> getUsers();
	
}
