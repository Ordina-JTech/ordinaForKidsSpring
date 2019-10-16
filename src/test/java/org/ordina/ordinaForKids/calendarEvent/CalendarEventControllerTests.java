package org.ordina.ordinaForKids.calendarEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ordina.ordinaForKids.validation.CalendarEventNotFoundException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayReachedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalendarEventControllerTests {
	protected MockMvc mvc;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Autowired
	WebApplicationContext webApplicationContext;
	

	@MockBean
	CalendarEventService calendarEventService;

	@Value("${ofk.events.max-per-day}")
	private long maxEventsPerDay; // max events per day for the calendar

	@Value("${ofk.events.max-per-day-per-owner}")
	private long maxEventsPerDayPerOwner;
	
	

	@Before()
	public void setUp() throws CalendarEventNotFoundException, MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		// set the mock events
		 mockCalendarEvents = CalendarEventTestHelper.getMockCalendarEvents(mockCalendarEventSize);

		// and mock Mvc
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

		// then assign the stubs
		setStubForGetCalendarEvents();
		setStubForGetCalendarEvent();
		setStubForDeleteCalendarEvent();
		setStubForCreateCalendarEvent();
	}

	

	@Test
	@WithMockUser(username = "demo@user.com", roles = "School")
	public void testGetEvents() throws Exception {
		// arrange
		
		// act
		MvcResult mvcResult = testGet("/calendar_events");
		
		// assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertTrue(mapFromJson(mvcResult.getResponse().getContentAsString(), CalendarEvent[].class).length == 10);
	}

	@Test
	@WithMockUser(username = "demo@user.com", roles = "School")
	public void testDeleteEvent() throws Exception {
		// arrange
		
		// act
		MvcResult mvcResult = testDelete("/calendar_events/" + mockCalendarEvents.get(0).getId());
		
		// assert
		assertEquals(mockCalendarEventSize - 1, mockCalendarEvents.size());

	}

	@Test
	@WithMockUser(username="demo@user.com", roles="School")
	public void testCreateEventShouldThrowMaximumEventsPerUserPerDayException() throws Exception {
		// > arrange
		CalendarEvent newCalendarEvent = new CalendarEvent();
		newCalendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2019, 10, 1).getTime()); // <- mock repository already has event on this day for this user
		newCalendarEvent.setOwner("demo@user.com");
		
		// > act
		MvcResult mvcResult = testPost("/calendar_events", newCalendarEvent);
		
		// > assert
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertTrue(mvcResult.getResponse().getErrorMessage().contains("Can only book " + maxEventsPerDayPerOwner + " per day per owner"));
				
	}
	
	@Test
	@WithMockUser(username="second@user.com", roles="School")
	public void testCreateEventShouldThrowMaximumEventsPerDayException() throws Exception {
		// > arrange
		// add calendar events to match the maximum number of events per day:
		CalendarEventTestHelper.inflateToMaxEventsPerDay(2019, 12, 1, maxEventsPerDay, mockCalendarEvents); 
		
		// create a new event for the same date with a unique user:
		CalendarEvent newCalendarEvent = new CalendarEvent();
		newCalendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2019, 12, 1).getTime());
		newCalendarEvent.setOwner("second@user.com");
		
		// > act
		MvcResult mvcResult = testPost("/calendar_events", newCalendarEvent);
		
		
		// > assert
		assertEquals(422, mvcResult.getResponse().getStatus());
		assertTrue(mvcResult.getResponse().getErrorMessage().contains("Maximum number of events per day '" + maxEventsPerDay + "' has"));
		
	}
	
	@Test
	@WithMockUser(username="demo@user.com", roles="School")
	public void testCreateEventShouldPass() throws Exception {
		// > arrange
		CalendarEvent newCalendarEvent = new CalendarEvent();
		newCalendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2020, 1, 1).getTime()); 
		newCalendarEvent.setOwner("demo@user.com");
		
		// > act
		MvcResult mvcResult = testPost("/calendar_events", newCalendarEvent);
		
		// > assert
		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals(mockCalendarEventSize + 1, mockCalendarEvents.size());
				
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private void setStubForGetCalendarEvents() {
		when(calendarEventService.getCalendarEvents()).thenReturn(mockCalendarEvents);
	}
	
	private void setStubForGetCalendarEvent() {
		when(calendarEventService.getCalendarEvent(any(Long.class))).thenAnswer(new Answer<Optional<CalendarEvent>>() {

			@Override
			public Optional<CalendarEvent> answer(InvocationOnMock invocation) throws Throwable {
				Long id = invocation.getArgument(0);
				return mockCalendarEvents.stream().filter(calendarEvent -> calendarEvent.getId() == id).findFirst();
			}

		});
	}
	
	private void setStubForDeleteCalendarEvent() throws CalendarEventNotFoundException {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Long calendarEventId = invocation.getArgument(0);
				mockCalendarEvents.removeIf(event -> event.getId() == calendarEventId);
				return null;
			}
		}).when(calendarEventService).deleteCalendarEvent(any(Long.class));
	}
	
	private void setStubForCreateCalendarEvent() throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				CalendarEvent calendarEvent = invocation.getArgument(0);
				// check for limits of eventsPerDay and eventPerDayPerUser
				
				List<CalendarEvent> matchingEventsByDate = mockCalendarEvents.stream().filter(
						mockCalendarEvent -> mockCalendarEvent.getDate().compareTo(calendarEvent.getDate()) == 0)
						.collect(Collectors.toList());
				
				if (matchingEventsByDate.size() >= maxEventsPerDay) {
					throw new MaximumNumberOfEventsPerDayReachedException(
							"Maximum number of events per day '" + maxEventsPerDay + "' has already been reached");
				}
				
				List<CalendarEvent> matchingEventsByDateAndOwner = matchingEventsByDate.stream()
						.filter(mockCalendarEvent -> mockCalendarEvent.getOwner().equals(calendarEvent.getOwner()))
						.collect(Collectors.toList());

				
				if (matchingEventsByDateAndOwner.size() >= maxEventsPerDayPerOwner) {
					throw new MaximumNumberOfEventsPerDayPerOwnerReachedException(
							"Can only book " + maxEventsPerDayPerOwner + " per day per owner");
				}
				
				// passed all, add to mockRepository:
				mockCalendarEvents.add(calendarEvent);
				
				return null;
			}

		}).when(calendarEventService).createCalendarEvent(any(CalendarEvent.class));
	}
	

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// HELPER METHODS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	
	// HELPERS FOR MOCK DATA
	
	// mock calendar events to be used for general testing
	private int mockCalendarEventSize = 10;
	private List<CalendarEvent> mockCalendarEvents;
	
	
	
	// HELPERS FOR MOCK MVC <--> CONTROLLER INTERACTION
	
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
