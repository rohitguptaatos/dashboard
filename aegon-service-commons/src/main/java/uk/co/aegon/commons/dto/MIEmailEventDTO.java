package uk.co.aegon.commons.dto;

import java.io.Serializable;

import org.springframework.core.io.InputStreamSource;

import lombok.Data;

@Data
public class MIEmailEventDTO implements Serializable{
	private static final long serialVersionUID = -2002378540004713712L;
	private Long id;
	private String fromAddress;
	private String subject;
	private String[] toAddresses;
	private String[] carbonCopy;
	private String[] blindCopy;
	private byte[] attachment;
	private String attachmentName;
	private String content;
}
