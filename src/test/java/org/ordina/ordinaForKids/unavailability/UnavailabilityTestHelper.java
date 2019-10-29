package org.ordina.ordinaForKids.unavailability;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ordina.ordinaForKids.teachingModule.TeachingModules;

public class UnavailabilityTestHelper {

	protected static List<Unavailability> createMockUnavailabilites(int mockUnavailabilitySize) {
		List<Unavailability> unavailabilities = new ArrayList<>();
		for (int i = 0; mockUnavailabilitySize > i; i++) {
			Unavailability unavailability = new Unavailability();
			unavailability.setDate(LocalDate.of(2019, (i % 12) + 1, (i % 27) + 1));
			unavailability.setLoggedBy("demo" + i + "@user.com");
			unavailability.setReason("vakantie");
			unavailability.setUnavailableModule(TeachingModules.AGILE);
			unavailability.setId(i+1);
			unavailabilities.add(unavailability);
		}
		return unavailabilities;
	}

}
