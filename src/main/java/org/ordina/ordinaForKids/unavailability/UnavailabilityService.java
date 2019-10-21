package org.ordina.ordinaForKids.unavailability;

import java.util.Collection;

import org.ordina.ordinaForKids.validation.UnavailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;

public interface UnavailabilityService {

	public abstract void createUnavailability(Unavailability unavailability)
			throws UnavailabilityOverlapsEventException;

	public abstract void deleteUnavailability(Unavailability unavailability);

	// Tim: ik heb dit anders gedaan dan in de CalendarEventService: daar wordt de
	// ruwe Optional teruggegeven. Leek mij meer een verantwoordelijkheid van de
	// service om te checken dat er geen lege optional terugkomt (als lege optional
	// door Repository wordt teruggegeven komt er in mijn voorstel een
	// UnavailabilityNotFoundException). Dit voorkomt ook dat we meer downstream
	// steeds moeten checken of de optional niet leeg is.
	public abstract Unavailability getUnavailability(Long id) throws UnavailabilityNotFoundException;

	public abstract Collection<Unavailability> getAllUnavailabilities();

}
