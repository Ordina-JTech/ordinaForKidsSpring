package org.ordina.ordinaForKids.validation;

import org.ordina.ordinaForKids.unavailability.Unavailability;

public class UnavailabilityNotFoundException extends Exception {

	public UnavailabilityNotFoundException(Unavailability unavailability) {
		super(String.format("The Unavailability event with was not found. ID: %d - Date: %s. Module: %s",
				unavailability.getId(), unavailability.getDate(), unavailability.getUnavailableModule()));
	}

	public UnavailabilityNotFoundException(Long id) {
		super(String.format("The requested Unavailability with ID %s was not found.", id));
	}
}
