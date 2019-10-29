package org.ordina.ordinaForKids.validation;

import org.ordina.ordinaForKids.availability.Availability;

public class AvailabilityNotFoundException extends Exception {

	public AvailabilityNotFoundException(Availability unavailability) {
		super(String.format("The Unavailability event with was not found. ID: %d - Date: %s. Module: %s",
				unavailability.getId(), unavailability.getDate(), unavailability.getAvailableModule()));
	}

	public AvailabilityNotFoundException(Long id) {
		super(String.format("The requested Unavailability with ID %s was not found.", id));
	}

	public AvailabilityNotFoundException() {
		super("The requested Unavailability / Unavailabilities were not found");
	}
}
