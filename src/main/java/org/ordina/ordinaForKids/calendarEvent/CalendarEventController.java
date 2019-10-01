package org.ordina.ordinaForKids.calendarEvent;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
	private CalendarEventRepository calendarEventRepository;

	private ModelMapper modelMapper = new ModelMapper();

	@Value("${ofk.events.maxperday}")
	private long maxEventsPerDay;

	/**
	 * Returns the calendar events for a specific username
	 * @param request
	 * @return
	 */
	@GetMapping("/calendar_events/{username}")
	public List<CalendarEventDTO> getAllUserEvents(HttpServletRequest request) {
		
		Type listType = new TypeToken<List<CalendarEventDTO>>() {
		}.getType();
		List<CalendarEventDTO> calendarEventDTOs = modelMapper.map(calendarEventRepository.findAllByOwner(request.getUserPrincipal().getName()), listType);
		return calendarEventDTOs;
	}
	
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
		List<CalendarEventDTO> calendarEventDTOs = modelMapper.map(calendarEventRepository.findAll(), listType);
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
				
		// check if the max number of events is exceeded based on application.properties => ofk.events.maxperday
		List<CalendarEvent> calendarEvents = calendarEventRepository.findAllByDate(calendarEvent.getDate());
		
		if(calendarEvents.size() >= maxEventsPerDay) {
			throw new ResponseStatusException(
			          HttpStatus.CONFLICT, "Maximum number of events per day '" + maxEventsPerDay + "' has already been reached");
		}
		
		calendarEvent.setOwner(request.getUserPrincipal().getName());
		
		for(CalendarEvent existingEvent : calendarEvents) {
			if(existingEvent.getOwner().equals(calendarEvent.getOwner())) {
				throw new ResponseStatusException(
				          HttpStatus.CONFLICT, "Can only book 1 event per day per user");
			}
		}
		
		
		calendarEvent = calendarEventRepository.save(calendarEvent);
		return modelMapper.map(calendarEvent, CalendarEventDTO.class);
	}
	
	@DeleteMapping("/calendar_events/{id}")
	public void deleteEvent(HttpServletRequest request, @PathVariable Long id)
	{
		
		Optional<CalendarEvent> calendarEvent = calendarEventRepository.findById(id);
		if(calendarEvent.isEmpty()) {
			throw new ResponseStatusException(
			          HttpStatus.NOT_FOUND, "Event not found");
		}
		if(!calendarEvent.get().getOwner().equals(request.getUserPrincipal().getName())) {
			
			throw new ResponseStatusException(
			          HttpStatus.FORBIDDEN, "Only event owner can remove event");
		}
		
		calendarEventRepository.delete(calendarEvent.get());

	}
}
