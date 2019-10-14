package org.ordina.ordinaForKids.calendarEvent;

import java.util.Collection;
import java.util.Optional;

import org.ordina.ordinaForKids.validation.CalendarEventNotFoundException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayReachedException;

public interface CalendarEventService {

	public abstract void createCalendarEvent(CalendarEvent calendarEvent) throws MaximumNumberOfEventsPerDayReachedException, MaximumNumberOfEventsPerDayPerOwnerReachedException;
	public abstract void deleteCalendarEvent(Long id) throws CalendarEventNotFoundException;
	public abstract Optional<CalendarEvent> getCalendarEvent(Long id);
	public abstract Collection<CalendarEvent> getCalendarEvents();
	void deleteCalendarEventsByOwner(String owner);
	
}
