package org.ordina.ordinaForKids.unavailability;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventServiceImpl;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventTestHelper;
import org.ordina.ordinaForKids.validation.UnavailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UnavailabilityServiceImplTest {

	@Autowired
	UnavailabilityServiceImpl unavailabilityService;

	@MockBean
	UnavailabilityRepository mockUnavailabilityRepo;
	@MockBean
	CalendarEventServiceImpl calendarEventService;

	List<Unavailability> mockUnavailabilities;

	@Before
	public void setup() {
		mockUnavailabilities = UnavailabilityTestHelper.createUnavailabilityList();
	}

	@Test
	public void getAllUnavailabilitiesTest() throws UnavailabilityNotFoundException {
		// arrange
		when(mockUnavailabilityRepo.findAll()).thenReturn(mockUnavailabilities);
		int nrOfMockUnavailabilities = mockUnavailabilities.size(); 

		// act
		List<Unavailability> unavailabilities = unavailabilityService.getAllUnavailabilities();

		// assert
		int nrOfReturnedUnavailabilities = unavailabilities.size();
		assertEquals(nrOfMockUnavailabilities, nrOfReturnedUnavailabilities);
	}

	@Test(expected = UnavailabilityNotFoundException.class)
	public void getAllUnavailabilitiesTestIfNoAvailabilitiesStored() throws UnavailabilityNotFoundException {
		// arrange
		List<Unavailability> emptyList = new ArrayList<Unavailability>();
		when(mockUnavailabilityRepo.findAll()).thenReturn(emptyList);
		
		// act
		unavailabilityService.getAllUnavailabilities();

		// assert
		// UnavailabilityNotFoundException should be thrown, as mock repository returns empty list
	}

	
	@Test
	public void createUnavailabilityTest() throws UnavailabilityOverlapsEventException {
		// arrange
		List<CalendarEvent> events =  CalendarEventTestHelper.getMockCalendarEvents(3);
		when(calendarEventService.getCalendarEvents()).thenReturn(events);
		Unavailability newUnavailability = UnavailabilityTestHelper.createUnavailabilityList().get(0);		
		LocalDate nonOverlappingDate = LocalDate.of(2010, 1, 1);
		newUnavailability.setDate(nonOverlappingDate);
		
		// act
		unavailabilityService.createUnavailability(newUnavailability);

		// assert
		verify(mockUnavailabilityRepo).save(newUnavailability);
	}
	
	@Test
	public void createUnavailabilityTestWithOverlappingDate() throws UnavailabilityOverlapsEventException {
		// arrange
		List<CalendarEvent> events =  CalendarEventTestHelper.getMockCalendarEvents(3);
		when(calendarEventService.getCalendarEvents()).thenReturn(events);
		Unavailability newUnavailability = UnavailabilityTestHelper.createUnavailabilityList().get(0);		
		LocalDate overlappingDate = events.get(0).getDate(); 
		newUnavailability.setDate(overlappingDate);
		
		
		// act
		unavailabilityService.createUnavailability(newUnavailability);

		// assert
		verify(mockUnavailabilityRepo).save(newUnavailability);
	}
	
}
