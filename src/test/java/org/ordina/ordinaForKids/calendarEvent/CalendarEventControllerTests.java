package org.ordina.ordinaForKids.calendarEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;
import java.util.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.validation.CalendarEventNotFoundException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalendarEventControllerTests {
	protected MockMvc mvc;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	CalendarEventService calendarEventService;
	
	@Value("${ofk.events.max-per-day}")
	private long maxEventsPerDay; // max events per day for the calendar
	

	@Before()
	public void setUp() {
		resetDemoEventValues();
		setDemoEvent();
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@After()
	public void tearDown() {
		removeDemoEvent();
	}
	
	private CalendarEvent demoEvent = new CalendarEvent();
	
	private void resetDemoEventValues() {

		demoEvent.setDate(getCalendarForDay(2019, 10, 30).getTime());
		demoEvent.setOwner("demo@user.com");
		
	}
	private Calendar getCalendarForDay(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar;
	}
	
	
	private void setDemoEvent() {
		// check if the demo user exists and add if it not:
		if (calendarEventService.getCalendarEvent(demoEvent.getId()).isEmpty()) {
			try {
				calendarEventService.createCalendarEvent(demoEvent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
		}
	}

	private void removeDemoEvent() {
		calendarEventService.deleteCalendarEventsByOwner(demoEvent.getOwner());
		cleanUpAdditionalDemoEvents();
	}
	
	@Test
	@WithMockUser(username="demo@user.com", roles="School")
	public void testGetEvents() throws Exception {
		MvcResult mvcResult = testGet("/calendar_events");
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertTrue(mapFromJson(mvcResult.getResponse().getContentAsString(), CalendarEvent[].class).length > 0);
	}
	
	@Test
	@WithMockUser(username="demo@user.com", roles="School")
	public void testCreateEventShouldThrowMaximumEventsPerUserPerDayException() throws Exception {
		MvcResult mvcResult = testPost("/calendar_events", demoEvent);
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertTrue(mvcResult.getResponse().getErrorMessage().contains("Can only book 1 per day per owner"));
	}
	
	@Test
	@WithMockUser(username="demo@user.com", roles="School")
	public void testCreateEventShouldThrowMaximumEventsPerDayException() throws Exception {
		CalendarEvent calendarEvent = null;
		for(int i = 0; i < maxEventsPerDay; i++) {
			// add max number of allowed events per day
			calendarEvent = new CalendarEvent();
			calendarEvent.setOwner("demo" + i + "@user.com");
			calendarEvent.setDate(getCalendarForDay(2019, 11, 1).getTime());
			calendarEventService.createCalendarEvent(calendarEvent);
		}
		
		// then try to add one more which should throw the exception
		calendarEvent.setOwner("demo" + maxEventsPerDay + "@user.com");
		
		// wrap in try statement so the cleanup will still be done
		MvcResult mvcResult = testPost("/calendar_events", calendarEvent);
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertTrue(mvcResult.getResponse().getErrorMessage().contains("Maximum number of events per day '" + maxEventsPerDay + "' has"));
		
	}
	
	private void cleanUpAdditionalDemoEvents() {
		//cleanup additional demo events that have been created by the testCreateEventShouldThrowMaximumEventsPerDayException
		for(int i = 0; i < maxEventsPerDay; i++) {
			calendarEventService.deleteCalendarEventsByOwner("demo" + i + "@user.com");
		}
	}
	
	
	private MvcResult testGet(String uri) throws Exception {
		return mvc.perform(MockMvcRequestBuilders

				.get(uri).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private MvcResult testPost(String uri, Object postObject) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE)
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
