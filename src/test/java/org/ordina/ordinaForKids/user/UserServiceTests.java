package org.ordina.ordinaForKids.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {

	@Autowired
	UserService userService;
	
	private User demoUser = new User();

	private void resetDemoUserValues() {
		demoUser.setEmail("demo@user.com");
		demoUser.setFirstname("firstname");
		demoUser.setLastname("lastname");
		demoUser.setPassword("SomePassword1234!");
		demoUser.setUserrole(UserRole.School);
	}

	private void setDemoUser() {
		// check if the demo user exists and add if it not:
		if (userService.getUser(demoUser.getEmail()).isEmpty()) {
			try {
				userService.createUser(demoUser);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
		}
	}

	private void removeDemoUser() {
		userService.deleteUser(demoUser.getEmail());
	}

	@Before()
	public void setUp() {
		resetDemoUserValues();
		setDemoUser();
	}

	@After()
	public void tearDown() {
		removeDemoUser();
	}
	
	/**
	 * The create user is already tested in the Before part of every test
	 * Only check if we get conflicts when we try to add the same user again
	 * or with bad credentials
	 */
	@Test
	public void createUserDuplication() {

		try {
			userService.createUser(demoUser);
		} catch (SQLIntegrityConstraintViolationException e) {
			// Should not get a constrain violation because the default user information is correct
			fail();
		} catch (UserAlreadyExistsException e) {
			// Should throw the user already exists violation
			return;
		}
		
		// Should definitely fail when the user is created because that means a duplication!
		fail();
	}
	
	/**
	 * The create user is already tested in the Before part of every test
	 * Only check if we get conflicts when we try to add the same user again
	 * or with bad credentials
	 */
	@Test
	public void createUserWrongInformation() {

		
		demoUser.setEmail("bad@email");
		
		try {
			userService.createUser(demoUser);
		} 
		catch(Exception e) {
			// Exception is wrapped but is throwing direct ConstraintViolationException
			// TODO: check what exactly Spring does what makes the running application throw the SQLIntegrityViolationException
			assertEquals(e.getCause().getCause().getClass(), ConstraintViolationException.class);
			return; 
		}
		
		// Should definitely fail when the user is created because that means the
		// constraint validation isn't working
		fail();
	}
	
	@Test
	public void getUser() {
		assertTrue(userService.getUser(demoUser.getEmail()).isPresent());
	}
	
	@Test
	public void getUsers() {
		assertTrue(userService.getUsers().size() > 0);
	}
}
