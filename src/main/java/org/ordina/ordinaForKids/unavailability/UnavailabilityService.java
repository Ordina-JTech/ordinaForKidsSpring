package org.ordina.ordinaForKids.unavailability;

import java.util.List;

import org.ordina.ordinaForKids.validation.UnavailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;

public interface UnavailabilityService {

	public abstract void createUnavailability(Unavailability unavailability)
			throws UnavailabilityOverlapsEventException;

	public abstract void deleteUnavailability(Unavailability unavailability) throws UnavailabilityNotFoundException;

	public abstract Unavailability getUnavailability(Long id) throws UnavailabilityNotFoundException;

	public abstract List<Unavailability> getAllUnavailabilities() throws UnavailabilityNotFoundException;

}
