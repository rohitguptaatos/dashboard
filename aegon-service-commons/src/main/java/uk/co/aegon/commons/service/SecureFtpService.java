package uk.co.aegon.commons.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
@Service("sFTPService")
@Slf4j
public class SecureFtpService  {
	
	private String ftpFilesUrl;
	
	private String ftpFilesUser;
	
	private String ftpFilesPassword;
	
	private Integer ftpFilesPort;
	
	private Boolean simulateSftp;
	
	public Boolean sftpToServer(File localFile, String targetServerPath,String targetFileName,
			String filesUrl, String filesUser,String filesPassword, int filesPort, boolean simulateSftp) {
		this.ftpFilesUrl = filesUrl;
		this.ftpFilesUser = filesUser;
		this.ftpFilesPassword = filesPassword;
		this.ftpFilesPort = filesPort;
		this.simulateSftp = simulateSftp;
		return sftpToServer(localFile, targetServerPath,targetFileName);
	}

	public Boolean ftpToServer(File localFile, String targetServerPath,String targetFileName,
			String filesUrl, String filesUser,String filesPassword, int filesPort, boolean simulateSftp) {
		this.ftpFilesUrl = filesUrl;
		this.ftpFilesUser = filesUser;
		this.ftpFilesPassword = filesPassword;
		this.ftpFilesPort = filesPort;
		this.simulateSftp = simulateSftp;
		return ftpToServer(localFile, targetServerPath,targetFileName);
	}

	/*
	 * Method to insert files on FTP server
	 */
	public Boolean sftpToServer(File localFile, String targetServerPath,String targetFileName) {
		Boolean uploaded = true;
	    String sftWorkingDir = targetServerPath;
	    log.debug("preparing the host information for sftp.");
		if(simulateSftp){
			File destFile = new File(sftWorkingDir, targetFileName);
			try {
				FileUtils.copyFile(localFile, destFile);
			} catch (IOException e) {
				uploaded = false;
		    	log.error("Exception found while local transfer.");
			}
		} else {
			Session session = null;
		    Channel channel = null;
		    ChannelSftp channelSftp = null;	   
		    try {
		        JSch jsch = new JSch();
		        session = jsch.getSession(ftpFilesUser, ftpFilesUrl, ftpFilesPort);
		        session.setPassword(ftpFilesPassword);
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        log.debug("Host connected.");
		        channel =  session.openChannel("sftp");
		        channel.connect();
		        log.debug("sftp channel opened and connected.");
		        channelSftp = (ChannelSftp) channel;
		        
		        if (sftWorkingDir!=null && !sftWorkingDir.isEmpty()) {
		        	try {
		        		channelSftp.cd(sftWorkingDir);
		        	} catch(Exception ex) {
		        		channelSftp.mkdir(sftWorkingDir);
		        		channelSftp.cd(sftWorkingDir);
		        	}
		        }
		        channelSftp.put(new FileInputStream(localFile), targetFileName);
		    } catch (Exception ex) {
		    	uploaded = false;
		    	log.error("Exception found while transfer the response.");
		    }
		    finally{
		        channelSftp.exit();       
		        channel.disconnect();        
		        session.disconnect();       
		    }
		}
	    
		return uploaded;
	}

