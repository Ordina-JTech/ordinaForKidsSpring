package org.ordina.ordinaForKids.availability;

import java.util.List;

import org.ordina.ordinaForKids.validation.AvailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.AvailabilityOverlapsEventException;

public interface AvailabilityService {

	public abstract void createAvailability(Availability availability)
			throws AvailabilityOverlapsEventException;

	public abstract void deleteAvailability(Availability availability) throws AvailabilityNotFoundException;

	public abstract Availability getAvailability(Long id) throws AvailabilityNotFoundException;

	public abstract List<Availability> getAllAvailabilities() throws AvailabilityNotFoundException;

}
