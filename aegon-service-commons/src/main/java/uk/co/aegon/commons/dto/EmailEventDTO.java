package uk.co.aegon.commons.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class EmailEventDTO implements Serializable{

	private static final long serialVersionUID = 3119203294490071926L;

	private Long id;
	
	private String userId;
	
	private String uan;
	
	private String policyNumber;
	
	private Date createdAt;
	
	private PolicyEmailDTO policyEmailDTO;
	
}
