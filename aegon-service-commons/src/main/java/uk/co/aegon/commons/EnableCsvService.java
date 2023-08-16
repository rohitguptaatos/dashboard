package uk.co.aegon.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import uk.co.aegon.commons.service.CsvService;
import uk.co.aegon.commons.service.EmailServiceSpringMailImpl;
import uk.co.aegon.commons.service.EmailServiceStubImpl;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({CsvService.class})
public @interface EnableCsvService  {

}
