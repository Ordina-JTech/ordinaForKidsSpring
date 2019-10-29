package org.ordina.ordinaForKids.availability;

import java.time.LocalDate;

import org.ordina.ordinaForKids.teachingModule.TeachingModule;

// We moeten hier de validaties definieren die aangeroepen worden in de Controller
public class AvailabilityDTO {

	private long id;
	// voorstel validatie: moet op of na vandaag zijn. Geen zaterdag of zondag.
	private LocalDate date;
	// voorstel validatie: moet gebruiker zijn van het type Ordina of Administrator
	private String loggedBy;
	private TeachingModule availableModule;

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
