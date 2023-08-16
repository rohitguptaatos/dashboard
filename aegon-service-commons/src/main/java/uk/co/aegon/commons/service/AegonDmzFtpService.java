package uk.co.aegon.commons.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("aegonDmzFtpService")
public class AegonDmzFtpService extends FtpService {

	// Paths will be the rooot /in  and /out drectories
	// Specific cases then added ie csat(=watermelon)
	
	//public AegonDmzFtpService() {
	//};
	
	@Value("${atos.aegDmz.ftp.ftpUrl:empty}") String ftpURL2;
	
	public AegonDmzFtpService(
			@Value("${atos.aegDmz.ftp.ftpUrl:empty}") String ftpURL,
			@Value("${atos.aegDmz.ftp.ftpUser:empty}") String ftpUser,
			@Value("${atos.aegDmz.ftp.ftpPassword:empty}") String ftpPassword,
			@Value("${atos.aegDmz.ftp.ftpDownloadPath:empty}") String downloadPath,
			@Value("${atos.aegDmz.ftp.ftpUploadPath:empty}") String uploadPath,
			@Value("${atos.aegDmz.ftp.ftpLocalPath:empty}") String localPath,
			@Value("${atos.aegDmz.ftp.ftpSimulateRootDirectory://ftproot}") String ftpSimulateRootDirectory,			
			@Value("${atos.aegDmz.ftp.ftpSimulate:false}") boolean ftpSimulate) {
					
		super(ftpURL, ftpUser, ftpPassword, downloadPath, uploadPath, localPath, ftpSimulateRootDirectory, ftpSimulate);
		
	};
	
}
