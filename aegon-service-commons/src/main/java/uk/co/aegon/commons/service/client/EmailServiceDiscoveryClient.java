package uk.co.aegon.commons.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import uk.co.aegon.commons.dto.EmailEventDTO;
import uk.co.aegon.commons.dto.MIEmailEventDTO;


@Component("emailServiceDiscoveryClient")
@EnableConfigurationProperties
@ConfigurationProperties("application")
@Slf4j
//@Profile("!dev")
public class EmailServiceDiscoveryClient {

	/**
	 * The restTemplate from the RibbonRestTemplateConfiguration is autowired here
	 * and is setup to use Hystrix by default
	 */
	
	@Value("${atos.email.service.url:http://email}")
	private String emailServiceURL; 
	
	//@Autowired
	//WebClient.Builder webClientBuilder;
	    
    public String addEmail(EmailEventDTO emailEventDTO) throws Exception {
		log.info("EmailServiceDiscoveryClient->emailServiceURL ="+this.emailServiceURL);
		WebClient webClient = this.getWebClientPolicyTrackingService();
		try {
			Mono<ClientResponse> response = webClient.post()
			        .uri("/emailadd")
			        .body(Mono.just(emailEventDTO), EmailEventDTO.class)
			        .exchange();
			
			HttpStatus statusCode = response.block().statusCode();
			log.trace("Response.status="+statusCode.value());
			
			if (statusCode == HttpStatus.OK) {
				return "OK";
			} else {
				return "ERROR";
				// 417 - EXPECTATION FAILED :  FOR AN APPLICATION ERROR
				//log.trace("Response.status="+statusCode.value());
				//ApiError apiError = response.flatMap(res -> res.bodyToMono(ApiError.class)).block();
				//log.trace("ApiError DebugMessage="+apiError.getDebugMessage());
				//log.trace("ApiError Message="+apiError.getMessage());				
			}			
		} catch (Exception e) {
			throw e;
		}		
	}    
    
    public String addMIEmail(MIEmailEventDTO miemailEventDTO) throws Exception {
		log.info("emailServiceURL ="+this.emailServiceURL);
		WebClient webClient = this.getWebClientPolicyTrackingService();
		try {
			Mono<ClientResponse> response = webClient.post()
			        .uri("/miemailadd")
			        .body(Mono.just(miemailEventDTO), MIEmailEventDTO.class)
			        .exchange();
			
			HttpStatus statusCode = response.block().statusCode();
			log.trace("Response.status="+statusCode.value());
			if (statusCode == HttpStatus.OK) {
				return "OK";
			} else {
				return "ERROR";
				// 417 - EXPECTATION FAILED :  FOR AN APPLICATION ERROR
				//log.trace("Response.status="+statusCode.value());
				//ApiError apiError = response.flatMap(res -> res.bodyToMono(ApiError.class)).block();
				//log.trace("ApiError DebugMessage="+apiError.getDebugMessage());
				//log.trace("ApiError Message="+apiError.getMessage());				
			}			
		} catch (Exception e) {
			throw e;
		}		
	}    
        
	private WebClient getWebClientPolicyTrackingService()
	{
	    HttpClient httpClient = HttpClient.create()
	            .tcpConfiguration(client ->
	                    client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
	                    .doOnConnected(conn -> conn
	                            .addHandlerLast(new ReadTimeoutHandler(10))
	                            .addHandlerLast(new WriteTimeoutHandler(10))));
	     
	    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);     
	 
	    return WebClient.builder()
	            .baseUrl(emailServiceURL)
	            .clientConnector(connector)
	            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	            .build();
	}
	
}

