package lj.epub.sd;

import java.io.File;

import org.apache.log4j.Logger;

import bro1.mine.FTPConfiguration;
import bro1.mine.utils.FtpUtil;
import bro1.utils.log.Log;

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
