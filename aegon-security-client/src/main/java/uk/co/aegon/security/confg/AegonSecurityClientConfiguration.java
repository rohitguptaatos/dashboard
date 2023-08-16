package uk.co.aegon.security.confg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import swagger.client.api.AipTriggerApiApi;
import swagger.client.trigger.ApiClient;

@Configuration
public class AegonSecurityClientConfiguration {

	@Bean
	public static AipTriggerApiApi apiTriggerBean(@Value("${aegon.mulesoft.url}") String basePath,
			@Value("${asec.swagger.debug:false}") String debug) throws Exception {
		AipTriggerApiApi trigger = new AipTriggerApiApi() {
			{
				ApiClient apiClient = new ApiClient(buildRestTemplate());
				super.setApiClient(apiClient);
			}
		};
		trigger.getApiClient().setBasePath(basePath);
		trigger.getApiClient().setDebugging(Boolean.valueOf(debug));
		return trigger;
	}

	private static RestTemplate buildRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().stream().forEach(messageConverter -> {
			if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
				((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper()
						.setSerializationInclusion(Include.NON_NULL);
			}
		});
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		return restTemplate;
	}
}
