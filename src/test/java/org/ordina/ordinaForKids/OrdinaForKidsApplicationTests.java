package org.ordina.ordinaForKids;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ordina.ordinaForKids.config.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrdinaForKidsApplicationTests {

	protected MockMvc mvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before()
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(new CorsFilter()).build();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void testCORSHeaders() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = mvc.perform(MockMvcRequestBuilders.get("/user")).andReturn()
				.getResponse();
		assertTrue(mockHttpServletResponse.containsHeader("Access-Control-Allow-Origin"));
		assertTrue(mockHttpServletResponse.containsHeader("Access-Control-Allow-Methods"));
		assertTrue(mockHttpServletResponse.containsHeader("Access-Control-Allow-Headers"));

	}
}
