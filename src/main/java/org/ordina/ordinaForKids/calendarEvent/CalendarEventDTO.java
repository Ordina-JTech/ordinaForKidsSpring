package org.ordina.ordinaForKids.calendarEvent;

import java.time.LocalDate;

/**
 * Data Transfer Object for CalendarEvent
 * Use DTO to prevent unwanted modifications using the persisted entity (CalendarEvent)
 * @author Tim Misset
 *
 */
public class CalendarEventDTO {
	private long id;
	private LocalDate date;
	private String owner;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
}