	public Boolean ftpToServer(File localFile, String targetServerPath,String targetFileName) {
		Boolean uploaded = true;
	    String sftWorkingDir = targetServerPath;
	    log.debug("preparing the host information for sftp.");
		if(simulateSftp){
			File destFile = new File(sftWorkingDir, targetFileName);
			try {
				FileUtils.copyFile(localFile, destFile);
			} catch (IOException e) {
				uploaded = false;
		    	log.error("Exception found while local transfer.");
			}
		} else {
			FTPClient client = null;
		    try {
		    	
		    	client = new FTPClient();

		    	int port = 21;
		    	client.connect(this.ftpFilesUrl,port);
		    	client.login(this.ftpFilesUser, this.ftpFilesPassword);
		    	//java.util.Properties config = new java.util.Properties();
		        log.debug("Host connected.");
		        if (sftWorkingDir!=null && !sftWorkingDir.isEmpty()) {
		        	try {
		        		client.changeWorkingDirectory(sftWorkingDir);
		        	} catch(Exception ex) {
		        		client.mkd(sftWorkingDir);
		        		client.changeWorkingDirectory(sftWorkingDir);
		        	}
		        }
		        client.storeFile(targetFileName, new FileInputStream(localFile));
		        //channelSftp.put(new FileInputStream(localFilepath), imageFileName);
		    } catch (Exception ex) {
		    	ex.printStackTrace();
		    	uploaded = false;
		    	System.out.println("FTP Client Exception");
		    	log.error("Exception found while transfer the response.");
		    }
		    finally{
		    	if (client!=null && client.isConnected()) {
		    		try {
		    			client.disconnect();
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
		    	}
		    }
		}
	    
		return uploaded;
	}

	/* Method to copy and paste a file  on the remote server
	 */
	public Boolean sftpCopyFromToServer(String sourceFileName, String sourceFilePath,String targetServerPath,String targetFileName) throws IOException {
		Boolean uploaded = true;
	    String sftWorkingDir = targetServerPath;
	    String sftExistingDir = sourceFilePath;
		if(simulateSftp){
			try {
				File source = new File(sftExistingDir,sourceFileName);
				File dest = new File(sftWorkingDir,targetFileName);
				FileUtils.copyFile(source, dest);
			} catch (IOException e) {
				uploaded = false;
		    	log.error("Exception found while local transfer the response.");
			}
		} else {
		    Session session = null;
		    Channel channel = null;
		    ChannelSftp channelSftp = null;
		    InputStream inputStream = null;
		    log.debug("preparing the host information for sftp.");
		    try {
		        JSch jsch = new JSch();
		        session = jsch.getSession(ftpFilesUser, ftpFilesUrl, ftpFilesPort);
		        session.setPassword(ftpFilesPassword);
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        log.debug("Host connected.");
		        channel =  session.openChannel("sftp");
		        channel.connect();
		        log.debug("sftp channel opened and connected.");
		        channelSftp = (ChannelSftp) channel;
		        try {
		        	channelSftp.cd(sftExistingDir);
		        } catch(Exception ex) {
		        	channelSftp.mkdir(sftExistingDir);
		        	channelSftp.cd(sftExistingDir);
		        }
		         inputStream = channelSftp.get(sourceFileName);
		         pasteToServer(inputStream,sftWorkingDir,targetFileName);
		    } catch (Exception ex) {
		    	uploaded = false;
		    	log.error("Exception found while transfer the response.");
		    }	   
		    finally{
		    	log.debug("sftp closed.");
		    	inputStream.close();
		        channelSftp.exit();       
		        channel.disconnect();        
		        session.disconnect();       
		    }
		}
	    return uploaded;
	}
	
	private Boolean pasteToServer(InputStream sourceFilepath, String targetServerPath,String targetFileName) {
		Boolean uploaded = true;
	    String sftpExistingDir = targetServerPath;

	    Session session = null;
	    Channel channel = null;
	    ChannelSftp channelSftp = null;
	    log.debug("preparing the host information for sftp.");
	    try {
	        JSch jsch = new JSch();
	        session = jsch.getSession(ftpFilesUser, ftpFilesUrl, ftpFilesPort);
	        session.setPassword(ftpFilesPassword);
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        log.debug("Host connected.");
	        channel =  session.openChannel("sftp");
	        channel.connect();
	        log.debug("sftp channel opened and connected.");
	        channelSftp = (ChannelSftp) channel;
	        try {
	        	channelSftp.cd(sftpExistingDir);
	        } catch(Exception ex) {
	        	channelSftp.mkdir(sftpExistingDir);
	        	channelSftp.cd(sftpExistingDir);
	        }
	        channelSftp.put(sourceFilepath, targetFileName);
	    } catch (Exception ex) {
	    	uploaded = false;
	    	log.error("Exception found while transfer the response.");
	    }	   
	    finally{
	        channelSftp.exit();       
	        channel.disconnect();        
	        session.disconnect();       
	    }
	    return uploaded;
	}
	
	/*Method to retrieve files from remote FTP server
	 */
	public Boolean retrieveFileFromServer(String sourceFileName, String sourceServerPath, String localDestPath) throws IOException {
		Boolean uploaded = true;
	    String sftExistingDir = sourceServerPath;
	    if (simulateSftp) {
			try {
				File source = new File(sftExistingDir,sourceFileName);
				File dest = new File(localDestPath,sourceFileName);
				FileUtils.copyFile(source, dest);
			} catch (IOException e) {
				uploaded = false;
		    	log.error("Exception found while local transfer.");
			}
		} else {
		    Session session = null;
		    Channel channel = null;
		    ChannelSftp channelSftp = null;
		    log.debug("preparing the host information for sftp.");
		    try {
		        JSch jsch = new JSch();
		        session = jsch.getSession(ftpFilesUser, ftpFilesUrl, ftpFilesPort);
		        session.setPassword(ftpFilesPassword);
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        log.debug("Host connected.");
		        channel =  session.openChannel("sftp");
		        channel.connect();
		        log.debug("sftp channel opened and connected.");
		        channelSftp = (ChannelSftp) channel;
		        try {
		        	channelSftp.cd(sftExistingDir);
		        } catch(Exception ex) {
		        	uploaded = false;
			    	log.error("File doesnt exists.");
		        }
		        log.debug(channelSftp.pwd());
		         channelSftp.get(sourceFileName,localDestPath+sourceFileName);
		        
		    } catch (Exception ex) {
		    	uploaded = false;
		    	log.error("Exception found while tranfer the response to " +localDestPath+sourceFileName);
		    }	   
		    finally{
		    	log.debug("sftp closed.");
		        channelSftp.exit();       
		        channel.disconnect();        
		        session.disconnect();       
		    }
		}
	    return uploaded;
	}
	public Boolean removeFileFromServer(String targetServerPath,String targetFileName) {
		Boolean uploaded = true;
	    String sftWorkingDir = targetServerPath;	    
	    log.debug("preparing the host information for sftp.");
		if(simulateSftp){
			File destFile = new File(targetServerPath, targetFileName);
			FileUtils.deleteQuietly(destFile);
		} else {
			Session session = null;
		    Channel channel = null;
		    ChannelSftp channelSftp = null;	   
		    try {
		        JSch jsch = new JSch();
		        session = jsch.getSession(ftpFilesUser, ftpFilesUrl, ftpFilesPort);
		        session.setPassword(ftpFilesPassword);
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        log.debug("Host connected.");
		        channel =  session.openChannel("sftp");
		        channel.connect();
		        log.debug("sftp channel opened and connected.");
		        channelSftp = (ChannelSftp) channel;
		        try {
		        	channelSftp.cd(sftWorkingDir);
		        } catch(Exception ex) {
		        	log.error("Directory doesnt exist, file cannot be removed.");
		        	uploaded = false;
		        	return uploaded;
		        }
		        channelSftp.rm(targetServerPath + targetFileName);
		    } catch (Exception ex) {
		    	uploaded = false;
		    	log.error("Exception found while removing the file.");
		    }
		    finally{
		        channelSftp.exit();       
		        channel.disconnect();        
		        session.disconnect();       
		    }
		}
	    
		return uploaded;
	}

}
