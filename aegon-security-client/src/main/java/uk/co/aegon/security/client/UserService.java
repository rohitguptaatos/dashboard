package uk.co.aegon.security.client;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

	/**
	 * To retrieve the details for the give user UID(s) from Aegon directory
	 * 
	 * @param userUIDs - array of Users UID
	 * 
	 * @return {@link UserInfoResponse}
	 */
	public UserInfoResponse retrieveUserInfo(String... userUIDs);

	/**
	 * To retrieve user(s) associated with the given email from Aegon directory
	 * 
	 * @param emailId of type {@link java.lang.String} 
	 * @return  {@link UserInfoResponse}
	 */
	public UserInfoResponse retrieveUserInfoByEmail(String emailId);

	/**
	 * To retrieve user(s) associated with the given Firm Ref Number from Aegon directory
	 * 
	 * @param firmRefNumber of type {@link java.lang.String} 
	 * @return   {@link UserInfoResponse}
	 */
	public UserInfoResponse retrieveUserInfoByFrnNumber(String firmRefNumber);

	public UserInfoResponse retrieveUnipassUserInfo(String email);

	
}