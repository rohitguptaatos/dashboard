package uk.co.aegon.security.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import uk.co.aegon.security.client.impl.UserServiceImpl;
import uk.co.aegon.security.client.impl.UserServiceStub;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({UserServiceStub.class, UserServiceImpl.class})
public @interface EnableAtosLDAPService  {

}
