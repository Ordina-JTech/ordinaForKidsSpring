package org.ordina.ordinaForKids.calendarEvent;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

	public List<CalendarEvent> findAllByOwner(String owner);
	
	public List<CalendarEvent> findAllByDate(Date date);
	
	public List<CalendarEvent> findAllByDateAndOwner(Date date, String owner);
	
	public void deleteByOwner(String owner);
}
