package org.ordina.ordinaForKids.unavailability;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UnavailabilityServiceImplTest {

	@Autowired
	UnavailabilityServiceImpl unavailabilityService;
	
	@Test
	public void testCreateUnavailability() throws UnavailabilityOverlapsEventException {
		Unavailability u = UnavailabilityTestHelper.createUnavailability();
		unavailabilityService.createUnavailability(u);
	}	

	@Test
	public void testDeleteUnavailability() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUnavailability() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllUnavailabilities() {
		fail("Not yet implemented");
	}

}
