package org.ordina.ordinaForKids.availability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.validation.AvailabilityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AvailabilityControllerTest {
	protected MockMvc mvc;

	@MockBean
	AvailabilityService mockAvailabilityService;
	List<Availability> mockAvailabilities;
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Before
	public void setup() {
		mockAvailabilities = AvailabilityTestHelper.createMockAvailabilites(100);
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}
	
	@Test
	public void testGetEvents() throws Exception {
		// arrange
		
		// act
		MvcResult mvcResult = testGet("/availability");
		
		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertTrue(mapFromJson(mvcResult.getResponse().getContentAsString(), Availability[].class).length == 10);
	}

	
	private void setStubGetAvailabilitiesFromService(List<Availability> availabilities) throws AvailabilityNotFoundException {
		when(mockAvailabilityService.getAllAvailabilities()).thenReturn(mockAvailabilities);
	}
	
	private MvcResult testGet(String uri) throws Exception {
		return mvc.perform(MockMvcRequestBuilders

				.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}
}
