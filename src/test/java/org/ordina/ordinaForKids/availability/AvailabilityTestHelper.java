package org.ordina.ordinaForKids.availability;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ordina.ordinaForKids.availability.Availability;
import org.ordina.ordinaForKids.teachingModule.TeachingModules;

public class AvailabilityTestHelper {

	protected static List<Availability> createMockAvailabilites(int mockAvailabilitySize) {
		List<Availability> availabilities = new ArrayList<>();
		for (int i = 0; mockAvailabilitySize > i; i++) {
			Availability availability = new Availability();
			availability.setDate(LocalDate.of(2019, (i % 12) + 1, (i % 27) + 1));
			availability.setLoggedBy("demo" + i + "@user.com");
			availability.setReason("vakantie");
			availability.setAvailableModule(TeachingModules.AGILE);
			availability.setId(i+1);
			availabilities.add(availability);
		}
		return availabilities;
	}

}
