package org.ordina.ordinaForKids.unavailability;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ordina.ordinaForKids.teachingModule.TeachingModules;

public class UnavailabilityTestHelper {

	protected static final int NUMBER_OF_MOCK_UNAVAILABILITIES = 10;

	protected static List<Unavailability> createUnavailabilityList() {
		List<Unavailability> unavailabilities = new ArrayList<>();
		for (int i = 0; NUMBER_OF_MOCK_UNAVAILABILITIES > i; i++) {
			Unavailability unavailability = new Unavailability();
			unavailability.setDate(LocalDate.of(2019, i + 1, i + 10));
			unavailability.setLoggedBy("demo" + i + "@user.com");
			unavailability.setReason("vakantie");
			unavailability.setUnavailableModule(TeachingModules.AGILE);
			unavailabilities.add(unavailability);
		}
		return unavailabilities;
	}

}
