package org.ordina.ordinaForKids.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findOneByEmail(String email);
	
	public void deleteByEmail(String email);
	
}
