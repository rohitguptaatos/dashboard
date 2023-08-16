package uk.co.aegon.security.client.impl;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import swagger.client.api.AipTriggerApiApi;

@Slf4j
class JWTUtility {

	private static Calendar expiry;
	
	/**
	 * Checks the given client trigger to see if bearer token is set and if set will check if the token has expired
	 * When token is not available or expired a new token will be generated and set into trigger client
	 * 
	 * @param clientAPI
	 * @throws Exception
	 */
	static void checkAndSetAPIKey(AipTriggerApiApi clientAPI) {
		log.debug("checkAndSetAPIKey >> ");
		if (expiry == null || Calendar.getInstance().after(expiry)) {
			log.debug("checkAndSetAPIKey >> Token is null or expired");
			try {
				clientAPI.getApiClient().setApiKey("Bearer " + newToken());
			} catch (Exception e) {
				log.error("checkAndSetAPIKey >>", e);
				throw new RuntimeException("checkAndSetAPIKey", e);
			}
		}
		log.trace("checkAndSetAPIKey > expiry : " + expiry);
		log.trace("checkAndSetAPIKey > hasExpired : " + Calendar.getInstance().after(expiry));

		log.debug("checkAndSetAPIKey << ");
	}

	private static String newToken() throws Exception {
		log.debug("newToken >> ");
		String keyFile = System.getProperty("jwt.keyfile");
		log.debug("The key file is >> " + keyFile);
		InputStreamReader keyfileReader = new InputStreamReader(new FileInputStream(keyFile));
		RSAPrivateKey privateKey = readPrivateKey(keyfileReader);
		expiry = Calendar.getInstance();
		expiry.set(Calendar.MINUTE, expiry.get(Calendar.MINUTE) + 5);
		String compactJWT = Jwts.builder().setSubject("dashboard.ap.bps.uk.atos.net").setAudience("aegon.co.uk")
				.setExpiration(expiry.getTime()).setIssuedAt(new Date()).setId("atosuser")
				.signWith(SignatureAlgorithm.RS512, privateKey).compact();
		log.debug("newToken << " + compactJWT);
		return compactJWT;
	}

	private static RSAPrivateKey readPrivateKey(Reader keyfileReader) throws Exception {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		try (PemReader pemReader = new PemReader(keyfileReader)) {
			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
			return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
		}
	}
}
