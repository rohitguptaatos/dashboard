package uk.co.aegon.template.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@EnableEurekaClient
@RefreshScope
public class EurekaClientConfiguration {

}
