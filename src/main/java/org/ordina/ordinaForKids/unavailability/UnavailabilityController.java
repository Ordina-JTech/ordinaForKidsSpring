package org.ordina.ordinaForKids.unavailability;

import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.ordina.ordinaForKids.validation.UnavailabilityNotFoundException;
import org.ordina.ordinaForKids.validation.UnavailabilityOverlapsEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

public class UnavailabilityController {

	@Autowired
	private UnavailabilityService unavailabilityService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/unavailabilities")
	public List<UnavailabilityDTO> getAllUnavailabilities() {
		Type listType = new TypeToken<List<UnavailabilityDTO>>() {
		}.getType();
		List<UnavailabilityDTO> unavailabilityDTOs = modelMapper.map(unavailabilityService.getAllUnavailabilities(),
				listType);

		return unavailabilityDTOs;
	}

	
	@PostMapping("/unavailabilities")
	// To do: requirements definieren in UnavailabilityDTO (anders heeft @Valid
	// annotatie geen zin)
	// Tim: wat is eigenlijk de reden dat de DTO weer teruggegeven wordt?
	public UnavailabilityDTO createUnavailability(@Valid @RequestBody UnavailabilityDTO unavailabilityDTO,
			HttpServletRequest request) {
		Unavailability unavailability = modelMapper.map(unavailabilityDTO, Unavailability.class);
		unavailability.setLoggedBy(request.getUserPrincipal().getName());
		try {
			unavailabilityService.createUnavailability(unavailability);
		} catch (UnavailabilityOverlapsEventException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		return unavailabilityDTO;
	}

	@DeleteMapping("/unavailabilities/{id}")
	public void deleteUnavailability(HttpServletRequest request, @PathVariable Long id) {
		Unavailability unavailability;
		try {
			unavailability = unavailabilityService.getUnavailability(id);
		} catch (UnavailabilityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailability not found");
		}
		checkAuthorization(unavailability, request);
		unavailabilityService.deleteUnavailability(unavailability);

	}

	private void checkAuthorization(Unavailability unavailability, HttpServletRequest request) {
		String loggedBy = unavailability.getLoggedBy();
		if (loggedBy.equals(request.getUserPrincipal().getName())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Only user who logged unavailability can remove unavailability.");
		}
	}

}
