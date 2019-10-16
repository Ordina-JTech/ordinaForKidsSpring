package org.ordina.ordinaForKids.calendarEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.Sort;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalendarEventServiceTests {

	@Autowired
	CalendarEventService calendarEventService;

	@MockBean
	CalendarEventRepository calendarEventRepository;

	@Value("${ofk.events.max-per-day}")
	private long maxEventsPerDay; // max events per day for the calendar

	@Value("${ofk.events.max-per-day-per-owner}")
	private long maxEventsPerDayPerOwner;

	@Before()
	public void setUp() {
		mockCalendarEvents = CalendarEventTestHelper.getMockCalendarEvents(mockCalendarEventSize);

		setStubForFindAll();
		setStubForFindById();
		setStubForSave();
		setStubForDelete();
		setStubForDeleteByOwner();
		setStubForFindAllByDate();
		setStubForFindAllByDateAndOwner();
	}

	@Test
	public void getAllEventsShouldPass() {
		// arrange

		// act
		Collection<CalendarEvent> calendarEvents = calendarEventService.getCalendarEvents();

		// assert
		assertEquals(mockCalendarEventSize, calendarEvents.size());
	}

	@Test
	public void getCalendarEventByIdShouldPass() {
		// arrange
		Long id = mockCalendarEvents.get(0).getId();

		// act
		Optional<CalendarEvent> calendarEvent = calendarEventService.getCalendarEvent(id);

		// assert
		assertTrue(calendarEvent.isPresent());
	}

	@Test
	public void deleteCalendarEventByIdShouldPass() throws CalendarEventNotFoundException {
		// arrange
		CalendarEvent calendarEvent = mockCalendarEvents.get(0);

		// act
		calendarEventService.deleteCalendarEvent(calendarEvent.getId());

		// assert
		assertEquals(mockCalendarEventSize - 1, mockCalendarEvents.size());
		assertFalse(mockCalendarEvents.contains(calendarEvent));

	}

	@Test(expected = CalendarEventNotFoundException.class)
	public void deleteCalendarEventByWrongIdShouldThrowCalendarEventNotFoundException()
			throws CalendarEventNotFoundException {
		// arrange
		Long id = (long) -1;

		// act
		calendarEventService.deleteCalendarEvent(id);

		// assert
		fail();

	}

	@Test()
	public void deleteCalendarEventsByOwner() {
		// arrange
		String owner = mockCalendarEvents.get(0).getOwner();
		long eventsFromOwner = mockCalendarEvents.stream()
				.filter(mockCalendarEvent -> mockCalendarEvent.getOwner().equals(owner)).count();

		// act
		calendarEventService.deleteCalendarEventsByOwner(owner);

		// assert
		assertEquals(mockCalendarEventSize - eventsFromOwner, mockCalendarEvents.size());
	}

	@Test()
	public void createCalendarEventShouldPass()
			throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		// arrange
		CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2020, 1, 1).getTime());
		calendarEvent.setOwner("new@owner.com");

		// act
		calendarEventService.createCalendarEvent(calendarEvent);

		// assert
		assertEquals(mockCalendarEventSize + 1, mockCalendarEvents.size());
	}

	@Test(expected = MaximumNumberOfEventsPerDayReachedException.class)
	public void createCalendarEventShouldThrowMaximumNumberOfEventsPerDayReachedException()
			throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		// arrange
		mockCalendarEvents = new ArrayList<CalendarEvent>();
		CalendarEventTestHelper.inflateToMaxEventsPerDay(2020, 1, 1, maxEventsPerDay, mockCalendarEvents);
		CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2020, 1, 1).getTime());
		calendarEvent.setOwner("new@owner.com");

		// act
		calendarEventService.createCalendarEvent(calendarEvent);

		// assert
		fail();
	}
	
	@Test(expected = MaximumNumberOfEventsPerDayPerOwnerReachedException.class)
	public void createCalendarEventShouldThrowMaximumNumberOfEventsPerDayPerOwnerReachedException()
			throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		// arrange
		mockCalendarEvents = new ArrayList<CalendarEvent>();
		String owner = "new@owner.com";
		CalendarEventTestHelper.inflateToMaxEventsPerDayPerOwner(2020, 2, 1, maxEventsPerDayPerOwner, owner, mockCalendarEvents);
		CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(2020, 2, 1).getTime());
		calendarEvent.setOwner("new@owner.com");

		// act
		calendarEventService.createCalendarEvent(calendarEvent);

		// assert
		fail();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private void setStubForFindAll() {
		when(calendarEventRepository.findAll(any(Sort.class))).thenReturn(mockCalendarEvents);
	}

	private void setStubForFindAllByDate() {
		when(calendarEventRepository.findAllByDate(any(Date.class))).thenAnswer(invocation -> {
			Date compareToDate = invocation.getArgument(0);
			return mockCalendarEvents.stream().filter(event -> event.getDate().compareTo(compareToDate) == 0)
					.collect(Collectors.toList());
		});
	}

	private void setStubForFindAllByDateAndOwner() {
		when(calendarEventRepository.findAllByDateAndOwner(any(Date.class), any(String.class)))
				.thenAnswer(invocation -> {
					Date compareToDate = invocation.getArgument(0);
					String owner = invocation.getArgument(1);
					return mockCalendarEvents.stream().filter(
							event -> event.getDate().compareTo(compareToDate) == 0 && event.getOwner().equals(owner))
							.collect(Collectors.toList());
				});
	}

	private void setStubForFindById() {
		doAnswer(new Answer<Optional<CalendarEvent>>() {
			@Override
			public Optional<CalendarEvent> answer(InvocationOnMock invocation) throws Throwable {
				Long id = invocation.getArgument(0);
				return mockCalendarEvents.stream().filter(calendarEvent -> calendarEvent.getId() == id).findFirst();
			}
		}).when(calendarEventRepository).findById(any(Long.class));
	}

	private void setStubForSave() {
		when(calendarEventRepository.save(any(CalendarEvent.class))).thenAnswer(invocation -> {
			CalendarEvent calendarEventToSave = invocation.getArgument(0);
			if (calendarEventToSave.getId() == 0) {
				calendarEventToSave.setId(CalendarEventTestHelper.getNewCalendarEventId());
			}
			mockCalendarEvents.removeIf(calendarEvent -> calendarEvent.getId() == calendarEventToSave.getId());
			mockCalendarEvents.add(calendarEventToSave);
			return calendarEventToSave;
		});
	}

	private void setStubForDelete() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Long calendarEventId = invocation.getArgument(0);
				mockCalendarEvents.removeIf(calendarEvent -> calendarEvent.getId() == calendarEventId);

				return null;
			}
		}).when(calendarEventRepository).deleteById(any(Long.class));
	}

	private void setStubForDeleteByOwner() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				String owner = invocation.getArgument(0);

				mockCalendarEvents.removeIf(calendarEvent -> calendarEvent.getOwner() == owner);

				return null;
			}
		}).when(calendarEventRepository).deleteByOwner(any(String.class));
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// HELPER METHODS
	// /////////////////////////////////////////////////////////////////////////////////////////////

	// HELPERS FOR MOCK DATA
	private int mockCalendarEventSize = 10;
	private List<CalendarEvent> mockCalendarEvents;

}
