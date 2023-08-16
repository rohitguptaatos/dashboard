package uk.co.aegon.security.client.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import uk.co.aegon.security.client.UserInfoResponse;

public class UserServiceStubTest {
	@Test
	public void testUserServiceStub() {
		UserServiceStub stub = new UserServiceStub();
		UserInfoResponse userInfo = stub.retrieveUserInfo("GP9RBH");
		assertEquals("IFA", userInfo.getUsers().get(0).getForename());
	}
	@Test
	public void testUserServiceStub_FRN() {
		UserServiceStub stub = new UserServiceStub();
		UserInfoResponse userInfo = stub.retrieveUserInfo("GP9RBH");
		assertEquals("799100", userInfo.getUsers().get(0).getFRN());
	}
}
