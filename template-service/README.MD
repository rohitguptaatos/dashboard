# Template Service

This Template Service holds the main configuration pieces each of the other microservices will need in order to coordinate with the microservice architecture. There is also sample code for such things as communicating with other services whilst using circuit breakers via the API gateway, and also standard examples for using Spring REST Controllers and Spring Data for persistence.   

For a description of each of the elements of the microservice architecture please see the README of the Dashboard_Java project, or for a more in-depth overview of each piece I highly recommend Spring Microservices in Action from Manning.

## Required Configuration

### Spring Profiles
As you work on your microservice you will probably not want to worry too much about cross cutting concerns such as logging, security, service discovery, etc. For this reason, these features will be disabled in the "dev" spring profile. If you wish, you can still enable these features by running any other profile, as long as you exclude "dev".   
Hence you will have:   
 - application.yml (for common properties)
 - application-dev.yml (for dev properties that exclude microservice architecture features)
 - application-test.yml (for test properties that include microservice architecture features)

You will also notice that some configurations are annotated wil @Profile("!dev") in order to exclude them from the dev profile.

### pom.xml
The pom.xml in the template service is the minimum you would expect each service to have. Be aware that:
  - it extends our custom parent pom and NOT the spring starter pom. This means that we lose the plugin management and need to add it back ourselves with the spring-boot-maven-plugin as shown in the pom.
 - if we change our logging stack some of the dependencies may change, although you will be notified if this happens.

 ### package uk.co.aegon.template.config
 This package contains configuration files. Please copy all of them to your own microservice, you don't need to change them and you can put them in any package you prefer.   
 They are:
  - DefaultSecurityConfiguration: sets up your microservice as an OAuth2 resource server.
  - EurekaClientConfiguration: for service discovery
  - RibbonRestTemplateConfiguration: for using the circuit breaker pattern (via HystrixCommand annotations)

 ### application-dev.yml
 The properties in here must be present in your own microservice if you wish zipkin and eureka not to log lots of errors on startup since you are running your service standalone

 ### application-test.yml
 Please copy all of these properties into your own application-test.yml. Note in particular that the server port is set to 0, this sets the port to a random available port.

 ### bootstrap.yml
 Your bootstrap.yml should contain a property like this:
 ```
 spring:
   application:
     name: myservice
 ```
Please use a short name for your service. This is what will be used for service discovery and will be the first part of the url after the port in calls to your service.
Names should be infinitives such as: template, quote, document, track, etc.   
It is also needed by other services when they call your service so make sure if you change it you let people know.

### public.pem
Copy src/main/resources/public.pem and put it in your own src/main/resources

## Sample Code

### SampleController
Contains a number of sample controller methods to demonstrate JSON calls.

### PolicyTrackingDiscoveryClient
Demonstrates how to call another service using service discovery and a circuit breaker.

## Creating Your Own Service

- Copy and paste the template-service directory
- Rename the directory. Here we will use "myproj-service"
- Delete the .project, .factorypath, .classpath files if they exist. And the .settings/ folder if it exists.
- Open your service's pom.xml file and edit the artifact, name, and description appropriately. For example:
```
  <artifactId>myproj-service</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>MyProj Microservice</name>
	<description>MyProj Microservice for IFA Dashboard</description>
```
- Add your service to the bottome of the list of projects in the master pom.xml
- Import the project in to STS using Import->Existing Maven Project...
- Change the spring.application.name property in src/main/resources/bootstrap.yml to be your service name. Do not use hyphens in this name. Try and use a noun or verb infinitive such as "email" rather than "emailer" or "emailing" so that we have nice looking URLs and package names.
- Change the package uk.co.aegon.template to your new name. For example: uk.co.aegon.myproj
- Change the main class from TemplateMicroserviceApplication to your new name e.g. MyProjMicroserviceApplication.