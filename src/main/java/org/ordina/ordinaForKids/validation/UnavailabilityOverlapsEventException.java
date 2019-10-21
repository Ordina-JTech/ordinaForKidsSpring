package org.ordina.ordinaForKids.validation;

import java.util.Date;

public class UnavailabilityOverlapsEventException extends Exception {

	public UnavailabilityOverlapsEventException(Date unavailabilityDate) {
		super("The date entered for unavailability ("+ unavailabilityDate + ") overlaps with an event");
	}
	
}
