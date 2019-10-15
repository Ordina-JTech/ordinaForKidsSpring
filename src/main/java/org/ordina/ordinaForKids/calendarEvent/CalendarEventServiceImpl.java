package org.ordina.ordinaForKids.calendarEvent;

import java.util.Collection;
import java.util.Optional;

import org.ordina.ordinaForKids.validation.CalendarEventNotFoundException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayReachedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalendarEventServiceImpl implements CalendarEventService {

	@Value("${ofk.events.max-per-day}")
	private long maxEventsPerDay; // max events per day for the calendar

	@Value("${ofk.events.max-per-day-per-owner}")
	private long maxEventsPerDayPerOwner;

	@Autowired
	CalendarEventRepository calendarEventRepository;

	@Override
	@Transactional
	public void createCalendarEvent(CalendarEvent calendarEvent)
			throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException {
		// check if the max number of events is exceeded based on application.properties
		// => ofk.events.maxperday
		if (calendarEventRepository.findAllByDate(calendarEvent.getDate()).size() >= maxEventsPerDay) {
			throw new MaximumNumberOfEventsPerDayReachedException(
					"Maximum number of events per day '" + maxEventsPerDay + "' has already been reached");
		}

		
		// check if the owner doesn't already have a booking for that date:
		if (calendarEventRepository.findAllByDateAndOwner(calendarEvent.getDate(), calendarEvent.getOwner())
				.size() >= maxEventsPerDayPerOwner) {
			throw new MaximumNumberOfEventsPerDayPerOwnerReachedException(
					"Can only book " + maxEventsPerDayPerOwner + " per day per owner");
		}

		calendarEventRepository.save(calendarEvent);

	}

	@Override
	@Transactional
	public void deleteCalendarEventsByOwner(String owner) {
		calendarEventRepository.deleteByOwner(owner);
	}
	
	@Override
	@Transactional
	public void deleteCalendarEvent(Long id) throws CalendarEventNotFoundException {
		
		Optional<CalendarEvent> calendarEvent = calendarEventRepository.findById(id);
		if (calendarEvent.isEmpty()) {
			throw new CalendarEventNotFoundException("Event not found");
		}
		calendarEventRepository.deleteById(id);
	}

	@Override
	public Optional<CalendarEvent> getCalendarEvent(Long id) {
		return calendarEventRepository.findById(id);
	}

	@Override
	public Collection<CalendarEvent> getCalendarEvents() {
		return calendarEventRepository.findAll(Sort.by("date").ascending());

	}

}
