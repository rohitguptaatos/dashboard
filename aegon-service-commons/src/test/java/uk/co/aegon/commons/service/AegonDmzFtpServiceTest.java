package uk.co.aegon.commons.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AegonDmzFtpServiceTest {
	
	AegonDmzFtpService aegonDmzFtpService;
	
    boolean ftpEnabled = true;
    boolean ftpSimulate = true;
    String ftpURL = "xxx";
	String ftpUser = "xxx";
	String ftpPassword = "xxx";
	String downloadPath = "out\\testing\\useragency\\";
	String uploadPath = "in\\testing\\useragency\\";
	String localPath = "\\dashboard\\ftplocal";
	String ftpSimulateRootDirectory = "\\dashboard\\ftpremote\\";			
	
	@BeforeEach
	void setUp() {
		aegonDmzFtpService = new AegonDmzFtpService(
				ftpURL,
				ftpUser,
				ftpPassword,
				downloadPath,
				uploadPath,
				localPath,
				ftpSimulateRootDirectory,			
				ftpSimulate);

		InputStream sourceFileStream = null;
		try {
			sourceFileStream = new ClassPathResource("user_agency.csv").getInputStream();
			File destination = new File(this.ftpSimulateRootDirectory + this.uploadPath + "user_agency.csv");
			FileUtils.copyInputStreamToFile(sourceFileStream, destination);
			sourceFileStream.close();
			
			sourceFileStream = new ClassPathResource("user_agency_2.csv").getInputStream();			
			destination = new File(this.ftpSimulateRootDirectory + this.uploadPath + "user_agency_2.csv");
			FileUtils.copyInputStreamToFile(sourceFileStream, destination);
			sourceFileStream.close();
		} catch (IOException e) {
			if (sourceFileStream!=null) {
				try {
					sourceFileStream.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			log.error("Unable to load resource test files >> " + e.getMessage());
			e.printStackTrace();
		}		
		
	}
	
	
	@Test
	void testDownloadFilesFromFTPDownloadPathSimulate() {
		log.trace("testDownloadFilesFromFTPDownloadPathSimulate started");
		System.out.println("test started");
		List<File> files = null;
		try {
			files = this.aegonDmzFtpService.downloadFilesFromFTPDownloadPathSimulate("user_agenc");
			log.trace("Files.size="+files.size());
			System.out.println("Files.size="+files.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assert(files.size()>0);
		//fail("Not yet implemented");
	}

	@Test
	void testDownloadFilesFromFTPUploadPathSimulate() {
		log.trace("testDownloadFilesFromFTPUploadPathSimulate started");
		System.out.println("test upload started");
		List<File> files = null;
		try {
			files = this.aegonDmzFtpService.downloadFilesFromFTPUploadPathSimulate("user_agenc");
			log.trace("Files.size="+files.size());
			System.out.println("Files.size="+files.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assert(files.size()>0);
	}

	//@Test
	void testUploadFileToFTPSimulate() {
		fail("Not yet implemented");
	}

	//@Test
	void testDeleteFilesFromFTPDownloadPathSimulate() {
		fail("Not yet implemented");
	}

	//@Test
	void testDeleteFilesFromFTPUploadPathSimulate() {
		fail("Not yet implemented");
	}

}
