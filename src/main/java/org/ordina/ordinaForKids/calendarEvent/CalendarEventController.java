package org.ordina.ordinaForKids.calendarEvent;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.ordina.ordinaForKids.validation.CalendarEventAccessViolationException;
import org.ordina.ordinaForKids.validation.CalendarEventNotFoundException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayPerOwnerReachedException;
import org.ordina.ordinaForKids.validation.MaximumNumberOfEventsPerDayReachedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
/**
 * Basic RestController for CalendarEvent
 * @author Tim Misset
 *
 */
public class CalendarEventController {

	@Autowired
	private CalendarEventService calendarEventService;
	
	private ModelMapper modelMapper = new ModelMapper();
	
	/**
	 * Returns the calendar events
	 * @param request
	 * @return
	 */
	@GetMapping("/calendar_events")
	public List<CalendarEventDTO> getAllEvents(HttpServletRequest request)
	{
		Type listType = new TypeToken<List<CalendarEventDTO>>() {
		}.getType();
		List<CalendarEventDTO> calendarEventDTOs = modelMapper.map(calendarEventService.getCalendarEvents(), listType);
		return calendarEventDTOs;
	}

	/**
	 * Creates new calendar event
	 * @param request
	 * @param calendarEventDTO
	 * @return
	 */
	@PostMapping("/calendar_events")
	public CalendarEventDTO createEvent(HttpServletRequest request,
			@Valid @RequestBody CalendarEventDTO calendarEventDTO) {
		// parse DTO to entity
		
		CalendarEvent calendarEvent = modelMapper.map(calendarEventDTO, CalendarEvent.class);
		calendarEvent.setOwner(request.getUserPrincipal().getName());
		try {
			calendarEventService.createCalendarEvent(calendarEvent);
		} catch (MaximumNumberOfEventsPerDayReachedException maximumNumberOfEventsPerDayReachedException) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, maximumNumberOfEventsPerDayReachedException.getMessage());
		} catch (MaximumNumberOfEventsPerDayPerOwnerReachedException maximumNumberOfEventsPerDayPerOwnerReachedException) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, maximumNumberOfEventsPerDayPerOwnerReachedException.getMessage());
		}		
		
		return calendarEventDTO;
	}
	
	@DeleteMapping("/calendar_events/{id}")
	public void deleteEvent(HttpServletRequest request, @PathVariable Long id)
	{
		
		Optional<CalendarEvent> calendarEvent = calendarEventService.getCalendarEvent(id);
		
		// Only event owner can remove event is something which is linked to the processing of the Rest call
		// and not necessarily a low-level limiting that should be placed on the service.
		if(!calendarEvent.get().getOwner().equals(request.getUserPrincipal().getName())) {
			
			throw new ResponseStatusException(
			          HttpStatus.FORBIDDEN, "Only event owner can remove event");
		}
			
		
		try {
			calendarEventService.deleteCalendarEvent(id);
		} catch (CalendarEventNotFoundException calendarEventNotFoundException) {
			throw new ResponseStatusException(
			          HttpStatus.FORBIDDEN, calendarEventNotFoundException.getMessage());
		}
	}
}
