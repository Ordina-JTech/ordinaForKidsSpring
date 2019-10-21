package org.ordina.ordinaForKids.unavailability;

import java.util.Date;

import org.ordina.ordinaForKids.teachingModule.TeachingModules;

public class UnavailabilityTestHelper {

	protected static Unavailability createUnavailability(){
		Unavailability unavailability = new Unavailability();
		unavailability.setDate(new Date());
		unavailability.setLoggedBy("demo@user.com");
		unavailability.setReason("vakantie");
		unavailability.setUnavailableModule(TeachingModules.AGILE);
		return unavailability;
	}

}
