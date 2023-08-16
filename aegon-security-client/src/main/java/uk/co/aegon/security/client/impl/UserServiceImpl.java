package uk.co.aegon.security.client.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import swagger.client.api.AipTriggerApiApi;
import swagger.client.model.SearchFRNRequest;
import swagger.client.model.SearchUserResponse;
import swagger.client.model.SearchbyEmailIdRequest;
import swagger.client.model.SearchbyUserIdRequest;
import uk.co.aegon.security.client.UserInfoDTO;
import uk.co.aegon.security.client.UserInfoResponse;
import uk.co.aegon.security.client.UserRole;
import uk.co.aegon.security.client.UserService;

@Slf4j
@Profile("!ldapstub")
@Configuration
public class UserServiceImpl implements UserService {

	private static final String USER_RETRIEVE_ERROR = "Unable to retreive user details";
	private static final String LDAP_STATUS_ACTIVE = "EXTRANET:Authorised";
	private static final String LDAP_STATUS_ACTIVE_DC = "SE_XNET:Enabled";

	@Autowired
	private AipTriggerApiApi restClient;

	@SuppressWarnings("serial")
	public UserInfoResponse retrieveUserInfo(String... userUIDs) {
		log.debug("retrieveUserInfo >> \n");

		if (userUIDs == null || userUIDs.length < 1) {
			log.debug("Input is null or empty");
			return new UserInfoResponse() {
				{
					setError(USER_RETRIEVE_ERROR);
				}
			};
		}
		SearchbyUserIdRequest request = new SearchbyUserIdRequest();
		UserInfoResponse response = null;
		log.debug("Retrieving user info for the following list of id");
		if (userUIDs.length <= 99) {
			log.debug("Request length is lesser than 100 >> " + userUIDs.length);

			for (String userUID : userUIDs) {
				log.debug(userUID);
				request.addUserUIDsItem(userUID);
			}
			JWTUtility.checkAndSetAPIKey(restClient);
			SearchUserResponse restResponse = restClient.searchByUserId(request);
			response = transformResponse(restResponse);
			log.debug("retrieveUserInfo << \n" + response);
			return response;
		}
		log.debug("Prod Fix kicks in >>");
//		Extract 100 users per fetch		
		List<String> userUIDList = Arrays.asList(userUIDs);
		int totalSize = userUIDList.size();
		log.debug("Request lenght is greater than 100 >> " + totalSize);
		int toIndex = 95;
		int startIndex = 0;
		response = new UserInfoResponse();
		while (toIndex < totalSize) {
			List<String> subIndex = userUIDList.subList(startIndex, toIndex);
			startIndex += 96;
			toIndex = (toIndex + 95 >= totalSize) ? totalSize : toIndex + 95;
			if (startIndex >= toIndex) {
				break;
			}
			request = new SearchbyUserIdRequest();
			request.setUserUIDs(subIndex);
			JWTUtility.checkAndSetAPIKey(restClient);
			SearchUserResponse restResponse = restClient.searchByUserId(request);
//			Concatinate the results
			transformResponse(restResponse, response);
		}
		log.debug("retrieveUserInfo << \n" + response);
		return response;
	}

	/**
	 * Utility method to transform the REST response returned from Aegon mulesoft
	 * into UserInfoResponse object
	 * 
	 * @param restResponse
	 * @return
	 */
	private UserInfoResponse transformResponse(SearchUserResponse restResponse) {
		log.debug("transformResponse >> " + restResponse);
		Assert.notNull(restResponse, USER_RETRIEVE_ERROR);
		UserInfoResponse response = new UserInfoResponse();
		if (restResponse.getErrorDetails() != null && !restResponse.getErrorDetails().isEmpty()) {
			log.error("transformResponse >> Error received from Aegon LDAP");
			restResponse.getErrorDetails().stream()
					.forEach(s -> log.error(s.getErrorCode() + "\t" + s.getErrorMessage()));
		}
		if (restResponse.getUserDetails() != null) {
			transformResponse(restResponse, response);
		}
		if (response.getUsers() == null || response.getUsers().isEmpty()) {
			log.debug("No user has been found");
			response.setError(USER_RETRIEVE_ERROR);
		}
		return response;
	}

