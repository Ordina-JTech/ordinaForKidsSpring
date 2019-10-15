package org.ordina.ordinaForKids.user;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayReachedException;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.ordina.ordinaForKids.validation.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {
	protected MockMvc mvc;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Autowired
	WebApplicationContext webApplicationContext;

	@MockBean
	UserService userService;

	@Before()
	public void setUp() throws SQLIntegrityConstraintViolationException, UserAlreadyExistsException, UserNotFoundException {
		// set the mock users
		setMockUsers();

		// set the mock Mvc
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

		// then assign the stubs for the mock service
		setStubForGetUsers();
		setStubForGetUser();
		setStubForCreateUser();
		setStubForUpdateUser();
		setStubForDeleteUser();
	}

	// test login
	@Test
	@WithMockUser(username = "demo@user.com", roles = "Administrator")
	public void testLoginWithAcceptableCredentialsShouldPass() throws Exception {
		// arrange

		// act
		MvcResult mvcResult = testGet("/login");

		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
	}

	@Test
	@WithMockUser(username = "wrong@user.name", roles = "Administrator")
	public void testLoginWithBadCredentialsShouldThrow403() throws Exception {
		// arrange

		// act
		MvcResult mvcResult = testGet("/login");

		// assert
		assertEquals(403, mvcResult.getResponse().getStatus());
	}

	@Test
	@WithMockUser(roles = "Administrator")
	public void testGetUserShouldPass() throws Exception {
		// arrange
		
		// act
		MvcResult mvcResult = testGet("/user");
		
		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals(mockUsersSize, mapFromJson(mvcResult.getResponse().getContentAsString(), User[].class).length);
	}

	@Test()
	@WithMockUser(roles = "Schooluser")
	public void testGetUsersWithIncorrectAuthenticationAuthorityShouldThrow403() throws Exception {
		// arrange
		
		// act
		MvcResult mvcResult = testGet("/user");
		
		// assert
		assertEquals(403, mvcResult.getResponse().getStatus());
	}
	
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testCreateUserAsAdministratorShouldPass() throws Exception {
		// arrange
		User newUser = new User();
		newUser.setEmail("new@user.com");
		newUser.setFirstname("firstname");
		newUser.setLastname("lastname");
		newUser.setPassword("somepassword123");
		newUser.setUserrole(UserRole.School);
				
		// act
		MvcResult mvcResult = testPost("/user", newUser);
		User createdUser = mapFromJson(mvcResult.getResponse().getContentAsString(), User.class);

		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals(newUser.getEmail(), createdUser.getEmail());
		assertEquals(mockUsersSize + 1, mockUsers.size());
	}
	
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testCreateUserWithDuplicateEmailShouldThrow422() throws Exception {
		// arrange
		User newUser = new User();
		newUser.setEmail("demo@user.com");
		newUser.setFirstname("firstname");
		newUser.setLastname("lastname");
		newUser.setPassword("somepassword123");
		newUser.setUserrole(UserRole.School);
				
		// act
		MvcResult mvcResult = testPost("/user", newUser);

		// assert
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertEquals(mockUsersSize, mockUsers.size());
	}
	
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testSetUserAsAdministratorWithWrongEmailShouldThrow404() throws Exception {
		// arrange
		User existingUser = new User();
		existingUser.setEmail("wrong@email.com");
		existingUser.setFirstname("new firstname");
		existingUser.setLastname("lastname");
		existingUser.setUserrole(UserRole.School);
				
		// act
		MvcResult mvcResult = testPut("/user", existingUser);
		
		// assert
		assertEquals(404, mvcResult.getResponse().getStatus());
		assertEquals(mockUsersSize, mockUsers.size());
	}
	
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testSetUserAsAdministratorShouldPass() throws Exception {
		// arrange
		User existingUser = mockUsers.get(0);
		existingUser.setFirstname("new firstname");
				
		// act
		MvcResult mvcResult = testPut("/user", existingUser);
		User modifiedUser = mapFromJson(mvcResult.getResponse().getContentAsString(), User.class);

		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals(existingUser.getEmail(), modifiedUser.getEmail());
		assertEquals(existingUser.getFirstname(), modifiedUser.getFirstname());
		assertEquals(mockUsersSize, mockUsers.size());
	}
	
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testDeleteUserAsAdministratorShouldPass() throws Exception {
		// arrange
		User existingUser = mockUsers.get(0);
			
		// act
		MvcResult mvcResult = testDelete("/user/" + existingUser.getEmail());
		
		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals(mockUsersSize - 1, mockUsers.size());
	}

	@Test()
	@WithMockUser(roles = "Administrator")
	public void testDeleteUserAsAdministratorWithWrongEmailShouldThrow404() throws Exception {
		// arrange
		String email = "wrong@email.com";
			
		// act
		MvcResult mvcResult = testDelete("/user/" + email);
		
		// assert
		assertEquals(404, mvcResult.getResponse().getStatus());
		assertEquals(mockUsersSize, mockUsers.size());
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	

	private void setStubForGetUsers() {
		when(userService.getUsers()).thenReturn(mockUsers);
	}

	private void setStubForGetUser() throws UserNotFoundException {
		when(userService.getUser(any(String.class))).thenAnswer(new Answer<Optional<User>>() {

			@Override
			public Optional<User> answer(InvocationOnMock invocation) throws Throwable {
				String email = invocation.getArgument(0);
				
				Optional<User> user = mockUsers.stream().filter(mockUser -> mockUser.getEmail().equals(email)).findFirst();
				
				if(user.isEmpty()) { 
					throw new UserNotFoundException(email);
				}
				return user;
			}

		});
	}
	
	private void setStubForCreateUser() throws SQLIntegrityConstraintViolationException, UserAlreadyExistsException  {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				User user = invocation.getArgument(0);
				
				if(mockUsers.stream().filter(mockUser -> mockUser.getEmail().equals(user.getEmail())).count() > 0) {
					throw new UserAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
				}
				
				// TODO: add some constraint validation logic 
				
				// passed all, add to mockRepository:
				mockUsers.add(user);
				
				return null;
			}

		}).when(userService).createUser(any(User.class));
	}
	
	private void setStubForUpdateUser() throws UserNotFoundException   {
		doAnswer(new Answer<Optional<User>>() {

			@Override
			public Optional<User> answer(InvocationOnMock invocation) throws Throwable {
				
				String email = invocation.getArgument(0);
				
				User user = invocation.getArgument(1);
				
				Optional<User> optionalExistingUser = mockUsers.stream().filter(mockUser -> mockUser.getEmail().equals(email)).findFirst();
				if(optionalExistingUser.isEmpty()) {
					throw new UserNotFoundException("Could not find user with " + email);
				}
				User existingUser = optionalExistingUser.get();
				existingUser.setFirstname(user.getFirstname());
				existingUser.setLastname(user.getLastname());
				existingUser.setUserrole(user.getUserrole());
				
				return optionalExistingUser;
			}

		}).when(userService).updateUser(any(String.class), any(User.class));
	}
	
	private void setStubForDeleteUser() throws UserNotFoundException   {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				
				String email = invocation.getArgument(0);
				Optional<User> optionalExistingUser = mockUsers.stream().filter(mockUser -> mockUser.getEmail().equals(email)).findFirst();
				
				if(optionalExistingUser.isEmpty()) {
					throw new UserNotFoundException("Could not find user with " + email);
				}
				
				mockUsers.remove(optionalExistingUser.get());
				return null;
			}

		}).when(userService).deleteUser((any(String.class)));
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

	// HELPERS FOR MOCK MVC <--> CONTROLLER INTERACTION
	private MvcResult testGet(String uri) throws Exception {
		return mvc.perform(MockMvcRequestBuilders

				.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private MvcResult testPost(String uri, Object postObject) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(postObject)).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private MvcResult testPut(String uri, Object postObject) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(postObject)).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private MvcResult testDelete(String uri) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.delete(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}
}
