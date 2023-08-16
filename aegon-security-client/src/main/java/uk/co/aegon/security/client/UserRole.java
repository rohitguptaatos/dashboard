package uk.co.aegon.security.client;

public enum UserRole {

	IFA, @Deprecated
	IFA_ADMIN, HO_USER;

	public static UserRole fromRole(String value) {
		switch (value.toUpperCase()) {
		case "IFA":
			return IFA;
		case "DC_IFA":
			return IFA;
		case "IFAADMIN":
			return IFA_ADMIN;
		case "SE":
			return HO_USER;
		case "SE_IFA":
			return HO_USER;
		case "HOUSER":
			return HO_USER;
		default:
			return IFA;
		}
	}

}