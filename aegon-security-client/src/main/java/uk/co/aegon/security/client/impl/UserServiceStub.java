package uk.co.aegon.security.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.co.aegon.security.client.UserInfoDTO;
import uk.co.aegon.security.client.UserInfoResponse;
import uk.co.aegon.security.client.UserRole;
import uk.co.aegon.security.client.UserService;

/**
 * Stub implementation for {@link UserService}
 * 
 * @author Praveen
 *
 */
@Slf4j
// @Profile({"!prod && !preprod"})
@Profile("ldapstub")
@Configuration
public class UserServiceStub implements UserService {

	private static final Map<String, UserInfoDTO> users = new HashMap<>();

	private boolean isLoaded;
	


	@Override
	public UserInfoResponse retrieveUserInfo(String... userUIDs) {

		if (!isLoaded) {
			log.debug("LOADING STUB FILE");
			InputStream stubFile;
			try {
				stubFile = new ClassPathResource("Stub.csv").getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stubFile));
					reader.lines().forEach(line -> {
						UserInfoDTO user = extractUserInfoFromCSV(line);
						users.put(user.getUserUID(), user);
						//log.debug("LOADED USER: " + user);
						
					});
					reader.close();
					isLoaded = true;
			} catch (IOException e) {
				log.error("Unable to load Stub >> " + e.getMessage());
				e.printStackTrace();
			}
		}

		//log.trace("retrieveUserInfo >> " + userUIDs);
		UserInfoResponse response = new UserInfoResponse();
		for (String uid : userUIDs) {
			if (users.containsKey(uid.toLowerCase()) || users.containsKey(uid.toUpperCase())) {
				response.addUser(users.get(uid.toUpperCase()));
			}
		}
		if (response.getUsers() == null) {
			response.setError("Unable to fetch information for the User");
		}

		log.trace("retrieveUserInfo << " + userUIDs);
		return response;
	}

    public UserInfoResponse retrieveUnipassUserInfo(String email) {
		if (!isLoaded) {
			log.debug("LOADING STUB FILE");
			InputStream stubFile;
			try {
				stubFile = new ClassPathResource("Stub.csv").getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stubFile));
					reader.lines().forEach(line -> {
						UserInfoDTO user = extractUserInfoFromCSV(line);
						users.put(user.getUserUID(), user);
						//log.trace("LOADED USER: " + user);
						
					});
					reader.close();
					isLoaded = true;
			} catch (IOException e) {
				log.error("Unable to load Stub >> " + e.getMessage());
				e.printStackTrace();
			}
		}

		// log.trace("retrieveUserInfo >> " + email);
		UserInfoResponse response = new UserInfoResponse();
		for (Map.Entry<String,UserInfoDTO> userMap : users.entrySet()) {
			UserInfoDTO userInfo = userMap.getValue();
			if (userInfo.getUserUID().length() > 6) {
				response.addUser(userInfo);
			}
		}
		if (response.getUsers() == null) {
			response.setError("Unable to fetch information for the inout email address");
		}

		log.trace("retrieveUserInfo << " + email);
		return response;
    	
    }	
	
    public UserInfoResponse retrieveUserInfoByEmail(String email) {
		if (!isLoaded) {
			log.debug("LOADING STUB FILE");
			InputStream stubFile;
			try {
				stubFile = new ClassPathResource("Stub.csv").getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stubFile));
					reader.lines().forEach(line -> {
						UserInfoDTO user = extractUserInfoFromCSV(line);
						users.put(user.getUserUID(), user);
						//log.trace("LOADED USER: " + user);
						
					});
					reader.close();
					isLoaded = true;
			} catch (IOException e) {
				log.error("Unable to load Stub >> " + e.getMessage());
				e.printStackTrace();
			}
		}

		//log.error("retrieveUserInfo >> " + email);
		UserInfoResponse response = new UserInfoResponse();
		for (Map.Entry<String,UserInfoDTO> userMap : users.entrySet()) {
			UserInfoDTO userInfo = userMap.getValue();
			if (userInfo.getEmail().equals(email)) {
				response.addUser(userInfo);
			}
		}
		if (response.getUsers() == null) {
			response.setError("Unable to fetch information for the inout email address");
		}

		log.trace("retrieveUserInfo << " + email);
		return response;
    	
    }
    
    public UserInfoResponse retrieveUserInfoByFrnNumber(String frnNumber) {
		if (!isLoaded) {
			log.debug("LOADING STUB FILE");
			InputStream stubFile;
			try {
				stubFile = new ClassPathResource("Stub.csv").getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stubFile));
					reader.lines().forEach(line -> {
						UserInfoDTO user = extractUserInfoFromCSV(line);
						users.put(user.getUserUID(), user);
						//log.error("LOADED USER: " + user);
						
					});
					reader.close();
					isLoaded = true;
			} catch (IOException e) {
				log.error("Unable to load Stub >> " + e.getMessage());
				e.printStackTrace();
			}
		}

		//log.trace("retrieveUserInfo >> " + frnNumber);
		UserInfoResponse response = new UserInfoResponse();
		for (Map.Entry<String,UserInfoDTO> userMap : users.entrySet()) {
			UserInfoDTO userInfo = userMap.getValue();
			if (userInfo.getFRN().equals(frnNumber)) {
				response.addUser(userInfo);
			}
		}
		if (response.getUsers() == null) {
			response.setError("Unable to fetch information for the inout email address");
		}

		log.trace("retrieveUserInfo << " + frnNumber);
		return response;
    	
    	
    }
	
	private static UserInfoDTO extractUserInfoFromCSV(String csvRow) {
		//log.trace("extractUserInfoFromCSV >> " + csvRow);
		String[] csvUser = csvRow.split(",");
		UserInfoDTO user = new UserInfoDTO();
		user.setUserUID(csvUser[0]);
		user.setTitle(csvUser[1]);
		user.setForename(csvUser[2]);
		user.setSurname(csvUser[3]);
		user.setEmail(csvUser[4]);
		user.setUserStatus(csvUser[5]);
		user.setUserAlias(csvUser[6]);
		user.setFRN(csvUser[7]);
		user.setUserRole(UserRole.fromRole(csvUser[8]));
		user.setGlobal(Boolean.valueOf(csvUser[9]));
		user.setMarketingAdminFlag(Boolean.FALSE);
		user.setPricingAdminFlag(Boolean.FALSE);
		String agency = csvUser.length > 10 ? csvUser[10] : null;
		if (StringUtils.isEmpty(agency))
			return user;
		if (agency.contains("|")) {
			user.setAgencyList(Arrays.asList(agency.split("|")));
		} else {
			user.setAgencyList(new ArrayList<String>());
			user.getAgencyList().add(agency);
		}
		//log.trace("extractUserInfoFromCSV <<");
		return user;
	}

}
