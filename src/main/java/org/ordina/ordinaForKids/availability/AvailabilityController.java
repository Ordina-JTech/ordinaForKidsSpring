package org.ordina.ordinaForKids.availability;

import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.ordina.ordinaForKids.validation.AvailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.AvailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

public class AvailabilityController {

	@Autowired
	private AvailabilityService availabilityService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/availabilities")
	public List<AvailabilityDTO> getAllAvailabilities() {
		Type listType = new TypeToken<List<AvailabilityDTO>>() {
		}.getType();
		List<Availability> availabilities = getAllAvailabilitiesFromDB();
		List<AvailabilityDTO> availabilityDTOs = modelMapper.map(availabilities, listType);
		return availabilityDTOs;
	}

	private List<Availability> getAllAvailabilitiesFromDB() {
		List<Availability> availabilities;
		try {
			availabilities = availabilityService.getAllAvailabilities();
		} catch (AvailabilityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No availability found");
		}
		return availabilities;
	}

	@PostMapping("/availabilities")
	// To do: requirements definieren in AvailabilityDTO (anders heeft @Valid
	// annotatie geen zin)
	public AvailabilityDTO createAvailability(@Valid @RequestBody AvailabilityDTO availabilityDTO,
			HttpServletRequest request) {
		Availability availability = modelMapper.map(availabilityDTO, Availability.class);
		availability.setLoggedBy(request.getUserPrincipal().getName());
		try {
			availabilityService.createAvailability(availability);
		} catch (AvailabilityOverlapsEventException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		return availabilityDTO;
	}

	@DeleteMapping("/availabilities/{id}")
	public void deleteAvailability(HttpServletRequest request, @PathVariable Long id) {
		Availability availability;
		try {
			availability = availabilityService.getAvailability(id);
			checkAuthorization(availability, request);
			availabilityService.deleteAvailability(availability);
		} catch (AvailabilityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found");
		}

	}

	private void checkAuthorization(Availability availability, HttpServletRequest request) {
		String loggedBy = availability.getLoggedBy();
		if (loggedBy.equals(request.getUserPrincipal().getName())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Only user who logged availability can modify availability.");
		}
	}

}
