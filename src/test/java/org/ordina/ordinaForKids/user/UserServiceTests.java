package org.ordina.ordinaForKids.user;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.ordina.ordinaForKids.validation.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {

	@Autowired
	UserService userService;

	@MockBean
	UserRepository userRepository;

	@Before()
	public void setUp() {
		setMockUsers();
		
		setStubForFindOneByEmail();
		setStubForFindAll();
		setStubForSave();
	}
	
	@Test
	public void getAllUsersShouldPass() {
		// arrange
		
		// act
		Collection<User> users = userService.getUsers();
		
		// assert
		assertEquals(mockUsersSize, users.size());
	}
	
	@Test
	public void getUserByEmailShouldPass() {
		// arrange
		String email = "demo@user.com";
		
		// act
		Optional<User> existingUser = null;
		try {
			existingUser = userService.getUser(email);
		} catch (UserNotFoundException e) {
			fail(); // should not throw user not found exception
		}
		
		// assert
		assertTrue(existingUser.isPresent());
	}
	
	@Test
	public void getUserByWrongEmailShouldPassThrowUserNotFoundException() {
		// arrange
		String email = "wrong@user.com";
		
		// act
		Optional<User> existingUser;
		
		try {
			existingUser = userService.getUser(email);
		} catch (UserNotFoundException e) {
			return; // passed the test
		}
		
		// assert
		fail(); // failed the test if the UserNotFoundException is not thrown
	}
	
	/**
	 * The create user is already tested in the Before part of every test Only check
	 * if we get conflicts when we try to add the same user again or with bad
	 * credentials
	 */
	@Test
	public void createUserDuplicationShouldThrowUserAlreadyExistsException() {
		// arrange
		User newUser = new User();
		newUser.setEmail(mockUsers.get(0).getEmail());
		
		
		// act
		try {
			userService.createUser(newUser);
		} catch (SQLIntegrityConstraintViolationException e) {
			// Should not get a constrain violation because the default user information is
			// correct
			fail();
		} catch (UserAlreadyExistsException e) {
			// Should throw the user already exists violation
			return;
		}
		
		// assert
		fail();
	}	

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private void setStubForFindAll() {
		when(userRepository.findAll()).thenReturn(mockUsers);
	}
	
	private void setStubForFindOneByEmail() {
		doAnswer(new Answer<Optional<User>>() {
			@Override
			public Optional<User> answer(InvocationOnMock invocation) throws Throwable {
				String email = invocation.getArgument(0);
				return mockUsers.stream().filter(user -> user.getEmail().equals(email)).findFirst();
			}
		}).when(userRepository).findOneByEmail(any(String.class));
	}
	
	private void setStubForSave() {
		when(userRepository.save(any(User.class))).thenReturn(new User());
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// HELPER METHODS
	// /////////////////////////////////////////////////////////////////////////////////////////////

	// HELPERS FOR MOCK DATA
	private List<User> mockUsers;
	private int mockUsersSize = 5;

	private void setMockUsers() {
		mockUsers = new ArrayList<User>();

		for (int i = 0; i < mockUsersSize; i++) {
			User user = new User();
			user.setEmail("demo" + (i == 0 ? "" : i) + "@user.com"); // <-- most tests will use demo@user.com, reads
																		// better than demo0@user.com.
			user.setFirstname("firstname " + i);
			user.setLastname("lastname " + i);
			user.setPassword("somepassword1234" + i); // <-- the password ..
			user.setUserrole(UserRole.School); // <-- .. and the role are not part of the tests since they are handled
												// by the MVC security context
			mockUsers.add(user);
		}

	}

	
}