	private void transformResponse(SearchUserResponse restResponse, UserInfoResponse response) {
		log.debug("transformResponse >> " + restResponse);
		if (restResponse.getErrorDetails() != null && !restResponse.getErrorDetails().isEmpty()) {
			log.error("transformResponse >> Error received from Aegon LDAP");
//			Swallow the Error here
		}
		if (restResponse.getUserDetails() == null) {
			return;
		}
		// Filter out non active users
		restResponse.getUserDetails().stream()
				.filter(p -> p.getUserStatus() != null 
//					PROD FIX - PE3131
						&& (p.getUserStatus().contains(LDAP_STATUS_ACTIVE)
								|| p.getUserStatus().contains(LDAP_STATUS_ACTIVE_DC)))
//						&& (LDAP_STATUS_ACTIVE.equals(p.getUserStatus().trim())
//						|| LDAP_STATUS_ACTIVE_DC.equals(p.getUserStatus().trim()))
//					END OF PROD FIX - PE3131 
						
				.forEach(rawuserInfo -> {
					UserInfoDTO responseDTO = new UserInfoDTO();
					BeanUtils.copyProperties(rawuserInfo, responseDTO);
					responseDTO.setFRN(rawuserInfo.getFrn());
					if (rawuserInfo.getIsGlobalUser().equalsIgnoreCase("Y")) {
						responseDTO.setGlobal(Boolean.TRUE);
					} else {
						responseDTO.setGlobal(Boolean.FALSE);
					}
					responseDTO.setUserRole(UserRole.fromRole(rawuserInfo.getUserType()));

//						For DC users the userUID = certID appended with I at the end which causes issues because
//						on SAML we get the user UID withouht I appended at the end, so we will just user certID everywhere for DC users 			
					if (!StringUtils.isEmpty(rawuserInfo.getCertID())) {
//							The below is no longer requred as Aegon are passing the User UID (i.e. with I at the end, so the below is no longer needed
//							responseDTO.setUserUID(rawuserInfo.getCertID());
						responseDTO.setUnipassUser(true);
					} else {
						responseDTO.setUnipassUser(false);
					}
					response.addUser(responseDTO);
				});
	}

	public UserInfoResponse retrieveUserInfoByEmail(String emailId) {
		log.debug("retrieveUserInfoByEmail >> " + emailId);
		SearchbyEmailIdRequest request = new SearchbyEmailIdRequest();
		request.addEmailIDsItem(emailId);
		JWTUtility.checkAndSetAPIKey(restClient);
		SearchUserResponse restResponse = restClient.searchByEmail(request);
		UserInfoResponse response = transformResponse(restResponse);
		log.debug("retrieveUserInfoByEmail << " + response);
		return response;
	}

	public UserInfoResponse retrieveUserInfoByFrnNumber(String firmRefNumber) {
		log.debug("retrieveUserInfoByFrnNumber >> " + firmRefNumber);
		SearchFRNRequest request = new SearchFRNRequest();
		request.setFrn(firmRefNumber);
		JWTUtility.checkAndSetAPIKey(restClient);
		SearchUserResponse restResponse = restClient.searchbyFRN(request);
		UserInfoResponse response = transformResponse(restResponse);
		if (restResponse.getUserDetails() != null) {
			log.info("retrieveUserInfoByFrnNumber->ldap raw result list size=" + restResponse.getUserDetails().size());
			// if (restResponse.getUserDetails().size() == 500) {
			if (restResponse.getUserDetails().size() >= 500) {
				response.setDisplayLdapWarningFlag(Boolean.TRUE);
			}
		}
		log.debug("retrieveUserInfoByEmail << " + response);
		return response;

	}

	public UserInfoResponse retrieveUnipassUserInfo(String email) {
		throw new RuntimeException("Operation Not Implemented");
	}

}
