package uk.co.aegon.commons.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.co.aegon.commons.dto.EmailDTO;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.PolicyEmailDTO;

@Slf4j
@Service("emailServiceStubImpl")
@Profile("stubemail")
public class EmailServiceStubImpl implements EmailService {
	
	@Override
	public void sendEmail(String userId, String uan, String policyNumber, PolicyEmailDTO policyEmailDTO) throws Exception {
		log.trace("send email to stub email service for user: " + userId);
	}

	@Override
	public void sendEmail(EmailDTO dto) throws Exception {
		log.trace("attempt to send email that did nothing! use EmailService.sendEmail(String,String,String) instead. Email intended for user: " + dto == null ? "no userId!" : dto.getUserId());
	}

	@Override
	public void sendEmail(MIEmailEvent event) {
		log.trace("sending mi email event");
	}
}
