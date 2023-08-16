package uk.co.aegon.template.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.co.aegon.template.service.client.dto.Status;

@Component
@Profile("!dev")
public class PolicyTrackingDiscoveryClient {

	/**
	 * The restTemplate from the RibbonRestTemplateConfiguration is autowired here
	 * and is setup to use Hystrix by default
	 */
	@Autowired
	private RestTemplate restTemplate;
	
	public Status getStatus() {
		// note here that we do not reference the 
		ResponseEntity<Status> rest = restTemplate.exchange(
				"http://template/fails1in2", HttpMethod.GET, null, Status.class);
		return rest.getBody();
	}
	
}
