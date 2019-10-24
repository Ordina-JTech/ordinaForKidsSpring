package org.ordina.ordinaForKids.validation;

import java.time.LocalDate;

public class UnavailabilityOverlapsEventException extends Exception {

	public UnavailabilityOverlapsEventException(LocalDate unavailabilityDate) {
		super("The date entered for unavailability ("+ unavailabilityDate + ") overlaps with an event");
	}
	
}
