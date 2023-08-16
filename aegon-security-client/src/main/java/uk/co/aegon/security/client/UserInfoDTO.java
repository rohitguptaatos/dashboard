package uk.co.aegon.security.client;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * User Model Object
 */
@Data
public class UserInfoDTO implements Serializable {
    
	private static final long serialVersionUID = -7007907580982486125L;

	/** forename */
	private String forename;

	/** surname */
	private String surname;

	/** email */
	private String email;

	/** aegonuserId */
	private String userUID; // unique user ID

	/** userStatus */
	private String userStatus;

	/** userStatus */
    private String userAlias; // user id used to login
    
    private String title;
    
    private String FRN;
    
    private List<String> agencyList;  

	private UserRole userRole;

	private Boolean global;
	
	private Boolean pricingAdminFlag;
	
	private Boolean marketingAdminFlag;

	private String postCode;

//	set to True for Digital Certificate aka Unipass users
	private Boolean unipassUser;
    
}