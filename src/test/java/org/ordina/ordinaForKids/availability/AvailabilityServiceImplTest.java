package org.ordina.ordinaForKids.availability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ordina.ordinaForKids.availability.Availability;
import org.ordina.ordinaForKids.availability.AvailabilityRepository;
import org.ordina.ordinaForKids.availability.AvailabilityServiceImpl;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventService;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventTestHelper;
import org.ordina.ordinaForKids.validation.AvailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.AvailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AvailabilityServiceImplTest {

	@Autowired
	AvailabilityServiceImpl availabilityService;

	@MockBean
	AvailabilityRepository mockAvailabilityRepo;
	@MockBean
	CalendarEventService mockCalendarEventService;

	List<Availability> mockAvailabilities;

	@Before
	public void setup() {
		mockAvailabilities = AvailabilityTestHelper.createMockAvailabilites(100);

		setStubFindAllAvailabilitiesFromRepo();
		setStubGetCalendarEventsFromService(CalendarEventTestHelper.getMockCalendarEvents(3));
		setStubDeleteAvailabilityFromRepo();
		setStubFindAvailabilityByIdInRepo();
	}

	/**
	 * As long as date is not overlapping a date of an existing Availability, it
	 * should be possible to set a new Availability. In this test, a duplicate
	 * Availability of an existing Availability is saved, but with modified
	 * date, and ID set to 0 (to mimic its situation just after creation).
	 */
	@Test
	public void createAvailabilityTest() throws AvailabilityOverlapsEventException {
		// arrange
		Availability validAvailability = AvailabilityTestHelper.createMockAvailabilites(1).get(0);
		LocalDate nonOverlappingDate = LocalDate.of(2010, 1, 1);
		validAvailability.setId(0);
		validAvailability.setDate(nonOverlappingDate);

		// act
		availabilityService.createAvailability(validAvailability);

		// assert
		verify(mockAvailabilityRepo).save(validAvailability);

	}

	@Test(expected = AvailabilityOverlapsEventException.class)
	public void createAvailabilityTestWithOverlappingDate() throws AvailabilityOverlapsEventException {
		// arrange
		List<CalendarEvent> events = CalendarEventTestHelper.getMockCalendarEvents(3);
		Availability overlappingAvailability = mockAvailabilities.get(0);
		LocalDate overlappingDate = events.get(0).getDate();
		overlappingAvailability.setDate(overlappingDate);

		// act
		availabilityService.createAvailability(overlappingAvailability);

		// assert
		verify(mockAvailabilityRepo).save(overlappingAvailability);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createNonUniqueAvailabilityTest() throws AvailabilityOverlapsEventException {
		// arrange
		Availability nonUniqueAvailability = mockAvailabilities.get(0);
		LocalDate nonOverlappingDate = LocalDate.of(2010, 1, 1);
		nonUniqueAvailability.setDate(nonOverlappingDate);

		// act
		availabilityService.createAvailability(nonUniqueAvailability);

		// assert
		verify(mockAvailabilityRepo).save(nonUniqueAvailability);
	}

	@Test
	public void deleteAvailabilityTest() throws AvailabilityNotFoundException {
		// arrange
		Availability validAvailability = mockAvailabilities.get(0);
		int nrOfAvailabilitiesBeforeDelete = mockAvailabilities.size();

		// act
		availabilityService.deleteAvailability(validAvailability);
		int nrOfAvailabilitiesAfterDelete = mockAvailabilities.size();

		// verify
		verify(mockAvailabilityRepo, times(1)).delete(validAvailability);
		assertEquals(nrOfAvailabilitiesBeforeDelete - 1, nrOfAvailabilitiesAfterDelete);
		assertFalse(mockAvailabilities.contains(validAvailability));

	}

	@Test(expected = AvailabilityNotFoundException.class)
	public void deleteNonExistingAvailabilityTest() throws AvailabilityNotFoundException {
		// arrange
		Availability nonExistingAvailability = new Availability();
		int nrOfAvailabilitiesBeforeDelete = mockAvailabilities.size();
		assertFalse(mockAvailabilities.contains(nonExistingAvailability));

		// act
		availabilityService.deleteAvailability(nonExistingAvailability);
		int nrOfAvailabilitiesAfterDelete = mockAvailabilities.size();

		// verify
		verify(mockAvailabilityRepo).delete(nonExistingAvailability);
		assertEquals(nrOfAvailabilitiesBeforeDelete, nrOfAvailabilitiesAfterDelete);
	}

	@Test
	public void getAvailability() throws AvailabilityNotFoundException {
		// arrange
		final long VALID_AVAILABILITY_ID = 5;
		Availability expectedAvailability = null;
		for (Availability availability : mockAvailabilities) {
			if (availability.getId() == VALID_AVAILABILITY_ID) {
				expectedAvailability = availability;
			}
		}

		// act
		Availability actualAvailability = availabilityService.getAvailability(VALID_AVAILABILITY_ID);

		// assert
		assertEquals(expectedAvailability, actualAvailability);
	}

	@Test(expected = AvailabilityNotFoundException.class)
	public void getInvalidAvailability() throws AvailabilityNotFoundException {
		// arrange
		final long INVALID_AVAILABILITY_ID = 123456;

		// act
		availabilityService.getAvailability(INVALID_AVAILABILITY_ID);

		// assert
		// expected: AvailabilityNotFoundException
	}

	@Test
	public void getAllAvailabilitiesTest() throws AvailabilityNotFoundException {
		// arrange
		int nrOfMockAvailabilities = mockAvailabilities.size();

		// act
		List<Availability> availabilities = availabilityService.getAllAvailabilities();

		// assert
		int nrOfReturnedAvailabilities = availabilities.size();
		assertEquals(nrOfMockAvailabilities, nrOfReturnedAvailabilities);
	}

	@Test(expected = AvailabilityNotFoundException.class)
	public void getAllAvailabilitiesTestIfNoAvailabilitiesStored() throws AvailabilityNotFoundException {
		// arrange
		List<Availability> emptyList = new ArrayList<Availability>();
		when(mockAvailabilityRepo.findAll()).thenReturn(emptyList);

		// act
		availabilityService.getAllAvailabilities();

		// assert
		// AvailabilityNotFoundException should be thrown, as mock repository returns
		// empty list
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	/////////////////////////////////////////////////////////////////////////////////////////////

	private void setStubFindAllAvailabilitiesFromRepo() {
		when(mockAvailabilityRepo.findAll()).thenReturn(mockAvailabilities);
	}

	private void setStubGetCalendarEventsFromService(List<CalendarEvent> events) {
		when(mockCalendarEventService.getCalendarEvents()).thenReturn(events);
	}

	private void setStubDeleteAvailabilityFromRepo() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				for (int i = 0; i < mockAvailabilities.size(); i++) {
					if (mockAvailabilities.get(i).equals(invocation.getArgument(0))) {
						mockAvailabilities.remove(i);
						break;
					}
				}
				return null;
			}
		}).when(mockAvailabilityRepo).delete(any(Availability.class));
	}

	private void setStubFindAvailabilityByIdInRepo() {
		when(mockAvailabilityRepo.findById(anyLong())).thenAnswer(invocation -> {
			Long availabilityId = invocation.getArgument(0);
			Optional<Availability> availability = Optional.empty();
			for (Availability mockU : mockAvailabilities) {
				if (mockU.getId() == availabilityId) {
					availability = Optional.of(mockU);
				}
			}
			return availability;
		});
	}

}