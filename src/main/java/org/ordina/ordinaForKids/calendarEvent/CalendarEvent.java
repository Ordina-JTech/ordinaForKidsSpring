package org.ordina.ordinaForKids.calendarEvent;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "calendarEvent")
@EntityListeners(AuditingEntityListener.class)
public class CalendarEvent {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "owner", nullable = false)
    private String owner;
    
    /**
     * Generate a calendar event for an owner
     * @param owner
     */
    public CalendarEvent(String owner) {
    	this.setOwner(owner);
    }
    
    public CalendarEvent() {
    	
    }

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
	 * @return the start
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param start the start to set
	 */
	public void setDate(Date start) {
		this.date = start;
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
