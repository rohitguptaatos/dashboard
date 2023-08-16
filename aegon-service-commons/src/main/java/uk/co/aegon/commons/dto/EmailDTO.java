package uk.co.aegon.commons.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class EmailDTO implements Serializable {
	private static final long serialVersionUID = -5266442292599571947L;
	public static final String SERIALIZED_NAME_POLICY_NUMBER = "policyNumber";
	public static final String SERIALIZED_NAME_UAN = "uan";
	public static final String SERIALIZED_NAME_USERID = "userid";
	
	@SerializedName(SERIALIZED_NAME_POLICY_NUMBER)
	private String policyNumber;

	@SerializedName(SERIALIZED_NAME_UAN)
	private String uan;
	
	@SerializedName(SERIALIZED_NAME_USERID)
	private String userId;

}
