package org.ordina.ordinaForKids.unavailability;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventService;
import org.ordina.ordinaForKids.validation.UnavailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnavailabilityServiceImpl implements UnavailabilityService {

	@Autowired
	UnavailabilityRepository unavailabilityRepository;

	@Autowired
	CalendarEventService calendarEventService;

	@Override
	@Transactional
	public void createUnavailability(Unavailability unavailability) throws UnavailabilityOverlapsEventException, IllegalArgumentException {
		checkEventOverlap(unavailability);
		if (isInRepository(unavailability)) {
			throw new IllegalArgumentException();
		} else {
			unavailabilityRepository.save(unavailability);
		}
	}

	private void checkEventOverlap(Unavailability unavailability) throws UnavailabilityOverlapsEventException {
		LocalDate unavailabilityDate = unavailability.getDate();
		Collection<CalendarEvent> eventsInDatabase = calendarEventService.getCalendarEvents();
		for (CalendarEvent calendarEvent : eventsInDatabase) {
			LocalDate calendarEventDate = calendarEvent.getDate();
			if (calendarEventDate.equals(unavailabilityDate)) {
				throw new UnavailabilityOverlapsEventException(unavailabilityDate);
			}
		}
	}

	private boolean isInRepository(Unavailability unavailability) {
		long id = unavailability.getId();
		try {
			getUnavailability(id);
			return true;
		} catch (UnavailabilityNotFoundException e) {
			return false;
		}
	}

	@Override
	@Transactional
	public void deleteUnavailability(Unavailability unavailability) throws UnavailabilityNotFoundException {
		if (isInRepository(unavailability)) {
			unavailabilityRepository.delete(unavailability);
		} else {
			throw new UnavailabilityNotFoundException(unavailability.getId());
		}
	}

	@Override
	public Unavailability getUnavailability(Long id) throws UnavailabilityNotFoundException {
		Optional<Unavailability> unavailability = unavailabilityRepository.findById(id);
		if (unavailability.isEmpty()) {
			throw new UnavailabilityNotFoundException(id);
		}
		return unavailability.get();
	}

	@Override
	public List<Unavailability> getAllUnavailabilities() throws UnavailabilityNotFoundException {
		List<Unavailability> unavailabilities = unavailabilityRepository.findAll();
		if (unavailabilities.isEmpty()) {
			throw new UnavailabilityNotFoundException();
		}
		return unavailabilities;
	}

}