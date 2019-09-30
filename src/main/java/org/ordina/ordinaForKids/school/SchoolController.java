package org.ordina.ordinaForKids.school;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SchoolController {
	@Autowired
	private SchoolRepository schoolRepository;
	
	@GetMapping("/schools")
	public List<School> getSchools()
	{
		return schoolRepository.findAll();
	}
	
	
}

