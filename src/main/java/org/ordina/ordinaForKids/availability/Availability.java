package org.ordina.ordinaForKids.availability;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.ordina.ordinaForKids.teachingModule.TeachingModule;

@Entity
public class Availability {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "date", nullable = false)
	private LocalDate date;
	@Column(name = "logged_by", nullable = false)
	// Tim: hoezo kunnen we eigenlijk niet met de entity User werken, maar gebruiken
	// we string?
	private String loggedBy;
	// Tim: dit moeten we nog even bekijken of het nodig is vanuit de business, maar
	// zoals ik het een beetje begreep uit de meeting vorige week kan het zo zijn
	// dat verschillende teams verantwoordelijk zijn voor verschillende typen
	// workshops. Als we het type workshop hier specificeren hoeft de availability
	// niet meteen voor alle workshops te gelden.
	@Column(name = "available_module", nullable = false)
	private TeachingModule availableModule;


	
	/**
	 * Standard implementation, overridden in accordance with equals method. 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/**
	 * Standard equals method implementation, where availabilities with equal ID are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Availability other = (Availability) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getLoggedBy() {
		return loggedBy;
	}

	public void setLoggedBy(String loggedBy) {
		this.loggedBy = loggedBy;
	}

	public TeachingModule getAvailableModule() {
		return availableModule;
	}

	public void setAvailableModule(TeachingModule availableModule) {
		this.availableModule = availableModule;
	}

}