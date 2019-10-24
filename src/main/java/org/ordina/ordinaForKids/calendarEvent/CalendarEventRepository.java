package org.ordina.ordinaForKids.calendarEvent;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

	public List<CalendarEvent> findAllByOwner(String owner);
	
	public List<CalendarEvent> findAllByDate(LocalDate date);
	
	public List<CalendarEvent> findAllByDateAndOwner(LocalDate date, String owner);
	
	public void deleteByOwner(String owner);
}
