package uk.co.aegon.commons.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.co.aegon.commons.util.CryptoUtils;

@Slf4j
@Component
public abstract class FtpService {
		
	protected String ftpURL;
	protected String ftpUser;
	protected String ftpPassword;
	protected String downloadPath;
	protected String uploadPath;
	protected String localPath;
	protected boolean ftpSimulate;
	protected String ftpSimulateRootDirectory;
	
	private static String decodedPassword = null;
	
	public FtpService() {};
	
	public FtpService(
			String ftpURL, 
			String ftpUser,
			String ftpPassword,
			String downloadPath,
			String uploadPath,
			String localPath,
			String ftpSimulateRootDirectory,
			boolean ftpSimulate) {
			
		this.ftpURL = ftpURL;
		this.ftpUser = ftpUser;
		this.ftpPassword = ftpPassword;
		this.downloadPath = downloadPath;
		this.uploadPath = uploadPath;
		this.localPath = localPath;
		this.ftpSimulate = ftpSimulate;
		this.ftpSimulateRootDirectory = ftpSimulateRootDirectory;
		
		log.trace("ftpUrl="+this.ftpURL);
	};

	
	/**
	 * Downloads files form mainframe extract location on FTP, 
	 * downloads the file to the local server
	 * @param fileNamePrefix - if supplied only files with the given prefix 
	 * in their name will be downloaded, if null all files will be downloaded 
	 *
	 * @return list of absolute paths of the files downloaded  from FTP
	 * @throws SocketException
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public List<File> downloadFilesFromFTPDownloadPath(String fileNamePrefix) throws SocketException, IOException, GeneralSecurityException {
		log.trace("downloadFilesFromFTP >> ");
		if (ftpSimulate) {
			return downloadFilesFromFTPDownloadPathSimulate(fileNamePrefix);
		}
		
		List<File> downloadedFiles = null;
		FTPClient ftpClient = connectAndLogin();
		if ( ftpClient == null ) {
			log.trace("Could not connect to FTP server, returning null");
			return downloadedFiles;
		}
		log.trace("Connected & Logged in ");
		ftpClient.changeWorkingDirectory(downloadPath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		
		if ( ftpFiles == null  ) {
			log.debug("No files present in FTP under Mainframe Extract Location !");
			return downloadedFiles;
		} else {
			int length = ftpFiles.length;
			downloadedFiles = new ArrayList<File>(length);
			log.debug("No of files present in FTP under Mainframe Extract Location is "+length);
		}
		for ( FTPFile ftpFile : ftpFiles ) {
			String remoteFileName = ftpFile.getName();
			log.debug("Rmeote file name >> "+remoteFileName);
			if ( !remoteFileName.contains(fileNamePrefix)) continue;
			String localFileName = localPath+ftpFile.getName();
			File localFile = new File(localFileName);
			OutputStream output = new FileOutputStream(localFile);
			ftpClient.retrieveFile(ftpFile.getName(), output);
			output.close();
			downloadedFiles.add(localFile);
		}
		logeOutAndDisconnect(ftpClient);
		log.trace("downloadFilesFromFTP << ");
		return downloadedFiles;
	}
	
	/**
	 * Downloads files from the upload path, ie data thats come in from eCOM, 
	 * downloads the file to the local server
	 * @param fileNamePrefix - if supplied only files with the given prefix 
	 * in their name will be downloaded, if null all files will be downloaded 
	 *
	 * @return list of absolute paths of the files downloaded  from FTP
	 * @throws SocketException
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public List<File> downloadFilesFromFTPUploadPath(String fileNamePrefix) throws SocketException, IOException, GeneralSecurityException {
		log.trace("downloadFilesFromFTPUploadPath >> ");
		if (ftpSimulate) {
			return downloadFilesFromFTPUploadPathSimulate(fileNamePrefix);
		}
		
		List<File> downloadedFiles = null;
		FTPClient ftpClient = connectAndLogin();
		if ( ftpClient == null ) {
			log.trace("Could not connect to FTP server, returning null");
			return downloadedFiles;
		}
		log.trace("Connected & Logged in ");
		ftpClient.changeWorkingDirectory(uploadPath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		
		if ( ftpFiles == null  ) {
			log.debug("No files present in FTP under Mainframe Extract Location !");
			return downloadedFiles;
		} else {
			int length = ftpFiles.length;
			downloadedFiles = new ArrayList<File>(length);
			log.debug("No of files present in FTP under Mainframe Extract Location is "+length);
		}
		for ( FTPFile ftpFile : ftpFiles ) {
			String remoteFileName = ftpFile.getName();
			log.debug("Rmeote file name >> "+remoteFileName);
			if ( !remoteFileName.contains(fileNamePrefix)) continue;
			String localFileName = localPath+ftpFile.getName();
			File localFile = new File(localFileName);
			OutputStream output = new FileOutputStream(localFile);
			ftpClient.retrieveFile(ftpFile.getName(), output);
			output.close();
			downloadedFiles.add(localFile);
		}
		logeOutAndDisconnect(ftpClient);
		log.trace("downloadFilesFromFTPUploadPath << ");
		return downloadedFiles;
	}	
	/**
	 * Upload the given Files to the FTP
	 * the given files should be available under the default localExtractpath
	 * 
	 * @param pathsToFiles list of absolute path to the files that needs to be uploaded
	 * @param deleteLocalCopy - if true will delete the local copy (only when the upload is successfull
	 * @return filenames that weren't uploaded
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public boolean uploadFileToFTP(File localFile, boolean deleteLocalCopy) throws IOException, GeneralSecurityException {
		log.trace("uploadFilesToFTP >> ");
		if (ftpSimulate) {
			return uploadFileToFTPSimulate(localFile, deleteLocalCopy);
		}
		
		FTPClient ftpClient = connectAndLogin();
		
		if ( ftpClient == null ) {
			log.trace("Could not connect to FTP server, returning null");
			return false;
		}
		log.trace("Connected & Logged in ");
		ftpClient.changeWorkingDirectory(uploadPath);
		String localFilename = localFile.getName();
		FileInputStream inputStream = new FileInputStream(localFile);
		boolean isSuccess = ftpClient.storeFile(localFilename, inputStream);
		inputStream.close();
		if ( !isSuccess ) {
			log.error("Error uploading File to FTP >> "+localFilename);
			return false;
		}
		if ( deleteLocalCopy && !localFile.delete() ) {
			log.error("Unable to delete the local copy! "+localFilename);	
		}
		log.debug("Uploaded File to FTP >> "+localFilename);
		logeOutAndDisconnect(ftpClient);
		log.trace("uploadFilesToFTP << ");
		return true;
	}
	
	/**
	 * Delete the files from FTP Server, the files will be deleted
	 * only if they are present under the download path in the FTP server.
	 * 
	 * @param filesToDelete
	 * @return the list of file names that weren't deleted.
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public List<File> deleteFilesFromFTPDownloadPath(List<File> filesToDelete) throws IOException, GeneralSecurityException {
		log.trace("deleteFilesFromFTP >> ");
		if (ftpSimulate) {
			return deleteFilesFromFTPDownloadPathSimulate(filesToDelete);
		}
		
		FTPClient ftpClient = connectAndLogin();
		
		if ( ftpClient == null ) {
			log.trace("Could not connect to FTP server, returning null");
			return filesToDelete;
		}
		log.trace("Connected & Logged in ");
		ftpClient.changeWorkingDirectory(downloadPath);
		List<File> failedDelete = new ArrayList<File>();
		log.trace("deleteFilesFromFTP << ");
		for ( File file : filesToDelete ) {
			if ( !ftpClient.deleteFile(file.getName()) ) {
				log.error("Unable to delete the file from FTP > "+file);
				failedDelete.add(file);
			}
		}
		logeOutAndDisconnect(ftpClient);
		log.trace("deleteFilesFromFTP << ");
		return failedDelete;
	}

	/**
	 * Delete the files from FTP Server, the files will be deleted
	 * only if they are present under the download path in the FTP server.
	 * 
	 * @param filesToDelete
	 * @return the list of file names that weren't deleted.
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public List<File> deleteFilesFromFTPUploadPath(List<File> filesToDelete) throws IOException, GeneralSecurityException {
		log.trace("deleteFilesFromFTPUploadPath >> ");
		if (ftpSimulate) {
			return deleteFilesFromFTPUploadPathSimulate(filesToDelete);
		}
		
		FTPClient ftpClient = connectAndLogin();
		
		if ( ftpClient == null ) {
			log.trace("Could not connect to FTP server, returning null");
			return filesToDelete;
		}
		log.trace("Connected & Logged in ");
		ftpClient.changeWorkingDirectory(uploadPath);
		List<File> failedDelete = new ArrayList<File>();
		log.trace("deleteFilesFromFTP << ");
		for ( File file : filesToDelete ) {
			if ( !ftpClient.deleteFile(file.getName()) ) {
				log.error("Unable to delete the file from FTP > "+file);
				failedDelete.add(file);
			}
		}
		logeOutAndDisconnect(ftpClient);
		log.trace("deleteFilesFromFTPUploadPath << ");
		return failedDelete;
	}	
	
	private void logeOutAndDisconnect(FTPClient ftpClient) throws IOException {
		ftpClient.logout();
		ftpClient.disconnect();
		log.trace("logeOutAndDisconnect >><< ");

	}

	private FTPClient connectAndLogin() throws IOException, GeneralSecurityException {
		log.trace("connectAndLogin >> ");
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(ftpURL);
		// SPP 3.3.2022 - replaced password decryption to use Jasypt solution
		if ( decodedPassword == null ) {
			//decodedPassword = CryptoUtils.decrypt(ftpPassword);
			decodedPassword = ftpPassword;
		}
		if (!ftpClient.login(ftpUser, decodedPassword)) {
			logeOutAndDisconnect(ftpClient);
			log.trace("Error logging into FTP, disconnecting");
			return null;
		}
		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			log.trace("Last comming didnt complete postively, disconnecting "
					+ reply);
			ftpClient.disconnect();
			return null;
		}
		ftpClient.enterLocalPassiveMode();
		return ftpClient;
	}
	
	public void printConfig() {
		log.trace("ftpURL="+this.ftpURL);
	}
	
	public List<File> downloadFilesFromFTPDownloadPathSimulate(String fileNamePrefix) throws SocketException, IOException, GeneralSecurityException {
		log.trace("downloadFilesFromFTPDownloadPathSimulate >> ");
		List<File> downloadedFiles = null;
		
		String[] extensions = {"csv"};
		File directory = new File(this.ftpSimulateRootDirectory + downloadPath);
		boolean recursive = false;
		List<File> files = (List<File>) FileUtils.listFiles(directory, extensions, recursive);
		
		if ( files == null  ) {
			log.debug("No files present in FTP under Mainframe Extract Location !");
			return files;
		} else {
			int length = files.size();
			System.out.println("files.sizeX="+length);
			downloadedFiles = new ArrayList<File>(length);
			log.debug("No of files present in FTP under Mainframe Extract Location is "+length);
		}
		for ( File file : files ) {
			String remoteFileName = file.getName();
			log.debug("Rmeote file name >> "+remoteFileName);
			if ( !remoteFileName.contains(fileNamePrefix)) continue;
			downloadedFiles.add(file);
		}
		log.trace("downloadFilesFromFtpDownloadPathSimulate << ");
		return downloadedFiles;
	}
	public List<File> downloadFilesFromFTPUploadPathSimulate(String fileNamePrefix) throws SocketException, IOException, GeneralSecurityException {
		log.trace("downloadFilesFromFTPUploadPathSimulate >> ");
		List<File> downloadedFiles = null;
		
		String[] extensions = {"csv"};
		File directory = new File(this.ftpSimulateRootDirectory + uploadPath);
		boolean recursive = false;
		List<File> files = (List<File>) FileUtils.listFiles(directory, extensions, recursive);
		
		if ( files == null  ) {
			log.debug("No files present in FTP under Mainframe Extract Location !");
			return files;
		} else {
			int length = files.size();
			downloadedFiles = new ArrayList<File>(length);
			log.debug("No of files present in FTP under Mainframe Extract Location is "+length);
		}
		for ( File file : files ) {
			String remoteFileName = file.getName();
			log.debug("Rmeote file name >> "+remoteFileName);
			if ( !remoteFileName.contains(fileNamePrefix)) continue;
			downloadedFiles.add(file);
		}
		log.trace("downloadFilesFromFtpUploadPathSimulate << ");
		return downloadedFiles;
	}
	
	public boolean uploadFileToFTPSimulate(File localFile, boolean deleteLocalCopy) throws IOException, GeneralSecurityException {
		log.trace("uploadFilesToFTPSimulate >> ");
		String localFilename = localFile.getName();

		File destFile = new File(this.ftpSimulateRootDirectory + this.uploadPath + localFilename);
		
		FileUtils.copyFile(localFile, destFile);
		
		if (deleteLocalCopy) {
			if (!FileUtils.deleteQuietly(localFile) ) {
				log.error("Unable to delete the local copy! "+localFilename);	
			}
		}
		log.debug("Uploaded File to FTP >> "+localFilename);
		log.trace("uploadFilesToFTPSimulate << ");
		return true;
	}
	
	public List<File> deleteFilesFromFTPDownloadPathSimulate(List<File> filesToDelete) throws IOException, GeneralSecurityException {
		log.trace("deleteFilesFromFTPDownloadPathSimulate >> ");
		List<File> failedDelete = new ArrayList<File>();
		log.trace("deleteFilesFromFTP << ");
		for ( File file : filesToDelete ) {
			if ( !FileUtils.deleteQuietly(file) ) {
				log.error("Unable to delete the file from FTP > "+file);
				failedDelete.add(file);
			}
		}
		log.trace("deleteFilesFromFTPDownloadPathSimulate << ");
		return failedDelete;
	}
	public List<File> deleteFilesFromFTPUploadPathSimulate(List<File> filesToDelete) throws IOException, GeneralSecurityException {
		log.trace("deleteFilesFromFTPUploadPathSimulate >> ");
		List<File> failedDelete = new ArrayList<File>();
		log.trace("deleteFilesFromFTP << ");
		for ( File file : filesToDelete ) {
			if ( !FileUtils.deleteQuietly(file) ) {
				log.error("Unable to delete the file from FTP > "+file);
				failedDelete.add(file);
			}
		}
		log.trace("deleteFilesFromFTPUploadPathSimulate << ");
		return failedDelete;
	}	
}
