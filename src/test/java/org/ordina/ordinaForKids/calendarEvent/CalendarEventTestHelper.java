package org.ordina.ordinaForKids.calendarEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class CalendarEventTestHelper {
	
	private static long calendarEventId = 1;
	
	public static List<CalendarEvent> getMockCalendarEvents(int mockCalendarEventSize) {
		List<CalendarEvent> mockCalendarEvents = new ArrayList<CalendarEvent>();
		for (int i = 0; i < mockCalendarEventSize; i++) {
			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setDate(getCalendarForDay(2019, 10, i + 1).getTime());
			calendarEvent.setOwner("demo@user.com");
			calendarEvent.setId(getNewCalendarEventId());
			mockCalendarEvents.add(calendarEvent);
		}
		return mockCalendarEvents;
	}
	
	public static long getNewCalendarEventId() {
		return calendarEventId++;
	}
	
	public static Calendar getCalendarForDay(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}
	// additional calendar events to be used for exception handling:
	// create maximum number of events per day to trigger the exception associated with it
	public static void inflateToMaxEventsPerDay(int year, int month, int day, long maxEventsPerDay, List<CalendarEvent> mockCalendarEvents) {
		for (int i = 0; i < maxEventsPerDay; i++) {
			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(year, month, day).getTime());
			calendarEvent.setOwner("demo" + i + "@user.com");
			calendarEvent.setId(mockCalendarEvents.size());
			mockCalendarEvents.add(calendarEvent);
		}
		return;
	}
	
	// additional calendar events to be used for exception handling:
	// create maximum number of events per day to trigger the exception associated with it
	public static void inflateToMaxEventsPerDayPerOwner(int year, int month, int day, long maxEventsPerDayPerOwner, String owner, List<CalendarEvent> mockCalendarEvents) {
		for (int i = 0; i < maxEventsPerDayPerOwner; i++) {
			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setDate(CalendarEventTestHelper.getCalendarForDay(year, month, day).getTime());
			calendarEvent.setOwner(owner);
			calendarEvent.setId(mockCalendarEvents.size());
			mockCalendarEvents.add(calendarEvent);
		}
		return;
	}
}
