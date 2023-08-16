package uk.co.aegon.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import uk.co.aegon.commons.EnableEmailService;
import uk.co.aegon.security.client.EnableAtosLDAPService;
import uk.co.aegon.security.client.UserService;

@EnableAtosLDAPService
@EnableEmailService
@SpringBootApplication
public class TemplateMicroserviceApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(TemplateMicroserviceApplication.class, args);
	}

	@Autowired
	private UserService service;
	
	@Override
	public void run(String... args) throws Exception {
		
	}
}
