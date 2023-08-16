package uk.co.aegon.commons.service;

import javax.jms.Queue;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import uk.co.aegon.commons.dto.EmailDTO;
import uk.co.aegon.commons.dto.EmailEventDTO;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.MIEmailEventDTO;
import uk.co.aegon.commons.dto.PolicyEmailDTO;

@Slf4j
// SPP Added !dev and !init otherwise unit tests will fail
// PJB Removed !dev and !init otherwise services fail
@Profile({"!stubemail && !dbemail"})
@Service("emailServiceSpringMailImpl")
@EnableJms
public class EmailServiceSpringMailImpl implements EmailService{

	@Autowired
	JmsMessagingTemplate template;
	
	@Value("${atos.emailQueueName:PROTECTION_EMAIL_QUEUE_TEST}")
	private String emailQueueName;

	private String emailStorageStrategy = "AMQ";   // "DB"
	
	@Value("${atos.email.mi.queue: PROTECTION_MI_EMAIL_QUEUE_DEFAULT}")
	private String emailMIQueueName;
	  
	@Override
	public void sendEmail(String userId, String uan, String policyNumber, PolicyEmailDTO policyEmailDTO) throws Exception {
		log.trace("sendEmail called for EmailServiceSpringMailImpl");
		if (StringUtil.isNullOrEmpty(uan) || StringUtil.isNullOrEmpty(userId)) {
			log.error("attempt to send email to user without userId or without uan");
		} else {
			log.info("adding email to queue for user: " + userId);
			// SPP removed this is causing an error
			//Queue q = template.getConnectionFactory().createConnection().createSession().createQueue(emailQueueName);
			
			EmailEventDTO event = new EmailEventDTO();
			event.setUan(uan);
			event.setUserId(userId);
			event.setPolicyNumber(policyNumber);
			event.setPolicyEmailDTO(policyEmailDTO);
			
			if (this.emailStorageStrategy.equalsIgnoreCase("AMQ")) {
				Queue q = template.getConnectionFactory().createConnection().createSession().createQueue(emailQueueName);				
				template.convertAndSend(q, event);
			} else {
				String returnStatus = "ERROR";
				try {
			//		returnStatus = this.emailServiceDiscoveryClient.addEmail(emailEventDTO);
				} catch (Exception e) {
					log.error("addEmail failed",e);
					throw e;
				}
				log.trace("sendEmail send status: "+returnStatus);
				
			}
			
		}
	}

	@Override
	public void sendEmail(EmailDTO dto) throws Exception {
		log.error("attempt to send email that did nothing! use EmailService.sendEmail(String,String,String) instead. Email intended for user: " + dto == null ? "no userId!" : dto.getUserId());
	}

	@Override
	public void sendEmail(MIEmailEvent event) {
		log.trace("attempting to send MI Email message to queue: " + emailMIQueueName);

		if (this.emailStorageStrategy.equalsIgnoreCase("AMQ")) {
			template.convertAndSend(emailMIQueueName, event);
		} else {
			String returnStatus = "ERROR";
			try {
				MIEmailEventDTO miEmailEventDTO = new MIEmailEventDTO();
				BeanUtils.copyProperties(miEmailEventDTO, event);
				
		//		returnStatus = this.emailServiceDiscoveryClient.addMIEmail(miEmailEventDTO);
			} catch (Exception e) {
				log.error("addEmail failed",e);
				//throw e;
			}
			log.trace("sendEmail send status: "+returnStatus);
		}
	}
}
