package org.ordina.ordinaForKids.unavailability;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.ordina.ordinaForKids.teachingModule.TeachingModules;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
// Tim: ik weet niet wat @EntityListeners is, maar aangezien het in de CalenderEvent klasse staat heb ik het overgenomen.
@EntityListeners(AuditingEntityListener.class)
public class Unavailability {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// Tim: laten we nog even kijken of we niet beter LocalDate kunnen gebruiken.
	@Column(name = "date", nullable = false)
	private Date date;
	@Column(name = "logged_by", nullable = false)
	// Tim: hoezo kunnen we eigenlijk niet met de entity User werken, maar gebruiken
	// we string?
	private String loggedBy;
	// Tim: dit moeten we nog even bekijken of het nodig is vanuit de business, maar
	// zoals ik het een beetje begreep uit de meeting vorige week kan het zo zijn
	// dat verschillende teams verantwoordelijk zijn voor verschillende typen
	// workshops. Als we het type workshop hier specificeren hoeft de unavailability
	// niet meteen voor alle workshops te gelden.
	@Column(name = "unavailable_module", nullable = false)
	private TeachingModules unavailableModule;
	@Column(name = "reason", nullable = true)
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
