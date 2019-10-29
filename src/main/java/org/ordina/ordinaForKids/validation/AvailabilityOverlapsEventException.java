package org.ordina.ordinaForKids.validation;

import java.time.LocalDate;

public class AvailabilityOverlapsEventException extends Exception {

	public AvailabilityOverlapsEventException(LocalDate unavailabilityDate) {
		super("The date entered for unavailability ("+ unavailabilityDate + ") overlaps with an event");
	}
	
}
