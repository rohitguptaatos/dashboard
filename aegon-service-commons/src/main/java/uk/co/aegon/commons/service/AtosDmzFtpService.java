package uk.co.aegon.commons.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("atosDmzFtpService")
@Slf4j
public class AtosDmzFtpService  {

	// Paths will be the rooot /in  and /out drectories
	// Specific cases then added ie csat(=watermelon)
	
	//public AtosDmzFtpService() {
	//};
/*	
	public AtosDmzFtpService(
			@Value("${atos.atosDmz.ftp.ftpUrl:empty}") String ftpURL,
			@Value("${atos.atosDmz.ftp.ftpUser:empty}") String ftpUser,
			@Value("${atos.atosDmz.ftp.ftpPassword:empty}") String ftpPassword,
			@Value("${atos.atosDmz.ftp.ftpDownloadPath:empty}") String downloadPath,
			@Value("${atos.atosDmz.ftp.ftpUploadPath:empty}") String uploadPath,
			@Value("${atos.atosDmz.ftp.ftpLocalPath:empty}") String localPath,
			@Value("${atos.aegDmz.ftp.ftpSimulate:empty}") boolean ftpSimulate) {			
					
		super(ftpURL, ftpUser, ftpPassword, downloadPath, uploadPath, localPath, ftpSimulate);
		
	};
*/	
}
