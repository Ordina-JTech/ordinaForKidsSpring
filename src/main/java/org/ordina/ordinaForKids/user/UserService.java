package org.ordina.ordinaForKids.user;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public interface UserService {

   public abstract void createUser(User user);
   public abstract void updateUser(String email, User userDTO);
   public abstract void deleteUser(String email);
   public abstract Optional<User> getUser(String email);
   public abstract Collection<User> getUsers();
	
}
