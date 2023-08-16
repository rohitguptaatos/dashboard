package uk.co.aegon.commons.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;

import com.opencsv.CSVWriter;

import uk.co.aegon.commons.dto.EmailEventDTO;
import uk.co.aegon.commons.dto.MIEmailEvent;
import uk.co.aegon.commons.dto.PolicyEmailDTO;

class CsvServiceTest {

	//@Test
	void testImportCsvToStringArrayListReaderStringArray() {
		fail("Not yet implemented");
	}

	//@Test
	void testImportCsvToStringArrayListReaderStringArrayCharCharInt() {
		fail("Not yet implemented");
	}

	//@Test
	void testImportCsvToBeanReaderClassStringArray() {
		fail("Not yet implemented");
	}

	//@Test
	void testImportCsvToBeanReaderClassStringArrayCharCharInt() {
		fail("Not yet implemented");
	}

	//@Test
	void testImportCsvToDatabase() {
		fail("Not yet implemented");
	}
	
	@Test
	void testExportBeanToCsv() {
		CsvService csvService = new CsvService();
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(bos);
			
			List<PolicyEmailDTO> beanRows = new ArrayList<PolicyEmailDTO>();
			PolicyEmailDTO e = new PolicyEmailDTO();
			e.setPolicyRef("Ref1");
			e.setClient1Forename("client1Forename_1");
			e.setClient1Surname("client1Surname_1");
			e.setClient2Forename("client2Forename_1");
			e.setClient2Surname("client2Surname_1");
			
			PolicyEmailDTO e2 = new PolicyEmailDTO();
			e2.setPolicyRef("Ref2");
			e2.setClient1Forename("client1Forename_1");
			e2.setClient1Surname("client1Surname_1");
			e2.setClient2Forename("client2Forename_1");
			e2.setClient2Surname("client2Surname_1");

			beanRows.add(e);
			beanRows.add(e2);
			
			char fieldSeparatorType = CSVWriter.DEFAULT_SEPARATOR;
			char fieldQuoteType = CSVWriter.DEFAULT_QUOTE_CHARACTER;
			char defaultEscapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
			String defaultLineEnd = CSVWriter.DEFAULT_LINE_END;
			
			 String[] beanFields = new String[] 
	                    { "policyRef", "client1Forename", "client1Surname", "client2Forename", "client2Surname" };
			csvService.exportBeanToCsv(writer, PolicyEmailDTO.class, beanFields, beanRows, fieldSeparatorType, fieldQuoteType, defaultEscapeCharacter, defaultLineEnd);
		
			System.out.println("CsvOut="+bos.toString());
			
			// Then convert to byteArray and send to email as attachment
			/*ByteArrayResource bytes = new ByteArrayResource(bos.toByteArray());
			
			MIEmailMessageDTO emailMessage = new MIEmailMessageDTO();
			ByteArrayResource bytes = new ByteArrayResource(out.toByteArray());
			emailMessage.setAttachment(bytes);
			emailMessage.setAttachmentName("policyStartdateReport.csv");
			emailMessage.setToAddresses(new String[] {toAddress});
			emailMessage.setFromAddress(fromAddress);
			emailMessage.setSubject("policyStartdateReport");
			emailMessage.setContent("policyStartdateReport");
						
			ModelMapper mapper = new ModelMapper();
			MIEmailEvent emailEvent = mapper.map(emailMessage, MIEmailEvent.class);
			emailEvent.setAttachment(bytes.getByteArray());
			try {
				emailService.sendEmail(emailEvent);
			} catch (Exception e) {
				log.error("deliver << Report failed to send", e);
			}
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
