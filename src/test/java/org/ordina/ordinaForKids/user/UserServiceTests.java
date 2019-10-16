package org.ordina.ordinaForKids.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {

	@Autowired
	UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@MockBean
	UserRepository userRepository;

	@Before()
	public void setUp() {
		setMockUsers();
		
		setStubForFindOneByEmail();
		setStubForFindAll();
		setStubForSave();
		setStubForDelete();
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
	public void getUserByEmailShouldPass() throws UserNotFoundException {
		// arrange
		String email = "demo@user.com";
		
		// act
		Optional<User> existingUser = userService.getUser(email);
		
		// assert
		assertTrue(existingUser.isPresent());
	}
	
	@Test(expected=UserNotFoundException.class)
	public void getUserByWrongEmailShouldThrowUserNotFoundException() throws UserNotFoundException {
		// arrange
		String email = "wrong@user.com";
		
		// act
		userService.getUser(email);
		
		// assert
		fail(); // failed the test if the UserNotFoundException is not thrown
	}
	
	@Test(expected=UserAlreadyExistsException.class)
	public void createUserDuplicationShouldThrowUserAlreadyExistsException() throws SQLIntegrityConstraintViolationException, UserAlreadyExistsException {
		// arrange
		User newUser = new User();
		newUser.setEmail(mockUsers.get(0).getEmail());
		
		// act
		userService.createUser(newUser);
		
		// assert
		fail();
	}	
	
	@Test
	public void createNewUserShouldPass() throws SQLIntegrityConstraintViolationException, UserAlreadyExistsException {
		// arrange
		User newUser = new User();
		newUser.setEmail("new@user.com");
		newUser.setFirstname("firstname");
		newUser.setLastname("lastname");
		newUser.setPassword("somepassword");
		newUser.setUserrole(UserRole.OrdinaEmployee);
		
		// act
		userService.createUser(newUser);
		boolean newUserExists = mockUsers.stream().filter(user -> user.getEmail().equals(newUser.getEmail())).findFirst().isPresent();;
		
		// assert
		assertTrue(newUserExists);
	}
	
	@Test
	public void updateUserShouldPassAndPersistPasswordWhenNotProvided() throws UserNotFoundException {
		// arrange
		
		// field values
		String email = mockUsers.get(0).getEmail();
		String firstname = "new firstname";
		String lastname = mockUsers.get(0).getLastname();
		String password = null; 							// <-- null as input, should persist existing password
		UserRole userrole = UserRole.School;
		
		// values to object
		User userToUpdate = new User();
		userToUpdate.setEmail(email);
		userToUpdate.setFirstname(firstname); 
		userToUpdate.setLastname(lastname);
		userToUpdate.setUserrole(userrole);
		userToUpdate.setPassword(password);
		
		// act
		Optional<User> updatedUser = userService.updateUser(email, userToUpdate);
		
		// assert
		assertTrue(updatedUser.isPresent());
		assertEquals(mockUsersSize, mockUsersSize);
		assertEquals(firstname, updatedUser.get().getFirstname());
		assertNotNull(updatedUser.get().getPassword());
	}
	
	@Test
	public void updateUserShouldPassAndModifyPassword() throws UserNotFoundException {
		// arrange
		
		// field values
		String email = mockUsers.get(0).getEmail();
		String firstname = "new firstname";
		String lastname = mockUsers.get(0).getLastname();
		String password = "newpassword"; 							// <-- null as input, should persist existing password
		UserRole userrole = UserRole.School;
		
		// values to object
		User userToUpdate = new User();
		userToUpdate.setEmail(email);
		userToUpdate.setFirstname(firstname); 
		userToUpdate.setLastname(lastname);
		userToUpdate.setUserrole(userrole);
		userToUpdate.setPassword(password);
		
		// act
		Optional<User> updatedUser = userService.updateUser(email, userToUpdate);
		
		// assert
		assertTrue(updatedUser.isPresent());
		assertEquals(mockUsersSize, mockUsersSize);
		assertEquals(firstname, updatedUser.get().getFirstname());
		assertNotNull(updatedUser.get().getPassword());
		assertTrue(bCryptPasswordEncoder.matches(password, updatedUser.get().getPassword()));
		
		
	}
	
	@Test
	public void deleteUserWithExistingCredentialsShouldPass() throws UserNotFoundException {
		// arrange
		String email = mockUsers.get(0).getEmail();
		
		// act
		userService.deleteUser(email);
		
		// assert
		assertEquals(mockUsersSize - 1, mockUsers.size());
	}	
	
	@Test(expected=UserNotFoundException.class)
	public void deleteUserWithWrongCredentialsShouldThrowUserNotFoundException() throws UserNotFoundException {
		// arrange
		String email = "bad@email.com";
		
		// act
		userService.deleteUser(email);
		
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
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User userToSave = invocation.getArgument(0);
			Optional<User> existingUser = mockUsers.stream().filter(user -> user.getEmail().equals(userToSave.getEmail())).findFirst();
			if(existingUser.isPresent()) { mockUsers.remove(existingUser.get()); } 
			
			mockUsers.add(userToSave);
			return userToSave;
		});
	}
	
	private void setStubForDelete() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				String email = invocation.getArgument(0);
				Optional<User> optionalUser = mockUsers.stream().filter(user -> user.getEmail().equals(email)).findFirst();
				if(optionalUser.isEmpty()) {
					throw new UserNotFoundException(email);
				}
				mockUsers.remove(optionalUser.get());
				return null;
			}
		}).when(userRepository).deleteByEmail(any(String.class));
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
