package org.ordina.ordinaForKids.availability;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventService;
import org.ordina.ordinaForKids.validation.AvailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.AvailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

	@Autowired
	AvailabilityRepository availabilityRepository;

	@Autowired
	CalendarEventService calendarEventService;

	@Override
	@Transactional
	public void createAvailability(Availability availability)
			throws AvailabilityOverlapsEventException, IllegalArgumentException {
		checkEventOverlap(availability);
		if (isInRepository(availability)) {
			throw new IllegalArgumentException();
		} else {
			availabilityRepository.save(availability);
		}
	}

	private void checkEventOverlap(Availability availability) throws AvailabilityOverlapsEventException {
		LocalDate availabilityDate = availability.getDate();
		Collection<CalendarEvent> eventsInDatabase = calendarEventService.getCalendarEvents();
		for (CalendarEvent calendarEvent : eventsInDatabase) {
			LocalDate calendarEventDate = calendarEvent.getDate();
			if (calendarEventDate.equals(availabilityDate)) {
				throw new AvailabilityOverlapsEventException(availabilityDate);
			}
		}
	}

	private boolean isInRepository(Availability availability) {
		long id = availability.getId();
		try {
			getAvailability(id);
			return true;
		} catch (AvailabilityNotFoundException e) {
			return false;
		}
	}

	@Override
	@Transactional
	public void deleteAvailability(Availability availability) throws AvailabilityNotFoundException {
		if (isInRepository(availability)) {
			availabilityRepository.delete(availability);
		} else {
			throw new AvailabilityNotFoundException(availability.getId());
		}
	}

	@Override
	public Availability getAvailability(Long id) throws AvailabilityNotFoundException {
		Optional<Availability> availability = availabilityRepository.findById(id);
		if (availability.isEmpty()) {
			throw new AvailabilityNotFoundException(id);
		}
		return availability.get();
	}

	@Override
	public List<Availability> getAllAvailabilities() throws AvailabilityNotFoundException {
		List<Availability> availabilities = availabilityRepository.findAll();
		if (availabilities.isEmpty()) {
			throw new AvailabilityNotFoundException();
		}
		return availabilities;
	}

}