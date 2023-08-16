package uk.co.aegon.template.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerErrorController {
	
	@GetMapping("/501")
	public ResponseEntity<Void> test501(){
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
	}
	
	@GetMapping("/502")
	public ResponseEntity<Void> test502(){
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
	}
	
	@GetMapping("/503")
	public ResponseEntity<Void> test503(){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}
	
	@GetMapping("/504")
	public ResponseEntity<Void> test504(){
		return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
	}
}
