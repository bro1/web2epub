package lj.epub.sd;

import java.io.File;

import lj.utils.log.Log;
import lj.utils.net.ftp.FTPConfiguration;
import lj.utils.net.ftp.FtpUtil;

import org.apache.log4j.Logger;


public class FTPPublisher {

	Logger logger = Log.getLogger(FTPPublisher.class);

	public void publish(String category, File file) throws Exception {
		FtpUtil ftpUtil = new FtpUtil();
		FTPConfiguration ftp = new FTPConfiguration();
		if (ftp.isEnabled()) {			
			ftpUtil.upload(ftp, category, file);
		} else {
			logger.debug("Not uploading anything to FTP - the FTP is disabled as the property ftp.servername is empty");
		}
	}

}
