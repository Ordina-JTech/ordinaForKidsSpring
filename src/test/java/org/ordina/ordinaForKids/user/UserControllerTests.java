package org.ordina.ordinaForKids.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.validation.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {
	protected MockMvc mvc;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Autowired
	WebApplicationContext webApplicationContext;

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
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@After()
	public void tearDown() {
		removeDemoUser();
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

	// test login
	@Test
	@WithMockUser(username = "demo@user.com", roles = "Administrator")
	public void testLoginShouldPass() throws Exception {
		MvcResult mvcResult = testGet("/login");
		assertEquals(200, mvcResult.getResponse().getStatus());
	}

	// test login should fail
	// controller will throw a 422 - unprocessable entity when trying to login with
	// unknown username
	@Test
	@WithMockUser(username = "wrong@user.name", roles = "Administrator")
	public void testLoginShouldFail() throws Exception {
		MvcResult mvcResult = testGet("/login");
		assertEquals(422, mvcResult.getResponse().getStatus());
	}

	// test a user that has a valid Administrator account
	@Test
	@WithMockUser(roles = "Administrator")
	public void testGetUserShouldPass() throws Exception {
		MvcResult mvcResult = testGet("/user");
		assertEquals(200, mvcResult.getResponse().getStatus());
	}

	// test a user that has a valid Schooluser account, and thus should be able to
	// access the /user endpoint:
	@Test()
	@WithMockUser(roles = "Schooluser")
	public void testGetUserShouldFail() throws Exception {
		MvcResult mvcResult = testGet("/user");
		assertEquals(403, mvcResult.getResponse().getStatus());
	}

	/**
	 * Test the create user flow from the controller
	 * 
	 * @throws Exception
	 */
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testCreateUserShouldPass() throws Exception {
		removeDemoUser();
		MvcResult mvcResult = testPost("/user", demoUser);

		// check if the response is correct
		assertEquals(200, mvcResult.getResponse().getStatus());

		// check if it returns the user as a JSONable Object:
		User user = mapFromJson(mvcResult.getResponse().getContentAsString(), User.class);
		assertEquals(user.getEmail(), demoUser.getEmail());
		assertEquals(user.getFirstname(), demoUser.getFirstname());
		assertEquals(user.getLastname(), demoUser.getLastname());
		assertEquals(user.getUserrole(), demoUser.getUserrole());
		assertNull(user.getPassword());

		// check if the user has been added
		assertTrue(userService.getUser(demoUser.getEmail()).isPresent());

		// clean up the user
		userService.deleteUser(demoUser.getEmail());
		assertTrue(userService.getUser(demoUser.getEmail()).isEmpty());
	}

	/**
	 * Test the create user flow from the controller with a few scenarios that
	 * should fail
	 * 
	 * @throws Exception
	 */
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testCreateUserShouldFail() throws Exception {

		// try adding the demoUser again, this should fail
		MvcResult mvcResult = testPost("/user", demoUser);
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertEquals("User with email '" + demoUser.getEmail() + "' already exists",
				mvcResult.getResponse().getErrorMessage());
		removeDemoUser(); // cleanup
		assertTrue(userService.getUser(demoUser.getEmail()).isEmpty()); // check cleanup

		// try adding the user with incorrect fields:
		resetDemoUserValues();
		demoUser.setEmail("bad@email");
		mvcResult = testPost("/user", demoUser);
		assertEquals(400, mvcResult.getResponse().getStatus());

		resetDemoUserValues();
		demoUser.setPassword("");
		mvcResult = testPost("/user", demoUser);
		assertEquals(400, mvcResult.getResponse().getStatus());

		resetDemoUserValues();
		demoUser.setFirstname("A");
		mvcResult = testPost("/user", demoUser);
		assertEquals(400, mvcResult.getResponse().getStatus());
	}

	/**
	 * Validate the update method for the user
	 * No fail tests are added, field constrains are already validated by the create test
	 * @throws Exception
	 */
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testSetUser() throws Exception {
		final String NEWFIRSTNAME = "New first name";
		demoUser.setFirstname(NEWFIRSTNAME);
		MvcResult mvcResult = testPut("/user", demoUser);

		// check if the response is correct
		assertEquals(200, mvcResult.getResponse().getStatus());

		// check if the user is returned
		User user = mapFromJson(mvcResult.getResponse().getContentAsString(), User.class);
		assertEquals(user.getFirstname(), NEWFIRSTNAME);

		// check if persisted in the database:
		Optional<User> updatedUser = userService.getUser(demoUser.getEmail());
		assertTrue(updatedUser.isPresent());
		assertEquals(updatedUser.get().getFirstname(), NEWFIRSTNAME);
	}

	/**
	 * Validate the delete method for the user
	 * @throws Exception
	 */
	@Test()
	@WithMockUser(roles = "Administrator")
	public void testDeleteUser() throws Exception {
		
		MvcResult mvcResult = testDelete("/user/" + demoUser.getEmail());

		// check if the response is correct
		assertEquals(200, mvcResult.getResponse().getStatus());

		// check if the user is removed
		Optional<User> updatedUser = userService.getUser(demoUser.getEmail());
		assertTrue(updatedUser.isEmpty());
	}
	
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
}
