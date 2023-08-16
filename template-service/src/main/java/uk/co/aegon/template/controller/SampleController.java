package uk.co.aegon.template.controller;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.PolicyEmailDTO;
import uk.co.aegon.commons.service.EmailService;
import uk.co.aegon.template.service.client.dto.SimpleDTO;

@RestController
@Slf4j
public class SampleController {
	
	@Autowired
	private EmailService service;
	
	@GetMapping("/log")
	public ResponseEntity<String> log(@RequestParam(name = "test", required = false) String logtext){
		log.trace(logtext == null ? "trace log event" : logtext);
		log.debug(logtext == null ? "debug log event" : logtext);
		log.info(logtext == null ? "info log event" : logtext);
		log.warn(logtext == null ? "warn log event" : logtext);
		log.error(logtext == null ? "error log event" : logtext);
		return ResponseEntity.ok("done");
	}
	
	/**
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/email")
	public ResponseEntity<String> user() throws Exception {
		PolicyEmailDTO policyEmailDTO = new PolicyEmailDTO();
		policyEmailDTO.setClient1Forename("Frank");
		policyEmailDTO.setClient1Surname("Jappa");								
		policyEmailDTO.setClient2Forename("Mary");
		policyEmailDTO.setClient2Surname("Jappa");
		policyEmailDTO.setPolicyRef("L0198366493");
		
		service.sendEmail("GP9RBH","003RGF","L0198366493",policyEmailDTO);
		return ResponseEntity.ok("sent");
	}

	@Value("classpath:sample/VrdExpiredLicenses.xlsx") 
	Resource someAttachment;
	
	@GetMapping("/miemail")
	public ResponseEntity<String> miemail() throws IOException {
		MIEmailEvent event = new MIEmailEvent();
		event.setToAddresses(new String[] {"me@me.com"});
		event.setContent("this is a report");
		event.setFromAddress("fromMe@mine.com");
		event.setSubject("test");
		event.setAttachment(IOUtils.toByteArray(someAttachment.getInputStream()));
		event.setAttachmentName(someAttachment.getFilename());
		service.sendEmail(event);
		return ResponseEntity.ok("sent");
	}
	
	/**
	 * A simple test request to return "test" when called.
	 * @return
	 */
	@GetMapping("/test")
	public ResponseEntity<String> test(){
		return new ResponseEntity<String>("test", HttpStatus.OK);
	}
	
	/**
	 * For demonstrating how to retrieve information about the logged in user from the request.
	 * The principal is injected for you by Spring, unless it doesn't exist, in which case it is null.
	 * @return
	 */
	@GetMapping("/user")
	public ResponseEntity<String> user(@RequestHeader("x-user-name") String principal){
		if (principal == null) {
			return ResponseEntity.ok(null);
		} else {
			return ResponseEntity.ok(principal);
		}
	}
	
	/**
	 * For demonstrating how Hystrix works. Fails with a 50/50 chance
	 * @return
	 */
	@GetMapping("/fails1in2")
	public ResponseEntity<String> fails1in2(){
		Random random = new Random();
		if (random.nextInt(2) == 1) {
			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Foo Not Found");
		}
		return ResponseEntity.ok("success");
	}
	
	/**
	 * For demonstrating how Hystrix works. Delays for 5 seconds with a 50/50 chance
	 * @return
	 */
	@GetMapping("/delays1in2")
	public ResponseEntity<String> delays1in2(){
		Random random = new Random();
		if (random.nextInt(2) == 1) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error("interrupted", e);
			}
			return ResponseEntity.ok("delayed");
		}
		return ResponseEntity.ok("success");
	}
	
	/**
	 * This shows how objects can be used as parameters and return values and they will be automatically
	 * converted to and from JSON.
	 * @param simple
	 * @return
	 */
	@PostMapping("/simple")
	public ResponseEntity<SimpleDTO> simple(@RequestBody SimpleDTO simple){
		simple.setName(simple.getName() + " received!");
		return ResponseEntity.ok(simple);
	}
}
