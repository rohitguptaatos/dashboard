package uk.co.aegon.template;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class TemplateMicroserviceApplicationTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Value("{atos.jwt}")
	private String jwt;

	//@Test
	public void contexLoads() throws Exception {
		this.mockMvc
			.perform(get("/test")
			.header("Authorization", "Bearer " + jwt))
			.andDo(print())
			.andExpect(
				status().isOk())
			.andExpect(
				content().string(containsString("test")));
	}

}
