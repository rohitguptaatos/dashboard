package uk.co.aegon.commons.service;

import uk.co.aegon.commons.dto.EmailDTO;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.PolicyEmailDTO;

public interface EmailService {

    /**
     * Sends a message to the email queue to notify users that a new 
     * email is availble for the given user and uan.
     * @param userId
     * @param uan
     * @throws Exception
     */
    public void sendEmail(String userId, String uan, String policyNumber, PolicyEmailDTO policyEmailDTO) throws Exception;
    
    public void sendEmail(MIEmailEvent event);
    /**
     * @deprecated use {@link EmailService#sendEmail(String, String, String)} instead
     */
    public void sendEmail(EmailDTO dto) throws Exception;
    
    public final static String EMAIL_QUEUE = "EMAIL_QUEUE";
    
}