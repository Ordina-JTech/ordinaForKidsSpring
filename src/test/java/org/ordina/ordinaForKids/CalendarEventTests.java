package org.ordina.ordinaForKids;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.calendarEvent.CalendarEvent;
import org.ordina.ordinaForKids.calendarEvent.CalendarEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrdinaForKidsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarEventTests {
	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	
	private String getRootUrl() {
		return "http://localhost:" + port;
	}
	
	@Test
		public void testCreateUser() {
			// Create new EventDTO to be send to the endpoint
			CalendarEventDTO calendarEventDTO = new CalendarEventDTO();
			calendarEventDTO.setDate(new Date());
			
			// check if the response data contains the new user
			ResponseEntity<CalendarEventDTO> postResponse = 
					restTemplate
					.withBasicAuth("schooluser1", "school")
					.postForEntity(getRootUrl() + "/calendar_events", calendarEventDTO, CalendarEventDTO.class);
			assertNotNull(postResponse);
			assertNotNull(postResponse.getBody());
			System.out.println(postResponse);
		}

}
