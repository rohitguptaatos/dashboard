package uk.co.aegon.commons.dto;

import lombok.Data;

@Data
public class PolicyEmailDTO implements java.io.Serializable {
	
	private static final long serialVersionUID = 3119203294490071927L;
	
	private String policyRef;
	private String client1Forename;
	private String client2Forename;
	private String client1Surname;
	private String client2Surname;
	
}
