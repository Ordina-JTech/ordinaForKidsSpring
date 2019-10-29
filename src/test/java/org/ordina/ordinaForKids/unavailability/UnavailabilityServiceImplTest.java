package org.ordina.ordinaForKids.unavailability;

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
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventService;
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
	CalendarEventService mockCalendarEventService;

	List<Unavailability> mockUnavailabilities;

	@Before
	public void setup() {
		mockUnavailabilities = UnavailabilityTestHelper.createMockUnavailabilites(100);

		setStubFindAllUnavailabilitiesFromRepo();
		setStubGetCalendarEventsFromService(CalendarEventTestHelper.getMockCalendarEvents(3));
		setStubDeleteUnavailabilityFromRepo();
		setStubFindUnavailabilityByIdInRepo();
	}

	/**
	 * As long as date is not overlapping a date of an existing Unavailability, it
	 * should be possible to set a new Unavailability. In this test, a duplicate
	 * Unavailability of an existing Unavailability is saved, but with modified
	 * date, and ID set to 0 (to mimic its situation just after creation).
	 */
	@Test
	public void createUnavailabilityTest() throws UnavailabilityOverlapsEventException {
		// arrange
		Unavailability validUnavailability = UnavailabilityTestHelper.createMockUnavailabilites(1).get(0);
		LocalDate nonOverlappingDate = LocalDate.of(2010, 1, 1);
		validUnavailability.setId(0);
		validUnavailability.setDate(nonOverlappingDate);

		// act
		unavailabilityService.createUnavailability(validUnavailability);

		// assert
		verify(mockUnavailabilityRepo).save(validUnavailability);

	}

	@Test(expected = UnavailabilityOverlapsEventException.class)
	public void createUnavailabilityTestWithOverlappingDate() throws UnavailabilityOverlapsEventException {
		// arrange
		List<CalendarEvent> events = CalendarEventTestHelper.getMockCalendarEvents(3);
		Unavailability overlappingUnavailability = mockUnavailabilities.get(0);
		LocalDate overlappingDate = events.get(0).getDate();
		overlappingUnavailability.setDate(overlappingDate);

		// act
		unavailabilityService.createUnavailability(overlappingUnavailability);

		// assert
		verify(mockUnavailabilityRepo).save(overlappingUnavailability);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createNonUniqueUnavailabilityTest() throws UnavailabilityOverlapsEventException {
		// arrange
		Unavailability nonUniqueUnavailability = mockUnavailabilities.get(0);
		LocalDate nonOverlappingDate = LocalDate.of(2010, 1, 1);
		nonUniqueUnavailability.setDate(nonOverlappingDate);

		// act
		unavailabilityService.createUnavailability(nonUniqueUnavailability);

		// assert
		verify(mockUnavailabilityRepo).save(nonUniqueUnavailability);
	}

	@Test
	public void deleteUnavailabilityTest() throws UnavailabilityNotFoundException {
		// arrange
		Unavailability validUnavailability = mockUnavailabilities.get(0);
		int nrOfUnavailabilitiesBeforeDelete = mockUnavailabilities.size();

		// act
		unavailabilityService.deleteUnavailability(validUnavailability);
		int nrOfUnavailabilitiesAfterDelete = mockUnavailabilities.size();

		// verify
		verify(mockUnavailabilityRepo, times(1)).delete(validUnavailability);
		assertEquals(nrOfUnavailabilitiesBeforeDelete - 1, nrOfUnavailabilitiesAfterDelete);
		assertFalse(mockUnavailabilities.contains(validUnavailability));

	}

	@Test(expected = UnavailabilityNotFoundException.class)
	public void deleteNonExistingUnavailabilityTest() throws UnavailabilityNotFoundException {
		// arrange
		Unavailability nonExistingUnavailability = new Unavailability();
		int nrOfUnavailabilitiesBeforeDelete = mockUnavailabilities.size();
		assertFalse(mockUnavailabilities.contains(nonExistingUnavailability));

		// act
		unavailabilityService.deleteUnavailability(nonExistingUnavailability);
		int nrOfUnavailabilitiesAfterDelete = mockUnavailabilities.size();

		// verify
		verify(mockUnavailabilityRepo).delete(nonExistingUnavailability);
		assertEquals(nrOfUnavailabilitiesBeforeDelete, nrOfUnavailabilitiesAfterDelete);
	}

	@Test
	public void getUnavailability() throws UnavailabilityNotFoundException {
		// arrange
		final long VALID_UNAVAILABILITY_ID = 5;
		Unavailability expectedUnavailability = null;
		for (Unavailability unavailability : mockUnavailabilities) {
			if (unavailability.getId() == VALID_UNAVAILABILITY_ID) {
				expectedUnavailability = unavailability;
			}
		}

		// act
		Unavailability actualUnavailability = unavailabilityService.getUnavailability(VALID_UNAVAILABILITY_ID);

		// assert
		assertEquals(expectedUnavailability, actualUnavailability);
	}

	@Test(expected = UnavailabilityNotFoundException.class)
	public void getInvalidUnavailability() throws UnavailabilityNotFoundException {
		// arrange
		final long INVALID_UNAVAILABILITY_ID = 123456;

		// act
		unavailabilityService.getUnavailability(INVALID_UNAVAILABILITY_ID);

		// assert
		// expected: UnavailabilityNotFounbdException
	}

	@Test
	public void getAllUnavailabilitiesTest() throws UnavailabilityNotFoundException {
		// arrange
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
		// UnavailabilityNotFoundException should be thrown, as mock repository returns
		// empty list
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	// STUBS
	/////////////////////////////////////////////////////////////////////////////////////////////

	private void setStubFindAllUnavailabilitiesFromRepo() {
		when(mockUnavailabilityRepo.findAll()).thenReturn(mockUnavailabilities);
	}

	private void setStubGetCalendarEventsFromService(List<CalendarEvent> events) {
		when(mockCalendarEventService.getCalendarEvents()).thenReturn(events);
	}

	private void setStubDeleteUnavailabilityFromRepo() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				for (int i = 0; i < mockUnavailabilities.size(); i++) {
					if (mockUnavailabilities.get(i).equals(invocation.getArgument(0))) {
						mockUnavailabilities.remove(i);
						break;
					}
				}
				return null;
			}
		}).when(mockUnavailabilityRepo).delete(any(Unavailability.class));
	}

	private void setStubFindUnavailabilityByIdInRepo() {
		when(mockUnavailabilityRepo.findById(anyLong())).thenAnswer(invocation -> {
			Long unavailabilityId = invocation.getArgument(0);
			Optional<Unavailability> unavailability = Optional.empty();
			for (Unavailability mockU : mockUnavailabilities) {
				if (mockU.getId() == unavailabilityId) {
					unavailability = Optional.of(mockU);
				}
			}
			return unavailability;
		});
	}

}