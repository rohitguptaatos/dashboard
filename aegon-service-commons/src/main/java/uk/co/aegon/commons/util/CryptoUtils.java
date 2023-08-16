package uk.co.aegon.commons.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.net.util.Base64;



public class CryptoUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";
	private static final char[] SECRET_KEY = {'P','K','B','S','E','C','K','E','Y'};
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    private static final int ITERATIONS = 34;

    /*  Phased out - using Jasypt and SpringBoot encryption
	public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET_KEY));
        Cipher pbeCipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT,ITERATIONS ));
        return Base64.encodeBase64String(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

	public static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET_KEY));
        Cipher pbeCipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATIONS));
        return new String(pbeCipher.doFinal(Base64.decodeBase64(property)), "UTF-8");
    }
*/	

}
