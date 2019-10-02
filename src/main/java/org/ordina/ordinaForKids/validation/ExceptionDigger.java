package org.ordina.ordinaForKids.validation;

/**
 * Simple class to iterate over nested exceptions and retrieve a reporatable one that makes sense to the user
 * @author tim
 *
 */
public class ExceptionDigger {

	@SuppressWarnings("unchecked")
	public <T> T digUntilException(Throwable throwable, Class<T> clazz) {
		
		if(throwable.getClass() == clazz) { return (T) throwable; }
		if(throwable.getCause() != null) {
			return digUntilException(throwable.getCause(), clazz);
		}
		return null;
		
	}
	
}
