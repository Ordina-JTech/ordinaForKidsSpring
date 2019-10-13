package org.ordina.ordinaForKids;


import org.junit.Test;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventDTO;
import org.ordina.ordinaForKids.school.School;
import org.ordina.ordinaForKids.user.User;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class AccessorTests {
	private static final Validator ACCESSOR_VALIDATOR = ValidatorBuilder.create()
            .with(new GetterTester())
            .with(new SetterTester())
            .build();

	public static void validateAccessors(final Class<?> clazz) {
	ACCESSOR_VALIDATOR.validate(PojoClassFactory.getPojoClass(clazz));
	}
	
	@Test
	public void testCalendarEvent() {
		validateAccessors(CalendarEvent.class);
	}
	
	@Test
	public void testCalendarEventDTO() {
		validateAccessors(CalendarEventDTO.class);
	}
	
	@Test
	public void testSchool() {
		validateAccessors(School.class);
	}
	
	@Test
	public void testUser() {
		validateAccessors(User.class);
	}
}
