# Aegon Security Client

Contains files for looking up user information from LDAP. This library contains a stub for ldap operations as well as the real implementation. 
The stub is used in all cases except when the "preprod" or "prod" profiles are active.
In order for the correct implementation of the UserService class to be injected for you via @Autowire or whatever, please add the @EnableAtosLDAPService annotation to some class in your configuration (usually this is added to your main SpringBootApplication).