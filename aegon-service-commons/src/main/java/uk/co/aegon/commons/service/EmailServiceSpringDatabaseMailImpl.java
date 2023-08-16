package uk.co.aegon.commons.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import io.netty.util.internal.StringUtil;
import uk.co.aegon.commons.dto.EmailDTO;
import uk.co.aegon.commons.dto.EmailEventDTO;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.MIEmailEventDTO;
import uk.co.aegon.commons.dto.PolicyEmailDTO;
import uk.co.aegon.commons.service.client.EmailServiceDiscoveryClient;

@Slf4j
//SPP Added !dev and !init otherwise unit tests will fail
//PJB Removed !dev and !init otherwise services fail
@Profile("dbemail")
@Service("emailServiceSpringDatabaseMailImpl")
public class EmailServiceSpringDatabaseMailImpl implements EmailService {
	
	// Steps to switch from AMQ queues to plain database usage
	// 1. start policytracking and document service with profile "dbemail"
	// 2. change EmailServiceSpringMailImpl  profile form !stubemail to amqemail
	// 3. Turn off JMS Health checks in spring yaml property files
	// 4. Ensure EmailService Discovery client is removed from Tracking service to avoid clash with commons
	
	@Autowired
	EmailServiceDiscoveryClient emailServiceDiscoveryClient;
	
	@Override
	public void sendEmail(String userId, String uan, String policyNumber, PolicyEmailDTO policyEmailDTO) throws Exception {
		log.trace(">>>>>>>>> sendEmail called for EmailServiceSpringDatabaseMailImpl");
		
		// SPPP Just write to DB here insetead and cut out all MQ stuff
		// At least for doc service, tracking and userprofile
		
		if (StringUtil.isNullOrEmpty(uan) || StringUtil.isNullOrEmpty(userId)) {
			log.error("attempt to send email to user without userId or without uan");
		} else {
			log.info("adding email to queue for user: " + userId);
			// SPP removed this is causing an error
			
			EmailEventDTO emailEventDTO = new EmailEventDTO();
			emailEventDTO.setUan(uan);
			emailEventDTO.setUserId(userId);
			emailEventDTO.setPolicyNumber(policyNumber);
			emailEventDTO.setPolicyEmailDTO(policyEmailDTO);
			
			// Instead write to DB table for EmailEvent - could do Discovery call to 
			// EmailService -> addToDatabase
			String returnStatus = "ERROR";
			try {
				returnStatus = this.emailServiceDiscoveryClient.addEmail(emailEventDTO);
			} catch (Exception e) {
				log.error("addEmail failed",e);
				throw e;
			}
			log.trace("sendEmail send status: "+returnStatus);
		}
		log.trace("<<<<<<<<< sendEmail called for EmailServiceSpringDatabaseMailImpl completed ");
	}

	@Override
	public void sendEmail(EmailDTO dto) throws Exception {
		log.error("attempt to send email that did nothing! use EmailService.sendEmail(String,String,String) instead. Email intended for user: " + dto == null ? "no userId!" : dto.getUserId());	
	}

	@Override
	public void sendEmail(MIEmailEvent miEmailEvent) {
		log.trace(">>>>>>>>> sendEmail called for EmailServiceSpringDatabaseMailImpl");		
		// Write to a new MIEmailEvent table instead and use sent field to manage retransmissions
		// Probably call Discovery EmailService method
		// which will write to DB table for MIEmailEvent
		String returnStatus = "ERROR";
		try {
			log.trace("miEmailEvent.subject="+ miEmailEvent.getSubject());
			log.trace("miEmailEvent.content="+ miEmailEvent.getContent());
			log.trace("miEmailEvent.attachmentName="+ miEmailEvent.getAttachmentName());
			log.trace("miEmailEvent.attachmentLength="+ miEmailEvent.getAttachment().length);
			MIEmailEventDTO miEmailEventDTO = new MIEmailEventDTO();
			BeanUtils.copyProperties(miEmailEvent, miEmailEventDTO);
			
			log.trace("miEmailEvent.subject="+ miEmailEventDTO.getSubject());
			log.trace("miEmailEvent.content="+ miEmailEventDTO.getContent());
			log.trace("miEmailEvent.attachmentName="+ miEmailEventDTO.getAttachmentName());
			log.trace("miEmailEvent.attachmentLength="+ miEmailEventDTO.getAttachment().length);			
			returnStatus = this.emailServiceDiscoveryClient.addMIEmail(miEmailEventDTO);
		} catch (Exception e) {
			log.error("addEmail failed",e);
			//throw e;
		}
		log.trace("sendEmail send status: "+returnStatus);
		
		
		// Then need another scheduled cron class for MIEmailEvent processing every 30 mins
		// Set sent flag if successful and scrap all Queues usage
		log.trace("<<<<<<<<< sendEmail called for EmailServiceSpringDatabaseMailImpl completed ");
	}
}