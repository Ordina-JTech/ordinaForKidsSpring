package org.ordina.ordinaForKids.unavailability;

import java.util.Date;

import org.ordina.ordinaForKids.teachingModule.TeachingModules;

// We moeten hier de validaties definieren die aangeroepen worden in de Controller
public class UnavailabilityDTO {

	private long id;
	// voorstel validatie: moet op of na vandaag zijn. Geen zaterdag of zondag.
	private Date date;
	// voorstel validatie: moet gebruiker zijn van het type Ordina of Administrator
	private String loggedBy;
	private TeachingModules unavailableModule;
	private String reason;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLoggedBy() {
		return loggedBy;
	}

	public void setLoggedBy(String loggedBy) {
		this.loggedBy = loggedBy;
	}

	public TeachingModules getUnavailableModule() {
		return unavailableModule;
	}

	public void setUnavailableModule(TeachingModules unavailableModule) {
		this.unavailableModule = unavailableModule;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
