package org.ordina.ordinaForKids.unavailability;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventServiceImpl;
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
	CalendarEventServiceImpl calendarEventServiceImpl;

	@Override
	@Transactional
	public void createUnavailability(Unavailability unavailability) throws UnavailabilityOverlapsEventException {
		checkEventOverlap(unavailability);
		unavailabilityRepository.save(unavailability);
	}

	private void checkEventOverlap(Unavailability unavailability) throws UnavailabilityOverlapsEventException {
		Date unavailabilityDate = unavailability.getDate();
		Collection<CalendarEvent> eventsInDatabase = calendarEventServiceImpl.getCalendarEvents();
		for (CalendarEvent calendarEvent : eventsInDatabase) {
			Date calendarEventDate = calendarEvent.getDate();
			if (calendarEventDate.compareTo(unavailabilityDate) == 0) {
				throw new UnavailabilityOverlapsEventException(unavailabilityDate);
			}
		}
	}

	@Override
	@Transactional
	public void deleteUnavailability(Unavailability unavailability) {
		unavailabilityRepository.delete(unavailability);
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